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
import com.sequenceiq.cloudbreak.model.RecipeRequest;
import com.sequenceiq.cloudbreak.model.RecipeResponse;

@Path("/")
public interface RecipeEndpoint {

    @POST
    @Path("user/recipes")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPrivate(RecipeRequest recipeRequest);

    @POST
    @Path("account/recipes")
    @Produces(MediaType.APPLICATION_JSON)
    IdJson postPublic(RecipeRequest recipeRequest);

    @GET
    @Path("user/recipes")
    @Produces(MediaType.APPLICATION_JSON)
    Set<RecipeResponse> getPrivates();

    @GET
    @Path("account/recipes")
    @Produces(MediaType.APPLICATION_JSON)
    Set<RecipeResponse> getPublics();

    @GET
    @Path("user/recipes/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    RecipeResponse getPrivate(@PathParam("name") String name);

    @GET
    @Path("account/recipes/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    RecipeResponse getPublic(@PathParam("name") String name);

    @GET
    @Path("recipes/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    RecipeResponse get(@PathParam("id") Long id);

    @DELETE
    @Path("recipes/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    RecipeResponse delete(@PathParam("id") Long id);

    @DELETE
    @Path("account/recipes/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    RecipeResponse deletePublic(@PathParam("name") String name);

    @DELETE
    @Path("user/recipes/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    RecipeResponse deletePrivate(@PathParam("name") String name);
}
