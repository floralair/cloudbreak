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

@Path("/")
public interface NetworkEndpoint {

    @GET
    @Path("networks/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson get(@PathParam("id") Long id);

    @DELETE
    @Path("networks/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson delete(@PathParam(value ="id") Long id);

    @POST
    @Path("account/networks")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPublic(NetworkJson networkJson);

    @GET
    @Path("account/networks")
    @Produces(MediaType.APPLICATION_JSON)
    Set<NetworkJson> getPublics();

    @GET
    @Path("account/networks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson getPublic(@PathParam(value = "name") String name);

    @DELETE
    @Path("account/networks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson deletePublic(@PathParam(value = "name") String name);

    @POST
    @Path("user/networks")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPrivate(NetworkJson networkJson);

    @GET
    @Path("user/networks")
    @Produces(MediaType.APPLICATION_JSON)
    Set<NetworkJson> getPrivates();

    @GET
    @Path("user/networks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson getPrivate(@PathParam(value = "name") String name);

    @DELETE
    @Path("user/networks/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    NetworkJson deletePrivate(@PathParam(value = "name") String name);

}
