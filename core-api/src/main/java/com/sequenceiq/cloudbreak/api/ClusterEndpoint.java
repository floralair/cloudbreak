package com.sequenceiq.cloudbreak.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.ClusterRequest;
import com.sequenceiq.cloudbreak.model.ClusterResponse;
import com.sequenceiq.cloudbreak.model.UpdateClusterJson;

@Path("/")
public interface ClusterEndpoint {

    @POST
    @Path("stacks/{id}/cluster")
    @Produces(MediaType.APPLICATION_JSON)
    String post(@PathParam(value = "id") Long id, ClusterRequest request);

    @GET
    @Path("stacks/{id}/cluster")
    @Produces(MediaType.APPLICATION_JSON)
    ClusterResponse get(@PathParam(value = "id") Long id);

    @GET
    @Path("account/stacks/{name}/cluster")
    @Produces(MediaType.APPLICATION_JSON)
    ClusterResponse getPublic(@PathParam(value = "name") String name);

    @GET
    @Path("user/stacks/{name}/cluster")
    @Produces(MediaType.APPLICATION_JSON)
    ClusterResponse getPrivate(@PathParam(value = "name") String name);

    @PUT
    @Path("stacks/{id}/cluster")
    @Produces(MediaType.APPLICATION_JSON)
    String put(@PathParam(value = "id") Long stackId, UpdateClusterJson updateJson) throws Exception;

}
