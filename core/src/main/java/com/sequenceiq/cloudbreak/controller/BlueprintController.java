package com.sequenceiq.cloudbreak.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.BlueprintEndpoint;
import com.sequenceiq.cloudbreak.doc.ContentType;
import com.sequenceiq.cloudbreak.doc.ControllerDescription;
import com.sequenceiq.cloudbreak.doc.Notes;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions.BlueprintOpDescription;
import com.sequenceiq.cloudbreak.domain.Blueprint;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.model.BlueprintRequest;
import com.sequenceiq.cloudbreak.model.BlueprintResponse;
import com.sequenceiq.cloudbreak.model.IdJson;
import com.sequenceiq.cloudbreak.service.blueprint.BlueprintLoaderService;
import com.sequenceiq.cloudbreak.service.blueprint.BlueprintService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Component
@Api(value = "/blueprints", description = ControllerDescription.BLUEPRINT_DESCRIPTION, position = 0)
public class BlueprintController implements BlueprintEndpoint {

    @Inject
    private BlueprintService blueprintService;

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @Inject
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Inject
    private BlueprintLoaderService blueprintLoaderService;

    @Override
    @ApiOperation(value = BlueprintOpDescription.POST_PRIVATE, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public IdJson postPrivate(BlueprintRequest blueprintRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createBlueprint(user, blueprintRequest, false);
    }

    @Override
    @ApiOperation(value = BlueprintOpDescription.POST_PUBLIC, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public IdJson postPublic(BlueprintRequest blueprintRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createBlueprint(user, blueprintRequest, true);
    }

    @Override
    @ApiOperation(value = BlueprintOpDescription.GET_PRIVATE, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public Set<BlueprintResponse> getPrivates() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Blueprint> blueprints = blueprintService.retrievePrivateBlueprints(user);
        if (blueprints.isEmpty()) {
            Set<Blueprint> blueprintsList = blueprintLoaderService.loadBlueprints(user);
            blueprints = new HashSet<>((ArrayList<Blueprint>) blueprintService.save(blueprintsList));
        }
        return toJsonList(blueprints);
    }

    @Override
    @ApiOperation(value = BlueprintOpDescription.GET_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public BlueprintResponse getPrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Blueprint blueprint = blueprintService.getPrivateBlueprint(name, user);
        return conversionService.convert(blueprint, BlueprintResponse.class);
    }

    @Override
    @ApiOperation(value = BlueprintOpDescription.GET_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public BlueprintResponse getPublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        Blueprint blueprint = blueprintService.getPublicBlueprint(name, user);
        return conversionService.convert(blueprint, BlueprintResponse.class);
    }

    @Override
    @ApiOperation(value = BlueprintOpDescription.GET_PUBLIC, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public Set<BlueprintResponse> getPublics() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Blueprint> blueprints = blueprintLoaderService.loadBlueprints(user);
        blueprints.addAll(blueprintService.retrieveAccountBlueprints(user));
        return toJsonList(blueprints);
    }

    @Override
    @ApiOperation(value = BlueprintOpDescription.GET_BY_ID, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public BlueprintResponse get(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Blueprint blueprint = blueprintService.get(id);
        return conversionService.convert(blueprint, BlueprintResponse.class);
    }

    @Override
    @ApiOperation(value = BlueprintOpDescription.DELETE_BY_ID, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public BlueprintResponse delete(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        blueprintService.delete(id, user);
        return new BlueprintResponse();
    }

    @Override
    @ApiOperation(value = BlueprintOpDescription.DELETE_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public BlueprintResponse deletePublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        blueprintService.delete(name, user);
        return new BlueprintResponse();
    }

    @Override
    @ApiOperation(value = BlueprintOpDescription.DELETE_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public BlueprintResponse deletePrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        blueprintService.delete(name, user);
        return new BlueprintResponse();
    }

    private IdJson createBlueprint(CbUser user, BlueprintRequest blueprintRequest, boolean publicInAccount) {
        Blueprint blueprint = conversionService.convert(blueprintRequest, Blueprint.class);
        blueprint.setPublicInAccount(publicInAccount);
        blueprint = blueprintService.create(user, blueprint);
        return new IdJson(blueprint.getId());
    }

    private Set<BlueprintResponse> toJsonList(Set<Blueprint> blueprints) {
        return (Set<BlueprintResponse>) conversionService.convert(blueprints,
                TypeDescriptor.forObject(blueprints),
                TypeDescriptor.collection(Set.class, TypeDescriptor.valueOf(BlueprintResponse.class)));
    }

}
