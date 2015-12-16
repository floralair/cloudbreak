package com.sequenceiq.cloudbreak.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import com.sequenceiq.cloudbreak.repository.BlueprintRepository;
import com.sequenceiq.cloudbreak.service.blueprint.BlueprintService;
import com.sequenceiq.cloudbreak.service.blueprint.DefaultBlueprintLoaderService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(value = "/blueprints", description = ControllerDescription.BLUEPRINT_DESCRIPTION, position = 0)
public class BlueprintController {

    @Inject
    private BlueprintService blueprintService;

    @Inject
    private BlueprintRepository blueprintRepository;

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @Inject
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Inject
    private DefaultBlueprintLoaderService defaultBlueprintLoaderService;

    @RequestMapping(value = "user/blueprints", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = BlueprintOpDescription.POST_PRIVATE, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public IdJson createPrivateBlueprint(@RequestBody @Valid BlueprintRequest blueprintRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createBlueprint(user, blueprintRequest, false);
    }

    @RequestMapping(value = "account/blueprints", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = BlueprintOpDescription.POST_PUBLIC, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    public IdJson createAccountBlueprint(@RequestBody @Valid BlueprintRequest blueprintRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createBlueprint(user, blueprintRequest, true);
    }


    @ApiOperation(value = BlueprintOpDescription.GET_PRIVATE, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    @RequestMapping(value = "user/blueprints", method = RequestMethod.GET)
    @ResponseBody
    public Set<BlueprintResponse> getPrivateBlueprints() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Blueprint> blueprints = blueprintService.retrievePrivateBlueprints(user);
        if (blueprints.isEmpty()) {
            Set<Blueprint> blueprintsList = defaultBlueprintLoaderService.loadBlueprints(user);
            blueprints = new HashSet<>((ArrayList<Blueprint>) blueprintRepository.save(blueprintsList));
        }
        return toJsonList(blueprints);
    }

    @ApiOperation(value = BlueprintOpDescription.GET_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    @RequestMapping(value = "user/blueprints/{name}", method = RequestMethod.GET)
    @ResponseBody
    public BlueprintResponse getPrivateBlueprint(@PathVariable String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Blueprint blueprint = blueprintService.getPrivateBlueprint(name, user);
        return conversionService.convert(blueprint, BlueprintResponse.class);
    }

    @ApiOperation(value = BlueprintOpDescription.GET_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    @RequestMapping(value = "account/blueprints/{name}", method = RequestMethod.GET)
    @ResponseBody
    public BlueprintResponse createAccountBlueprint(@PathVariable String name) {
        CbUser user = authenticatedUserService.getCbUser();
        Blueprint blueprint = blueprintService.getPublicBlueprint(name, user);
        return conversionService.convert(blueprint, BlueprintResponse.class);
    }

    @ApiOperation(value = BlueprintOpDescription.GET_PUBLIC, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    @RequestMapping(value = "account/blueprints", method = RequestMethod.GET)
    @ResponseBody
    public Set<BlueprintResponse> getAccountBlueprints() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Blueprint> blueprints = defaultBlueprintLoaderService.loadBlueprints(user);
        blueprints.addAll(blueprintService.retrieveAccountBlueprints(user));
        return toJsonList(blueprints);
    }

    @ApiOperation(value = BlueprintOpDescription.GET_BY_ID, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    @RequestMapping(value = "blueprints/{id}", method = RequestMethod.GET)
    @ResponseBody
    public BlueprintResponse getBlueprint(@PathVariable Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Blueprint blueprint = blueprintService.get(id);
        return conversionService.convert(blueprint, BlueprintResponse.class);
    }

    @ApiOperation(value = BlueprintOpDescription.DELETE_BY_ID, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    @RequestMapping(value = "blueprints/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public BlueprintResponse deleteBlueprint(@PathVariable Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        blueprintService.delete(id, user);
        return new BlueprintResponse();
    }

    @ApiOperation(value = BlueprintOpDescription.DELETE_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    @RequestMapping(value = "account/blueprints/{name}", method = RequestMethod.DELETE)
    @ResponseBody
    public BlueprintResponse deleteBlueprintInAccount(@PathVariable String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        blueprintService.delete(name, user);
        return new BlueprintResponse();
    }

    @ApiOperation(value = BlueprintOpDescription.DELETE_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.BLUEPRINT_NOTES)
    @RequestMapping(value = "user/blueprints/{name}", method = RequestMethod.DELETE)
    @ResponseBody
    public BlueprintResponse deleteBlueprintInPrivate(@PathVariable String name) {
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
