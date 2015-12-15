package com.sequenceiq.cloudbreak.controller;

import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.RecipeEndpoint;
import com.sequenceiq.cloudbreak.doc.ContentType;
import com.sequenceiq.cloudbreak.doc.ControllerDescription;
import com.sequenceiq.cloudbreak.doc.Notes;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions.RecipeOpDescription;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.Recipe;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.model.IdJson;
import com.sequenceiq.cloudbreak.model.RecipeRequest;
import com.sequenceiq.cloudbreak.model.RecipeResponse;
import com.sequenceiq.cloudbreak.service.recipe.RecipeService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Component
@Api(value = "/recipe", description = ControllerDescription.RECIPE_DESCRIPTION, position = 5)
public class RecipeController implements RecipeEndpoint {

    @Inject
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Inject
    private RecipeService recipeService;

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @Override
    @ApiOperation(value = RecipeOpDescription.POST_PUBLIC, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public IdJson postPublic(RecipeRequest recipeRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createRecipe(user, recipeRequest, true);
    }

    @Override
    @ApiOperation(value = RecipeOpDescription.POST_PRIVATE, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public IdJson postPrivate(RecipeRequest recipeRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createRecipe(user, recipeRequest, false);
    }

    @ApiOperation(value = RecipeOpDescription.GET_PRIVATE, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public Set<RecipeResponse> getPrivates() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Recipe> recipes = recipeService.retrievePrivateRecipes(user);
        return toJsonSet(recipes);
    }

    @ApiOperation(value = RecipeOpDescription.GET_PUBLIC, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public Set<RecipeResponse> getPublics() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Recipe> recipes = recipeService.retrieveAccountRecipes(user);
        return toJsonSet(recipes);
    }

    @ApiOperation(value = RecipeOpDescription.GET_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public RecipeResponse getPrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        Recipe recipe = recipeService.getPrivateRecipe(name, user);
        return conversionService.convert(recipe, RecipeResponse.class);
    }

    @ApiOperation(value = RecipeOpDescription.GET_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public RecipeResponse getPublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Recipe recipe = recipeService.getPublicRecipe(name, user);
        return conversionService.convert(recipe, RecipeResponse.class);
    }

    @Override
    @ApiOperation(value = RecipeOpDescription.GET_BY_ID, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public RecipeResponse get(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Recipe recipe = recipeService.get(id);
        return conversionService.convert(recipe, RecipeResponse.class);
    }

    @Override
    @ApiOperation(value = RecipeOpDescription.DELETE_BY_ID, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public RecipeResponse delete(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        recipeService.delete(id, user);
        return new RecipeResponse();
    }

    @Override
    @ApiOperation(value = RecipeOpDescription.DELETE_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public RecipeResponse deletePublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        recipeService.delete(name, user);
        return new RecipeResponse();
    }

    @Override
    @ApiOperation(value = RecipeOpDescription.DELETE_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.RECIPE_NOTES)
    public RecipeResponse deletePrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        recipeService.delete(name, user);
        return new RecipeResponse();
    }

    private IdJson createRecipe(CbUser user, RecipeRequest recipeRequest, boolean publicInAccount) {
        Recipe recipe = conversionService.convert(recipeRequest, Recipe.class);
        recipe.setPublicInAccount(publicInAccount);
        recipe = recipeService.create(user, recipe);
        return new IdJson(recipe.getId());
    }

    private Set<RecipeResponse> toJsonSet(Set<Recipe> recipes) {
        return (Set<RecipeResponse>) conversionService.convert(recipes, TypeDescriptor.forObject(recipes),
                TypeDescriptor.collection(Set.class, TypeDescriptor.valueOf(RecipeResponse.class)));
    }
}
