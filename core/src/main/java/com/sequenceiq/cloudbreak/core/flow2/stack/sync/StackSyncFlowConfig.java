package com.sequenceiq.cloudbreak.core.flow2.stack.sync;

import static com.sequenceiq.cloudbreak.core.flow2.stack.sync.StackSyncEvent.SYNC_EVENT;
import static com.sequenceiq.cloudbreak.core.flow2.stack.sync.StackSyncEvent.SYNC_FAIL_HANDLED_EVENT;
import static com.sequenceiq.cloudbreak.core.flow2.stack.sync.StackSyncEvent.SYNC_FINALIZED_EVENT;
import static com.sequenceiq.cloudbreak.core.flow2.stack.sync.StackSyncEvent.SYNC_FINISHED_EVENT;
import static com.sequenceiq.cloudbreak.core.flow2.stack.sync.StackSyncState.FINAL_STATE;
import static com.sequenceiq.cloudbreak.core.flow2.stack.sync.StackSyncState.INIT_STATE;
import static com.sequenceiq.cloudbreak.core.flow2.stack.sync.StackSyncState.SYNC_FAILED_STATE;
import static com.sequenceiq.cloudbreak.core.flow2.stack.sync.StackSyncState.SYNC_FINISHED_STATE;
import static com.sequenceiq.cloudbreak.core.flow2.stack.sync.StackSyncState.SYNC_STATE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.statemachine.config.builders.StateMachineConfigurationBuilder;
import org.springframework.statemachine.config.builders.StateMachineStateBuilder;
import org.springframework.statemachine.config.builders.StateMachineTransitionBuilder;
import org.springframework.statemachine.config.common.annotation.ObjectPostProcessor;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.sequenceiq.cloudbreak.core.flow2.Flow;
import com.sequenceiq.cloudbreak.core.flow2.MessageFactory;
import com.sequenceiq.cloudbreak.core.flow2.config.AbstractFlowConfiguration;

public class StackSyncFlowConfig extends AbstractFlowConfiguration<StackSyncState, StackSyncEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackSyncFlowConfig.class);
    private static final List<Transition<StackSyncState, StackSyncEvent>> TRANSITIONS = Arrays.asList(
            new Transition<>(INIT_STATE, SYNC_STATE, SYNC_EVENT),
            new Transition<>(SYNC_STATE, SYNC_FINISHED_STATE, SYNC_FINISHED_EVENT)
    );
    private static final FlowEdgeConfig<StackSyncState, StackSyncEvent> EDGE_CONFIG =
            new FlowEdgeConfig<>(INIT_STATE, FINAL_STATE, SYNC_FINISHED_STATE, SYNC_FINALIZED_EVENT, SYNC_FAILED_STATE, SYNC_FAIL_HANDLED_EVENT);

    @Override
    public Flow<StackSyncState, StackSyncEvent> createFlow(String flowId) {
        Flow<StackSyncState, StackSyncEvent> flow = new Flow<>(getStateMachineFactory().getStateMachine(),
                new MessageFactory<StackSyncEvent>(), new StackSyncEventConverter());
        flow.initialize(flowId);
        return flow;
    }

    @Override
    public List<StackSyncEvent> getFlowTriggerEvents() {
        return Collections.singletonList(SYNC_EVENT);
    }

    @Override
    public StackSyncEvent[] getEvents() {
        return StackSyncEvent.values();
    }

    @Override
    protected MachineConfiguration<StackSyncState, StackSyncEvent> getStateMachineConfiguration() {
        StateMachineConfigurationBuilder<StackSyncState, StackSyncEvent> configurationBuilder =
                new StateMachineConfigurationBuilder<>(ObjectPostProcessor.QUIESCENT_POSTPROCESSOR, true);
        StateMachineStateBuilder<StackSyncState, StackSyncEvent> stateBuilder =
                new StateMachineStateBuilder<>(ObjectPostProcessor.QUIESCENT_POSTPROCESSOR, true);
        StateMachineTransitionBuilder<StackSyncState, StackSyncEvent> transitionBuilder =
                new StateMachineTransitionBuilder<>(ObjectPostProcessor.QUIESCENT_POSTPROCESSOR, true);
        StateMachineListener<StackSyncState, StackSyncEvent> listener =
                new StateMachineListenerAdapter<StackSyncState, StackSyncEvent>() {
                    @Override
                    public void stateChanged(State<StackSyncState, StackSyncEvent> from, State<StackSyncState, StackSyncEvent> to) {
                        LOGGER.info("StackSyncFlowConfig changed from {} to {}", from, to);
                    }
                };
        return new MachineConfiguration<>(configurationBuilder, stateBuilder, transitionBuilder, listener, new SyncTaskExecutor());
    }

    @Override
    protected List<Transition<StackSyncState, StackSyncEvent>> getTransitions() {
        return TRANSITIONS;
    }

    @Override
    protected FlowEdgeConfig<StackSyncState, StackSyncEvent> getEdgeConfig() {
        return EDGE_CONFIG;
    }
}
