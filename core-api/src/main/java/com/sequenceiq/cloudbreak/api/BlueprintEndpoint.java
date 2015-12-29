package com.sequenceiq.cloudbreak.api;

import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.BlueprintRequest;
import com.sequenceiq.cloudbreak.model.BlueprintResponse;
import com.sequenceiq.cloudbreak.model.IdJson;

@Path("/")
public interface BlueprintEndpoint {

    @GET
    @Path("blueprints/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    BlueprintResponse get(@PathParam(value = "id") Long id);

    @DELETE
    @Path("blueprints/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    BlueprintResponse delete(@PathParam(value = "id") Long id);

    @POST
    @Path("user/blueprints")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPrivate(BlueprintRequest blueprintRequest);

    @GET
    @Path("user/blueprints")
    @Produces(MediaType.APPLICATION_JSON)
    Set<BlueprintResponse> getPrivates();

    @GET
    @Path("user/blueprints/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    BlueprintResponse getPrivate(@PathParam(value = "name") String name);

    @DELETE
    @Path("user/blueprints/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    BlueprintResponse deletePrivate(@PathParam(value = "name") String name);

    @POST
    @Path("account/blueprints")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPublic(BlueprintRequest blueprintRequest);

    @GET
    @Path("account/blueprints")
    @Produces(MediaType.APPLICATION_JSON)
    Set<BlueprintResponse> getPublics();

    @GET
    @Path("account/blueprints/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    BlueprintResponse getPublic(@PathParam(value = "name") String name);

    @DELETE
    @Path("account/blueprints/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    BlueprintResponse deletePublic(@PathParam(value = "name") String name);

}
