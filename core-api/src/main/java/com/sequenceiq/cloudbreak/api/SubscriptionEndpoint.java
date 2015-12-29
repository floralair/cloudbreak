package com.sequenceiq.cloudbreak.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.IdJson;
import com.sequenceiq.cloudbreak.model.SubscriptionRequest;

@Path("/")
public interface SubscriptionEndpoint {

    @POST
    @Path("subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson subscribe(SubscriptionRequest subscriptionRequest);

}
