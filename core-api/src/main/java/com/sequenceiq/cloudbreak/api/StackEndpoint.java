package com.sequenceiq.cloudbreak.api;

import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.IdJson;
import com.sequenceiq.cloudbreak.model.StackRequest;
import com.sequenceiq.cloudbreak.model.StackResponse;

public interface StackEndpoint {

    @POST
    @Path("user/stacks")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPrivate(StackRequest stackRequest);

    @POST
    @Path("account/stacks")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPublic(StackRequest stackRequest);

    @GET
    @Path("user/stacks")
    @Produces(MediaType.APPLICATION_JSON)
    Set<StackResponse> getPrivates();

    @GET
    @Path("account/stacks")
    @Produces(MediaType.APPLICATION_JSON)
    Set<StackResponse> getPublics();

    @GET
    @Path("user/stacks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse getPrivate(@PathParam("name") String name);

    @GET
    @Path("account/stacks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse getPublic(@PathParam("name") String name);

    @GET
    @Path("stacks/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse get(@PathParam("id") Long id);

    @DELETE
    @Path("stacks/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse delete(@PathParam("id") Long id);

    @DELETE
    @Path("account/stacks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse deletePublic(@PathParam("name") String name);

    @DELETE
    @Path("user/stacks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse deletePrivate(@PathParam("name") String name);
}
