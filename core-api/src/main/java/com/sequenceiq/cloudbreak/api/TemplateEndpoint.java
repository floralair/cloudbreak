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
import com.sequenceiq.cloudbreak.model.TemplateRequest;
import com.sequenceiq.cloudbreak.model.TemplateResponse;

public interface TemplateEndpoint {

    @POST
    @Path("user/templates")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPrivate(TemplateRequest templateRequest);

    @POST
    @Path("account/templates")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPublic(TemplateRequest templateRequest);

    @GET
    @Path("user/templates")
    @Produces(MediaType.APPLICATION_JSON)
    Set<TemplateResponse> getPrivates();

    @GET
    @Path("account/templates")
    @Produces(MediaType.APPLICATION_JSON)
    Set<TemplateResponse> getPublics();

    @GET
    @Path("user/templates/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    TemplateResponse getPrivate(@PathParam("name") String name);

    @GET
    @Path("account/templates/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    TemplateResponse getPublic(@PathParam("name") String name);

    @GET
    @Path("templates/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    TemplateResponse get(@PathParam("id") Long id);

    @DELETE
    @Path("templates/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    TemplateResponse delete(@PathParam("id") Long id);

    @DELETE
    @Path("account/templates/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    TemplateResponse deletePublic(@PathParam("name") String name);

    @DELETE
    @Path("user/templates/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    TemplateResponse deletePrivate(@PathParam("name") String name);
}
