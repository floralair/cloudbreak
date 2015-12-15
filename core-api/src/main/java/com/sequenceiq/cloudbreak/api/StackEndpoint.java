package com.sequenceiq.cloudbreak.api;


import java.util.Map;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.AmbariAddressJson;
import com.sequenceiq.cloudbreak.model.IdJson;
import com.sequenceiq.cloudbreak.model.PlatformVariantsJson;
import com.sequenceiq.cloudbreak.model.StackRequest;
import com.sequenceiq.cloudbreak.model.StackResponse;
import com.sequenceiq.cloudbreak.model.StackValidationRequest;
import com.sequenceiq.cloudbreak.model.UpdateStackJson;

public interface StackEndpoint {

    @POST
    @Path("user/stacks")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPrivate(StackRequest stackRequest);

    @POST
    @Path("account/stacks")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPublic(StackRequest stackRequest);

    @POST
    @Path("stacks/validate")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson validate(StackValidationRequest stackValidationRequest);

    @POST
    @Path(value = "stacks/ambari")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse getStackForAmbari(AmbariAddressJson json);

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
    StackResponse getPrivate(@PathParam(value = "name") String name);

    @GET
    @Path("account/stacks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse getPublic(@PathParam(value = "name") String name);

    @GET
    @Path("stacks/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse get(@PathParam(value = "id") Long id);

    @GET
    @Path(value = "stacks/{id}/status")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> status(@PathParam(value = "id") Long id);

    @GET
    @Path(value = "stacks/platformVariants")
    @Produces(MediaType.APPLICATION_JSON)
    PlatformVariantsJson variants();

    @DELETE
    @Path("stacks/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse delete(@PathParam(value = "id") Long id, @QueryParam("forced") @DefaultValue("false") Boolean forced);

    @DELETE
    @Path("account/stacks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse deletePublic(@PathParam(value = "name") String name, @QueryParam("forced") @DefaultValue("false") Boolean forced);

    @DELETE
    @Path("user/stacks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    StackResponse deletePrivate(@PathParam(value = "name") String name, @QueryParam("forced") @DefaultValue("false") Boolean forced);

    @DELETE
    @Path("stacks/{stackId}/{instanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    String deleteInstance(@PathParam(value = "stackId") Long stackId, @PathParam(value = "instanceId") String instanceId);

    @PUT
    @Path("stacks/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    String put(@PathParam(value = "id")Long id, UpdateStackJson updateRequest);
}
