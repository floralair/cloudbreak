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
import com.sequenceiq.cloudbreak.model.SecurityGroupJson;

@Path("/")
public interface SecurityGroupEndpoint {

    @POST
    @Path("user/securitygroups")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPrivate(SecurityGroupJson securityGroupJson);

    @POST
    @Path("account/securitygroups")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPublic(SecurityGroupJson securityGroupJson);

    @GET
    @Path("user/securitygroups")
    @Produces(MediaType.APPLICATION_JSON)
    Set<SecurityGroupJson> getPrivates();

    @GET
    @Path("account/securitygroups")
    @Produces(MediaType.APPLICATION_JSON)
    Set<SecurityGroupJson> getPublics();

    @GET
    @Path("user/securitygroups/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SecurityGroupJson getPrivate(@PathParam(value = "name") String name);

    @GET
    @Path("account/securitygroups/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SecurityGroupJson getPublic(@PathParam(value = "name") String name);

    @GET
    @Path("securitygroups/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    SecurityGroupJson get(@PathParam(value = "id") Long id);

    @DELETE
    @Path("securitygroups/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    SecurityGroupJson delete(@PathParam(value = "id") Long id);

    @DELETE
    @Path("account/securitygroups/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SecurityGroupJson deletePublic(@PathParam(value = "name") String name);

    @DELETE
    @Path("user/securitygroups/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    SecurityGroupJson deletePrivate(@PathParam(value = "name") String name);
}
