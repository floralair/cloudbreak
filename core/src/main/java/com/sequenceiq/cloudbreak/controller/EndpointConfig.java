package com.sequenceiq.cloudbreak.controller;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class EndpointConfig extends ResourceConfig {

    public EndpointConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        register(BlueprintController.class);
        register(CloudConnectorController.class);
        register(ClusterController.class);
        register(CredentialController.class);
        register(NetworkController.class);
        register(RecipeController.class);
        register(SecurityGroupController.class);
        register(StackController.class);
        register(TemplateController.class);

        register(CloudbreakEventController.class);
        register(SubscriptionController.class);
        register(CloudbreakUsageController.class);
        register(AccountPreferencesController.class);

        register(Test.class);
    }
}
