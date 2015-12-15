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
import com.sequenceiq.cloudbreak.model.NetworkJson;

public interface NetworkEndpoint {

    @POST
    @Path("user/networks")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPrivate(NetworkJson networkJson);

    @POST
    @Path("account/networks")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPublic(NetworkJson networkJson);

    @GET
    @Path("user/networks")
    @Produces(MediaType.APPLICATION_JSON)
    Set<NetworkJson> getPrivates();

    @GET
    @Path("account/networks")
    @Produces(MediaType.APPLICATION_JSON)
    Set<NetworkJson> getPublics();

    @GET
    @Path("user/networks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson getPrivate(@PathParam("name") String name);

    @GET
    @Path("account/networks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson getPublic(@PathParam("name") String name);

    @GET
    @Path("networks/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson get(@PathParam("id") Long id);

    @DELETE
    @Path("networks/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson delete(@PathParam("id") Long id);

    @DELETE
    @Path("account/networks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson deletePublic(@PathParam("name") String name);

    @DELETE
    @Path("user/networks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson deletePrivate(@PathParam("name") String name);
}
