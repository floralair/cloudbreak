package com.sequenceiq.cloudbreak.service.stack.resource.definition;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cloud.event.platform.ResourceDefinitionRequest;
import com.sequenceiq.cloudbreak.cloud.event.platform.ResourceDefinitionResult;
import com.sequenceiq.cloudbreak.cloud.model.CloudPlatformVariant;
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.cloud.model.Variant;
import com.sequenceiq.cloudbreak.service.stack.connector.OperationException;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class ResourceDefinitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDefinitionService.class);

    @Inject
    private EventBus eventBus;

    public String getResourceDefinition(String cloudPlatform, String resource) {
        LOGGER.debug("Sending request for {} {} resource property definition", cloudPlatform, resource);
        CloudPlatformVariant platformVariant = new CloudPlatformVariant(Platform.platform(cloudPlatform), Variant.EMPTY);
        ResourceDefinitionRequest request = new ResourceDefinitionRequest(platformVariant, resource);
        eventBus.notify(request.selector(), Event.wrap(request));
        try {
            ResourceDefinitionResult result = request.await();
            LOGGER.info("Resource property definition: {}", result);
            return result.getDefinition();
        } catch (InterruptedException e) {
            LOGGER.error("Error while sending resource definition request", e);
            throw new OperationException(e);
        }
    }

}
