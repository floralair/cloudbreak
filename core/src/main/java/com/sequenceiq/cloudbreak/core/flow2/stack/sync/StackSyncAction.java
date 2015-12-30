package com.sequenceiq.cloudbreak.core.flow2.stack.sync;

import static com.sequenceiq.cloudbreak.cloud.model.AvailabilityZone.availabilityZone;
import static com.sequenceiq.cloudbreak.cloud.model.Location.location;
import static com.sequenceiq.cloudbreak.cloud.model.Region.region;
import static java.util.Arrays.asList;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.springframework.statemachine.StateContext;

import com.sequenceiq.cloudbreak.cloud.context.CloudContext;
import com.sequenceiq.cloudbreak.cloud.event.resource.GetInstancesStateRequest;
import com.sequenceiq.cloudbreak.cloud.event.resource.GetInstancesStateResult;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;
import com.sequenceiq.cloudbreak.cloud.model.Location;
import com.sequenceiq.cloudbreak.converter.spi.CredentialToCloudCredentialConverter;
import com.sequenceiq.cloudbreak.converter.spi.InstanceMetaDataToCloudInstanceConverter;
import com.sequenceiq.cloudbreak.core.flow.context.StackStatusUpdateContext;
import com.sequenceiq.cloudbreak.core.flow2.AbstarctAction;
import com.sequenceiq.cloudbreak.core.flow2.MessageFactory;
import com.sequenceiq.cloudbreak.domain.InstanceMetaData;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.repository.InstanceMetaDataRepository;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.sequenceiq.cloudbreak.service.stack.flow.InstanceSyncState;

public class StackSyncAction extends AbstarctAction<StackSyncState, StackSyncEvent, StackSyncContext, StackStatusUpdateContext> {

    @Inject
    private StackService stackService;
    @Inject
    private InstanceMetaDataRepository instanceMetaDataRepository;
    @Inject
    private CredentialToCloudCredentialConverter credentialConverter;
    @Inject
    private InstanceMetaDataToCloudInstanceConverter metadataConverter;

    public StackSyncAction() {
        super(StackStatusUpdateContext.class);
    }

    @Override
    protected StackSyncContext createFlowContext(StateContext<StackSyncState, StackSyncEvent> stateContext, StackStatusUpdateContext payload) {
        String flowId = (String) stateContext.getMessageHeader(MessageFactory.HEADERS.FLOW_ID.name());
        Stack stack = stackService.getById(payload.getStackId());
        Set<InstanceMetaData> instances = instanceMetaDataRepository.findNotTerminatedForStack(payload.getStackId());
        Location location = location(region(stack.getRegion()), availabilityZone(stack.getAvailabilityZone()));
        CloudContext cloudContext = new CloudContext(stack.getId(), stack.getName(), stack.cloudPlatform(), stack.getOwner(), stack.getPlatformVariant(),
                location);
        CloudCredential cloudCredential = credentialConverter.convert(stack.getCredential());
        // TODO move somewhere else if possible
        Map<InstanceSyncState, Integer> syncStateCounts = initInstanceStateCounts();
        return new StackSyncContext(flowId, stack, instances, syncStateCounts, cloudContext, cloudCredential);
    }

    @Override
    protected void doExecute(StackSyncContext context, StackStatusUpdateContext payload, Map<Object, Object> variables) {
        MDCBuilder.buildMdcContext(payload.getStackId());
        Stack stack = context.getStack();
        if (!stack.isDeleteInProgress() && !stack.isStackInDeletionPhase() && !stack.isModificationInProgress()) {
            for (InstanceMetaData metaData : context.getInstanceMetaData()) {
                // TODO remove asList()
                GetInstancesStateRequest<GetInstancesStateResult> stateRequest =
                        new GetInstancesStateRequest<>(context.getCloudContext(), context.getCloudCredential(), asList(metadataConverter.convert(metaData)));
                sendEvent(context.getFlowId(), stateRequest.selector(), stateRequest);
            }
        } else {
            GetInstancesStateRequest<GetInstancesStateResult> stateRequest =
                    new GetInstancesStateRequest<>(context.getCloudContext(), context.getCloudCredential());
            sendEvent(context.getFlowId(), StackSyncEvent.SYNC_FINISHED_EVENT.stringRepresentation(), new GetInstancesStateResult(stateRequest));
        }
    }

    @Override
    protected Object getFailurePayload(StackSyncContext flowContext, Exception ex) {
        GetInstancesStateRequest<GetInstancesStateResult> stateRequest =
                new GetInstancesStateRequest<>(flowContext.getCloudContext(), flowContext.getCloudCredential());
        return new GetInstancesStateResult(ex.getMessage(), ex, stateRequest);
    }

    private Map<InstanceSyncState, Integer> initInstanceStateCounts() {
        Map<InstanceSyncState, Integer> instanceStates = new ConcurrentHashMap<>();
        instanceStates.put(InstanceSyncState.DELETED, 0);
        instanceStates.put(InstanceSyncState.STOPPED, 0);
        instanceStates.put(InstanceSyncState.RUNNING, 0);
        instanceStates.put(InstanceSyncState.IN_PROGRESS, 0);
        instanceStates.put(InstanceSyncState.UNKNOWN, 0);
        return instanceStates;
    }
}
