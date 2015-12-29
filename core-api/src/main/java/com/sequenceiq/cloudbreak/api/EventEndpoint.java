package com.sequenceiq.cloudbreak.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.CloudbreakEventsJson;

@Path("/")
public interface EventEndpoint {

    @GET
    @Path("events")
    @Produces(MediaType.APPLICATION_JSON)
    List<CloudbreakEventsJson> get(@QueryParam("since") Long since);
}
