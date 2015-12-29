package com.sequenceiq.cloudbreak.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import com.sequenceiq.cloudbreak.api.TemplateEndpoint;
import com.sequenceiq.cloudbreak.doc.ContentType;
import com.sequenceiq.cloudbreak.doc.ControllerDescription;
import com.sequenceiq.cloudbreak.doc.Notes;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions.TemplateOpDescription;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.Template;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.model.IdJson;
import com.sequenceiq.cloudbreak.model.TemplateRequest;
import com.sequenceiq.cloudbreak.model.TemplateResponse;
import com.sequenceiq.cloudbreak.service.template.DefaultTemplateLoaderService;
import com.sequenceiq.cloudbreak.service.template.TemplateService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Component
@Api(value = "/templates", description = ControllerDescription.TEMPLATE_DESCRIPTION, position = 2)
public class TemplateController implements TemplateEndpoint{
    @Autowired
    private TemplateService templateService;

    @Autowired
    private DefaultTemplateLoaderService defaultTemplateLoaderService;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Autowired
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Override
    @ApiOperation(value = TemplateOpDescription.POST_PRIVATE, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public IdJson postPrivate(TemplateRequest templateRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createTemplate(user, templateRequest, false);
    }

    @Override
    @ApiOperation(value = TemplateOpDescription.POST_PUBLIC, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public IdJson postPublic(TemplateRequest templateRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createTemplate(user, templateRequest, true);
    }

    @Override
    @ApiOperation(value = TemplateOpDescription.GET_PRIVATE, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public Set<TemplateResponse> getPrivates() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Template> templates = templateService.retrievePrivateTemplates(user);
        return convert(templates);
    }

    @Override
    @ApiOperation(value = TemplateOpDescription.GET_PUBLIC, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public Set<TemplateResponse> getPublics() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Template> templates = defaultTemplateLoaderService.loadTemplates(user);
        templates.addAll(templateService.retrieveAccountTemplates(user));
        return convert(templates);
    }

    @Override
    @ApiOperation(value = TemplateOpDescription.GET_BY_ID, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public TemplateResponse get(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Template template = templateService.get(id);
        return convert(template);
    }

    @Override
    @ApiOperation(value = TemplateOpDescription.GET_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public TemplateResponse getPrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Template template = templateService.getPrivateTemplate(name, user);
        return convert(template);
    }

    @Override
    @ApiOperation(value = TemplateOpDescription.GET_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public TemplateResponse getPublic(@PathVariable String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Template template = templateService.getPublicTemplate(name, user);
        return convert(template);
    }

    @Override
    @ApiOperation(value = TemplateOpDescription.DELETE_BY_ID, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public TemplateResponse delete(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        templateService.delete(id, user);
        return new TemplateResponse();
    }

    @Override
    @ApiOperation(value = TemplateOpDescription.DELETE_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public TemplateResponse deletePublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        templateService.delete(name, user);
        return new TemplateResponse();
    }

    @Override
    @ApiOperation(value = TemplateOpDescription.DELETE_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.TEMPLATE_NOTES)
    public TemplateResponse deletePrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        templateService.delete(name, user);
        return new TemplateResponse();
    }

    private IdJson createTemplate(CbUser user, TemplateRequest templateRequest, boolean publicInAccount) {
        Template template = convert(templateRequest, publicInAccount);
        template = templateService.create(user, template);
        return new IdJson(template.getId());
    }

    private Template convert(TemplateRequest templateRequest, boolean publicInAccount) {
        Template converted = conversionService.convert(templateRequest, Template.class);
        converted.setPublicInAccount(publicInAccount);
        return converted;
    }

    private TemplateResponse convert(Template template) {
        return conversionService.convert(template, TemplateResponse.class);
    }

    private Set<TemplateResponse> convert(Set<Template> templates) {
        Set<TemplateResponse> jsons = new HashSet<>();
        for (Template template : templates) {
            jsons.add(convert(template));
        }
        return jsons;
    }

}
