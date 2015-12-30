package com.sequenceiq.cloudbreak.core.flow2.stack.sync;

import java.util.Map;
import java.util.Set;

import com.sequenceiq.cloudbreak.cloud.context.CloudContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;
import com.sequenceiq.cloudbreak.core.flow2.CommonContext;
import com.sequenceiq.cloudbreak.domain.InstanceMetaData;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.service.stack.flow.InstanceSyncState;

public class StackSyncContext extends CommonContext {

    private Stack stack;
    private Set<InstanceMetaData> instanceMetaData;
    private Map<InstanceSyncState, Integer> syncStateCounts;
    private CloudContext cloudContext;
    private CloudCredential cloudCredential;

    public StackSyncContext(String flowId, Stack stack, Set<InstanceMetaData> instanceMetaData, Map<InstanceSyncState, Integer> syncStateCounts,
            CloudContext cloudContext, CloudCredential cloudCredential) {
        super(flowId);
        this.stack = stack;
        this.instanceMetaData = instanceMetaData;
        this.syncStateCounts = syncStateCounts;
        this.cloudContext = cloudContext;
        this.cloudCredential = cloudCredential;
    }

    public Stack getStack() {
        return stack;
    }

    public Set<InstanceMetaData> getInstanceMetaData() {
        return instanceMetaData;
    }

    public Map<InstanceSyncState, Integer> getSyncStateCounts() {
        return syncStateCounts;
    }

    public CloudContext getCloudContext() {
        return cloudContext;
    }

    public CloudCredential getCloudCredential() {
        return cloudCredential;
    }
}
