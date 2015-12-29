package com.sequenceiq.cloudbreak.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.CloudbreakUsageJson;

@Path("/")
public interface UsageEndpoint {

    @GET
    @Path("usages")
    @Produces(MediaType.APPLICATION_JSON)
    List<CloudbreakUsageJson> getDeployer(
            @QueryParam("since") Long since,
            @QueryParam("filterenddate") Long filterEndDate,
            @QueryParam("user") String userId,
            @QueryParam("account") String accountId,
            @QueryParam("cloud") String cloud,
            @QueryParam("zone") String zone);

    @GET
    @Path("account/usages")
    @Produces(MediaType.APPLICATION_JSON)
    List<CloudbreakUsageJson> getAccount(
            @QueryParam("since") Long since,
            @QueryParam("filterenddate") Long filterEndDate,
            @QueryParam("user") String userId,
            @QueryParam("cloud") String cloud,
            @QueryParam("zone") String zone);

    @GET
    @Path("user/usages")
    @Produces(MediaType.APPLICATION_JSON)
    List<CloudbreakUsageJson> getUser(
            @QueryParam("since") Long since,
            @QueryParam("filterenddate") Long filterEndDate,
            @QueryParam("cloud") String cloud,
            @QueryParam("zone") String zone);

    @GET
    @Path("/usages/generate")
    @Produces(MediaType.APPLICATION_JSON)
    List<CloudbreakUsageJson> generate();
}
