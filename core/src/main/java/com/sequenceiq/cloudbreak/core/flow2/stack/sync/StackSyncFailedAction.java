package com.sequenceiq.cloudbreak.core.flow2.stack.sync;

import static com.sequenceiq.cloudbreak.common.type.Status.AVAILABLE;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.statemachine.StateContext;

import com.sequenceiq.cloudbreak.cloud.event.resource.GetInstancesStateResult;
import com.sequenceiq.cloudbreak.cloud.model.CloudVmInstanceStatus;
import com.sequenceiq.cloudbreak.core.flow2.AbstarctAction;
import com.sequenceiq.cloudbreak.service.events.CloudbreakEventService;
import com.sequenceiq.cloudbreak.service.messages.CloudbreakMessagesService;
import com.sequenceiq.cloudbreak.service.stack.flow.InstanceSyncState;

public class StackSyncFailedAction extends AbstarctAction<StackSyncState, StackSyncEvent, StackSyncContext, GetInstancesStateResult> {

    private static final String STACK_SYNC_INSTANCE_STATUS_RETRIEVAL_FAILED = "stack.sync.instance.status.retrieval.failed";

    @Inject
    private CloudbreakEventService eventService;
    @Inject
    private CloudbreakMessagesService cloudbreakMessagesService;

    public StackSyncFailedAction() {
        super(GetInstancesStateResult.class);
    }

    @Override
    protected StackSyncContext createFlowContext(StateContext<StackSyncState, StackSyncEvent> stateContext, GetInstancesStateResult payload) {
        return null;
    }

    @Override
    protected void doExecute(StackSyncContext context, GetInstancesStateResult payload, Map<Object, Object> variables) {
        CloudVmInstanceStatus instanceStatus = payload.getStatuses().get(0);
        instanceStatus.getCloudInstance();
        eventService.fireCloudbreakEvent(context.getStack().getId(), AVAILABLE.name(),
                cloudbreakMessagesService.getMessage(STACK_SYNC_INSTANCE_STATUS_RETRIEVAL_FAILED,
                        Collections.singletonList(instanceStatus.getCloudInstance().getInstanceId())));
        context.getSyncStateCounts().put(InstanceSyncState.UNKNOWN, context.getSyncStateCounts().get(InstanceSyncState.UNKNOWN) + 1);
        // TODO send if all
        sendEvent(context.getFlowId(), StackSyncEvent.SYNC_FAIL_HANDLED_EVENT.stringRepresentation(), payload);
    }

    @Override
    protected Object getFailurePayload(StackSyncContext flowContext, Exception ex) {
        return null;
    }
}
