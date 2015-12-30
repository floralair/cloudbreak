package com.sequenceiq.cloudbreak.core.flow2.stack.sync;

import static com.sequenceiq.cloudbreak.common.type.Status.AVAILABLE;
import static com.sequenceiq.cloudbreak.common.type.Status.CREATE_FAILED;
import static com.sequenceiq.cloudbreak.common.type.Status.DELETE_FAILED;
import static com.sequenceiq.cloudbreak.common.type.Status.STOPPED;
import static com.sequenceiq.cloudbreak.common.type.Status.WAIT_FOR_SYNC;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;

import com.sequenceiq.cloudbreak.cloud.event.resource.GetInstancesStateResult;
import com.sequenceiq.cloudbreak.cloud.model.CloudInstance;
import com.sequenceiq.cloudbreak.common.type.HostMetadataState;
import com.sequenceiq.cloudbreak.common.type.InstanceStatus;
import com.sequenceiq.cloudbreak.common.type.ResourceType;
import com.sequenceiq.cloudbreak.common.type.Status;
import com.sequenceiq.cloudbreak.controller.NotFoundException;
import com.sequenceiq.cloudbreak.core.flow2.AbstarctAction;
import com.sequenceiq.cloudbreak.domain.Cluster;
import com.sequenceiq.cloudbreak.domain.HostMetadata;
import com.sequenceiq.cloudbreak.domain.InstanceGroup;
import com.sequenceiq.cloudbreak.domain.InstanceMetaData;
import com.sequenceiq.cloudbreak.domain.Resource;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.repository.HostMetadataRepository;
import com.sequenceiq.cloudbreak.repository.InstanceGroupRepository;
import com.sequenceiq.cloudbreak.repository.InstanceMetaDataRepository;
import com.sequenceiq.cloudbreak.repository.ResourceRepository;
import com.sequenceiq.cloudbreak.repository.StackUpdater;
import com.sequenceiq.cloudbreak.service.cluster.flow.AmbariClusterConnector;
import com.sequenceiq.cloudbreak.service.events.CloudbreakEventService;
import com.sequenceiq.cloudbreak.service.messages.CloudbreakMessagesService;
import com.sequenceiq.cloudbreak.service.stack.flow.InstanceSyncState;

public class StackSyncFinishedAction extends AbstarctAction<StackSyncState, StackSyncEvent, StackSyncContext, GetInstancesStateResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackSyncFinishedAction.class);

    @Inject
    private InstanceGroupRepository instanceGroupRepository;
    @Inject
    private HostMetadataRepository hostMetadataRepository;
    @Inject
    private ResourceRepository resourceRepository;
    @Inject
    private AmbariClusterConnector ambariClusterConnector;
    @Inject
    private StackUpdater stackUpdater;
    @Inject
    private CloudbreakEventService eventService;
    @Inject
    private CloudbreakMessagesService cloudbreakMessagesService;
    @Inject
    private InstanceMetaDataRepository instanceMetaDataRepository;

    public StackSyncFinishedAction() {
        super(GetInstancesStateResult.class);
    }

    @Override
    protected StackSyncContext createFlowContext(StateContext<StackSyncState, StackSyncEvent> stateContext, GetInstancesStateResult payload) {
        return null;
    }

    @Override
    protected void doExecute(StackSyncContext context, GetInstancesStateResult payload, Map<Object, Object> variables) {
        InstanceMetaData instance = null;
        CloudInstance cloudInstance = payload.getRequest().getInstances().get(0);
        for (InstanceMetaData metaData : context.getInstanceMetaData()) {
            if (metaData.getInstanceId().equals(cloudInstance.getInstanceId())) {
                instance = metaData;
                break;
            }
        }
        Stack stack = context.getStack();
        InstanceSyncState state = instance == null ? InstanceSyncState.DELETED : transform(payload.getStatuses().get(0).getStatus());
        if (InstanceSyncState.DELETED.equals(state) && !instance.isTerminated()) {
            syncDeletedInstance(stack, stack.getId(), context.getSyncStateCounts(), instance);
        } else if (InstanceSyncState.RUNNING.equals(state)) {
            syncRunningInstance(stack, stack.getId(), context.getSyncStateCounts(), instance);
        } else if (InstanceSyncState.STOPPED.equals(state)) {
            syncStoppedInstance(stack, stack.getId(), context.getSyncStateCounts(), instance);
        } else {
            context.getSyncStateCounts().put(InstanceSyncState.IN_PROGRESS, context.getSyncStateCounts().get(InstanceSyncState.IN_PROGRESS) + 1);
        }

        // TODO all, !(actualContext instanceof StackScalingContext)
        handleSyncResult(stack, context.getSyncStateCounts(), true);
    }

    @Override
    protected Object getFailurePayload(StackSyncContext flowContext, Exception ex) {
        return null;
    }

    private void syncStoppedInstance(Stack stack, Long stackId, Map<InstanceSyncState, Integer> instanceStateCounts, InstanceMetaData instance) {
        instanceStateCounts.put(InstanceSyncState.STOPPED, instanceStateCounts.get(InstanceSyncState.STOPPED) + 1);
        if (!instance.isTerminated() && !stack.isStopped()) {
            LOGGER.info("Instance '{}' is reported as stopped on the cloud provider, setting its state to STOPPED.", instance.getInstanceId());
            deleteResourceIfNeeded(stackId, instance);
            updateMetaDataToTerminated(stackId, instance);
        }
    }

    private void syncRunningInstance(Stack stack, Long stackId, Map<InstanceSyncState, Integer> instanceStateCounts, InstanceMetaData instance) {
        instanceStateCounts.put(InstanceSyncState.RUNNING, instanceStateCounts.get(InstanceSyncState.RUNNING) + 1);
        if (stack.getStatus() == WAIT_FOR_SYNC && instance.isCreated()) {
            LOGGER.info("Instance '{}' is reported as created on the cloud provider but not member of the cluster, setting its state to FAILED.",
                    instance.getInstanceId());
            instance.setInstanceStatus(InstanceStatus.FAILED);
            instanceMetaDataRepository.save(instance);
            eventService.fireCloudbreakEvent(stackId, CREATE_FAILED.name(),
                    cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_FAILED.code(), Arrays.asList(instance.getDiscoveryFQDN())));
        } else if (!instance.isRunning() && !instance.isDecommissioned() && !instance.isCreated() && !instance.isFailed()) {
            LOGGER.info("Instance '{}' is reported as running on the cloud provider, updating metadata.", instance.getInstanceId());
            createResourceIfNeeded(stack, instance);
            updateMetaDataToRunning(stackId, stack.getCluster(), instance);
        }
    }

    private void syncDeletedInstance(Stack stack, Long stackId, Map<InstanceSyncState, Integer> instanceStateCounts, InstanceMetaData instance) {
        instanceStateCounts.put(InstanceSyncState.DELETED, instanceStateCounts.get(InstanceSyncState.DELETED) + 1);
        deleteHostFromCluster(stack, instance);
        if (!instance.isTerminated()) {
            LOGGER.info("Instance '{}' is reported as deleted on the cloud provider, setting its state to TERMINATED.", instance.getInstanceId());
            deleteResourceIfNeeded(stackId, instance);
            updateMetaDataToTerminated(stackId, instance);
        }
    }

    private void createResourceIfNeeded(Stack stack, InstanceMetaData instance) {
        // TODO metadata.getInstanceResourceType() why null
        ResourceType resourceType = null;
        if (resourceType != null) {
            Resource resource = new Resource(resourceType, instance.getInstanceId(), stack, instance.getInstanceGroup().getGroupName());
            resourceRepository.save(resource);
        }
    }

    private void deleteResourceIfNeeded(Long stackId, InstanceMetaData instance) {
        // TODO metadata.getInstanceResourceType() why null
        ResourceType resourceType = null;
        Resource resource = resourceRepository.findByStackIdAndNameAndType(stackId, instance.getInstanceId(), resourceType);
        if (resource != null) {
            resourceRepository.delete(resource);
        }
    }

    private void deleteHostFromCluster(Stack stack, InstanceMetaData instanceMetaData) {
        try {
            if (stack.getCluster() != null) {
                HostMetadata hostMetadata = hostMetadataRepository.findHostInClusterByName(stack.getCluster().getId(), instanceMetaData.getDiscoveryFQDN());
                if (hostMetadata == null) {
                    throw new NotFoundException(String.format("Host not found with id '%s'", instanceMetaData.getDiscoveryFQDN()));
                }
                if (ambariClusterConnector.isAmbariAvailable(stack)) {
                    if (ambariClusterConnector.deleteHostFromAmbari(stack, hostMetadata)) {
                        hostMetadataRepository.delete(hostMetadata.getId());
                        eventService.fireCloudbreakEvent(stack.getId(), AVAILABLE.name(),
                                cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_HOST_DELETED.code(), Arrays.asList(instanceMetaData.getDiscoveryFQDN())));
                    } else {
                        eventService.fireCloudbreakEvent(stack.getId(), AVAILABLE.name(),
                                cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_REMOVAL_FAILED.code(),
                                        Arrays.asList(instanceMetaData.getDiscoveryFQDN())));
                    }
                } else {
                    hostMetadata.setHostMetadataState(HostMetadataState.UNHEALTHY);
                    hostMetadataRepository.save(hostMetadata);
                    eventService.fireCloudbreakEvent(stack.getId(), AVAILABLE.name(),
                            cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_HOST_UPDATED.code(),
                                    Arrays.asList(instanceMetaData.getDiscoveryFQDN(), HostMetadataState.UNHEALTHY.name())));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Host cannot be deleted from cluster: ", e);
            eventService.fireCloudbreakEvent(stack.getId(), AVAILABLE.name(),
                    cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_TERMINATED.code(), Arrays.asList(instanceMetaData.getDiscoveryFQDN())));
        }
    }

    private void updateMetaDataToTerminated(Long stackId, InstanceMetaData instanceMetaData) {
        InstanceGroup instanceGroup = instanceMetaData.getInstanceGroup();
        instanceGroup.setNodeCount(instanceGroup.getNodeCount() - 1);
        instanceMetaData.setInstanceStatus(InstanceStatus.TERMINATED);
        instanceMetaDataRepository.save(instanceMetaData);
        instanceGroupRepository.save(instanceGroup);
        eventService.fireCloudbreakEvent(stackId, AVAILABLE.name(),
                cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_DELETED_CBMETADATA.code(), Arrays.asList(instanceMetaData.getDiscoveryFQDN())));
    }

    private void updateMetaDataToRunning(Long stackId, Cluster cluster, InstanceMetaData instanceMetaData) {
        InstanceGroup instanceGroup = instanceMetaData.getInstanceGroup();
        instanceGroup.setNodeCount(instanceGroup.getNodeCount() + 1);
        HostMetadata hostMetadata = hostMetadataRepository.findHostInClusterByName(cluster.getId(), instanceMetaData.getDiscoveryFQDN());
        if (hostMetadata != null) {
            LOGGER.info("Instance '{}' was found in the cluster metadata, setting it's state to REGISTERED.", instanceMetaData.getInstanceId());
            instanceMetaData.setInstanceStatus(InstanceStatus.REGISTERED);
        } else {
            LOGGER.info("Instance '{}' was not found in the cluster metadata, setting it's state to UNREGISTERED.", instanceMetaData.getInstanceId());
            instanceMetaData.setInstanceStatus(InstanceStatus.UNREGISTERED);
        }
        instanceMetaDataRepository.save(instanceMetaData);
        instanceGroupRepository.save(instanceGroup);
        eventService.fireCloudbreakEvent(stackId, AVAILABLE.name(),
                cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_RUNNING.code(), Arrays.asList(instanceMetaData.getDiscoveryFQDN())));
    }

    private void handleSyncResult(Stack stack, Map<InstanceSyncState, Integer> instanceStateCounts, boolean stackStatusUpdateEnabled) {
        if (instanceStateCounts.get(InstanceSyncState.UNKNOWN) > 0) {
            eventService.fireCloudbreakEvent(stack.getId(), AVAILABLE.name(),
                    cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_STATUS_COULDNT_DETERMINE.code()));
        } else if (instanceStateCounts.get(InstanceSyncState.IN_PROGRESS) > 0) {
            eventService.fireCloudbreakEvent(stack.getId(), AVAILABLE.name(),
                    cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_OPERATION_IN_PROGRESS.code()));
        } else if (instanceStateCounts.get(InstanceSyncState.RUNNING) > 0 && instanceStateCounts.get(InstanceSyncState.STOPPED) > 0) {
            eventService.fireCloudbreakEvent(stack.getId(), AVAILABLE.name(),
                    cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_HOST_DELETED.code()));
        } else if (instanceStateCounts.get(InstanceSyncState.RUNNING) > 0) {
            updateStackStatusIfEnabled(stack.getId(), AVAILABLE, cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_STATUS_REASON.code()),
                    stackStatusUpdateEnabled);
            eventService.fireCloudbreakEvent(stack.getId(), AVAILABLE.name(),
                    cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_STATE_SYNCED.code()));
        } else if (instanceStateCounts.get(InstanceSyncState.STOPPED) > 0) {
            updateStackStatusIfEnabled(stack.getId(), STOPPED, cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_STATUS_REASON.code()),
                    stackStatusUpdateEnabled);
            eventService.fireCloudbreakEvent(stack.getId(), STOPPED.name(),
                    cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_STATE_SYNCED.code()));
        } else {
            updateStackStatusIfEnabled(stack.getId(), DELETE_FAILED, cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_STATUS_REASON.code()),
                    stackStatusUpdateEnabled);
            eventService.fireCloudbreakEvent(stack.getId(), DELETE_FAILED.name(),
                    cloudbreakMessagesService.getMessage(Msg.STACK_SYNC_INSTANCE_STATE_SYNCED.code()));
        }
    }

    private void updateStackStatusIfEnabled(Long stackId, Status status, String statusReason, boolean stackStatusUpdateEnabled) {
        if (stackStatusUpdateEnabled) {
            stackUpdater.updateStackStatus(stackId, status, statusReason);
        }
    }

    private InstanceSyncState transform(com.sequenceiq.cloudbreak.cloud.model.InstanceStatus instanceStatus) {
        switch (instanceStatus) {
            case IN_PROGRESS:
                return InstanceSyncState.IN_PROGRESS;
            case STARTED:
                return InstanceSyncState.RUNNING;
            case STOPPED:
                return InstanceSyncState.STOPPED;
            case CREATED:
                return InstanceSyncState.RUNNING;
            case FAILED:
                return InstanceSyncState.DELETED;
            case TERMINATED:
                return InstanceSyncState.DELETED;
            default:
                return InstanceSyncState.UNKNOWN;
        }
    }

    private enum Msg {
        STACK_SYNC_STATUS_REASON("stack.sync.status.reason"),
        STACK_SYNC_INSTANCE_STATUS_COULDNT_DETERMINE("stack.sync.instance.status.couldnt.determine"),
        STACK_SYNC_INSTANCE_OPERATION_IN_PROGRESS("stack.sync.instance.operation.in.progress"),
        STACK_SYNC_INSTANCE_STATE_SYNCED("stack.sync.instance.state.synced"),
        STACK_SYNC_HOST_DELETED("stack.sync.host.deleted"),
        STACK_SYNC_INSTANCE_REMOVAL_FAILED("stack.sync.instance.removal.failed"),
        STACK_SYNC_HOST_UPDATED("stack.sync.host.updated"),
        STACK_SYNC_INSTANCE_TERMINATED("stack.sync.instance.terminated"),
        STACK_SYNC_INSTANCE_DELETED_CBMETADATA("stack.sync.instance.deleted.cbmetadata"),
        STACK_SYNC_INSTANCE_RUNNING("stack.sync.instance.running"),
        STACK_SYNC_INSTANCE_FAILED("stack.sync.instance.failed");

        private String code;

        Msg(String msgCode) {
            code = msgCode;
        }

        public String code() {
            return code;
        }
    }
}
