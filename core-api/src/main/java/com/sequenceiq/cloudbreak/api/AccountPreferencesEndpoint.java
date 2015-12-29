package com.sequenceiq.cloudbreak.api;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.AccountPreferencesJson;

@Path("/")
public interface AccountPreferencesEndpoint {

    @GET
    @Path("accountpreferences")
    @Produces(MediaType.APPLICATION_JSON)
    AccountPreferencesJson get();

    @PUT
    @Path("accountpreferences")
    @Produces(MediaType.APPLICATION_JSON)
    String put(AccountPreferencesJson updateRequest);

    @GET
    @Path("accountpreferences/validate")
    @Produces(MediaType.APPLICATION_JSON)
    String validate();
}
