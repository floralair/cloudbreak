package com.sequenceiq.cloudbreak.cloud.task;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.CloudConnector;
import com.sequenceiq.cloudbreak.cloud.InstanceConnector;
import com.sequenceiq.cloudbreak.cloud.event.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.event.context.ResourceBuilderContext;
import com.sequenceiq.cloudbreak.cloud.event.instance.InstanceConsoleOutputResult;
import com.sequenceiq.cloudbreak.cloud.event.instance.InstancesStatusResult;
import com.sequenceiq.cloudbreak.cloud.init.CloudPlatformConnectors;
import com.sequenceiq.cloudbreak.cloud.model.CloudInstance;
import com.sequenceiq.cloudbreak.cloud.model.CloudResource;
import com.sequenceiq.cloudbreak.cloud.model.CloudResourceStatus;
import com.sequenceiq.cloudbreak.cloud.model.CloudVmInstanceStatus;
import com.sequenceiq.cloudbreak.cloud.model.InstanceStatus;
import com.sequenceiq.cloudbreak.cloud.template.ComputeResourceBuilder;
import com.sequenceiq.cloudbreak.cloud.template.ResourceChecker;

@Component
public class PollTaskFactory {

    @Inject
    private CloudPlatformConnectors cloudPlatformConnectors;

    public PollTask<ResourcesStatePollerResult> newPollResourcesStateTask(AuthenticatedContext authenticatedContext,
            List<CloudResource> cloudResource, boolean cancellable) {
        CloudConnector connector = cloudPlatformConnectors.get(authenticatedContext.getCloudContext().getPlatformVariant());
        return new PollResourcesStateTask(authenticatedContext, connector.resources(), cloudResource, cancellable);
    }

    public PollTask<InstancesStatusResult> newPollInstanceStateTask(AuthenticatedContext authenticatedContext, List<CloudInstance> instances,
            Set<InstanceStatus> completedStatuses) {
        CloudConnector connector = cloudPlatformConnectors.get(authenticatedContext.getCloudContext().getPlatformVariant());
        return new PollInstancesStateTask(authenticatedContext, connector.instances(), instances, completedStatuses);
    }

    public PollTask<InstanceConsoleOutputResult> newPollConsoleOutputTask(InstanceConnector instanceConnector,
            AuthenticatedContext authenticatedContext, CloudInstance instance) {
        return new PollInstanceConsoleOutputTask(instanceConnector, authenticatedContext, instance);
    }

    public PollTask<List<CloudResourceStatus>> newPollResourceTask(ResourceChecker checker, AuthenticatedContext authenticatedContext,
            List<CloudResource> cloudResource, ResourceBuilderContext context, boolean cancellable) {
        return new PollResourceTask(authenticatedContext, checker, cloudResource, context, cancellable);
    }

    public PollTask<List<CloudVmInstanceStatus>> newPollComputeStatusTask(ComputeResourceBuilder builder, AuthenticatedContext authenticatedContext,
            ResourceBuilderContext context, CloudInstance instance) {
        return new PollComputeStatusTask(authenticatedContext, builder, context, instance);
    }

}