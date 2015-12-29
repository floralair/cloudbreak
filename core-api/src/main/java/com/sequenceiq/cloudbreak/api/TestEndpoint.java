package com.sequenceiq.cloudbreak.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
@Path("/test")
public interface TestEndpoint {

    @GET
    @Path("/simple")
    String test();

}
