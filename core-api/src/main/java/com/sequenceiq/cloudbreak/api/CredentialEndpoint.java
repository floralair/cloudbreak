package com.sequenceiq.cloudbreak.api;

import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.CredentialRequest;
import com.sequenceiq.cloudbreak.model.CredentialResponse;
import com.sequenceiq.cloudbreak.model.IdJson;

public interface CredentialEndpoint {

    @POST
    @Path("user/credentials")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPrivate(CredentialRequest credentialRequest);

    @POST
    @Path("account/credentials")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPublic(CredentialRequest credentialRequest);

    @GET
    @Path("user/credentials")
    @Produces(MediaType.APPLICATION_JSON)
    Set<CredentialResponse> getPrivates();

    @GET
    @Path("account/credentials")
    @Produces(MediaType.APPLICATION_JSON)
    Set<CredentialResponse> getPublics();

    @GET
    @Path("user/credentials/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    CredentialResponse getPrivate(@PathParam("name") String name);

    @GET
    @Path("account/credentials/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    CredentialResponse getPublic(@PathParam("name") String name);

    @GET
    @Path("credentials/{credentialId}")
    @Produces(MediaType.APPLICATION_JSON)
    CredentialResponse get(@PathParam("credentialId") Long credentialId);

    @DELETE
    @Path("credentials/{credentialId}")
    @Produces(MediaType.APPLICATION_JSON)
    CredentialResponse delete(@PathParam("credentialId") Long credentialId);

    @DELETE
    @Path("account/credentials/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    CredentialResponse deletePublic(@PathParam("name") String name);

    @DELETE
    @Path("user/credentials/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    CredentialResponse deletePrivate(@PathParam("name") String name);
}