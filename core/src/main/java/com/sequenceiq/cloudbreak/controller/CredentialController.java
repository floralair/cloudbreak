package com.sequenceiq.cloudbreak.controller;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.CredentialEndpoint;
import com.sequenceiq.cloudbreak.doc.ContentType;
import com.sequenceiq.cloudbreak.doc.ControllerDescription;
import com.sequenceiq.cloudbreak.doc.Notes;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions.CredentialOpDescription;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.Credential;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.model.CredentialRequest;
import com.sequenceiq.cloudbreak.model.CredentialResponse;
import com.sequenceiq.cloudbreak.model.IdJson;
import com.sequenceiq.cloudbreak.service.credential.CredentialService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Component
@Api(value = "/credentials", description = ControllerDescription.CREDENTIAL_DESCRIPTION, position = 1)
public class CredentialController implements CredentialEndpoint {

    @Resource
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Override
    @ApiOperation(value = CredentialOpDescription.POST_PRIVATE, produces = ContentType.JSON, notes = Notes.CREDENTIAL_NOTES)
    public IdJson postPrivate(CredentialRequest credentialRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createCredential(user, credentialRequest, false);
    }

    @Override
    @ApiOperation(value = CredentialOpDescription.POST_PUBLIC, produces = ContentType.JSON, notes = Notes.CREDENTIAL_NOTES)
    public IdJson postPublic(CredentialRequest credentialRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createCredential(user, credentialRequest, true);
    }

    @Override
    @ApiOperation(value = CredentialOpDescription.GET_PRIVATE, produces = ContentType.JSON, notes = Notes.CREDENTIAL_NOTES)
    public Set<CredentialResponse> getPrivates() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Credential> credentials = credentialService.retrievePrivateCredentials(user);
        return convertCredentials(credentials);
    }

    @Override
    @ApiOperation(value = CredentialOpDescription.GET_PUBLIC, produces = ContentType.JSON, notes = Notes.CREDENTIAL_NOTES)
    public Set<CredentialResponse> getPublics() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Credential> credentials = credentialService.retrieveAccountCredentials(user);
        return convertCredentials(credentials);
    }

    @Override
    @ApiOperation(value = CredentialOpDescription.GET_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.CREDENTIAL_NOTES)
    public CredentialResponse getPrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Credential credentials = credentialService.getPrivateCredential(name, user);
        return convert(credentials);
    }

    @Override
    @ApiOperation(value = CredentialOpDescription.GET_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.CREDENTIAL_NOTES)
    public CredentialResponse getPublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Credential credentials = credentialService.getPublicCredential(name, user);
        return convert(credentials);
    }

    @Override
    @ApiOperation(value = CredentialOpDescription.GET_BY_ID, produces = ContentType.JSON, notes = Notes.CREDENTIAL_NOTES)
    public CredentialResponse get(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Credential credential = credentialService.get(id);
        return convert(credential);
    }

    @Override
    @ApiOperation(value = CredentialOpDescription.DELETE_BY_ID, produces = ContentType.JSON, notes = Notes.CREDENTIAL_NOTES)
    public CredentialResponse delete(@PathParam("id") Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        credentialService.delete(id, user);
        return new CredentialResponse();
    }

    @Override
    @ApiOperation(value = CredentialOpDescription.DELETE_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.CREDENTIAL_NOTES)
    public CredentialResponse deletePublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        credentialService.delete(name, user);
        return new CredentialResponse();
    }

    @Override
    public CredentialResponse deletePrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        credentialService.delete(name, user);
        return new CredentialResponse();
    }

    private IdJson createCredential(CbUser user, CredentialRequest credentialRequest, boolean publicInAccount) {
        Credential credential = convert(credentialRequest, publicInAccount);
        credential = credentialService.create(user, credential);
        return new IdJson(credential.getId());
    }

    private Credential convert(CredentialRequest json, boolean publicInAccount) {
        Credential converted = conversionService.convert(json, Credential.class);
        converted.setPublicInAccount(publicInAccount);
        return converted;
    }

    private CredentialResponse convert(Credential credential) {
        return conversionService.convert(credential, CredentialResponse.class);
    }

    private Set<CredentialResponse> convertCredentials(Set<Credential> credentials) {
        Set<CredentialResponse> jsonSet = new HashSet<>();
        for (Credential credential : credentials) {
            jsonSet.add(convert(credential));
        }
        return jsonSet;
    }
}
