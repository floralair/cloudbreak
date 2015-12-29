package com.sequenceiq.cloudbreak.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.StackEndpoint;
import com.sequenceiq.cloudbreak.cloud.model.PlatformVariants;
import com.sequenceiq.cloudbreak.doc.ContentType;
import com.sequenceiq.cloudbreak.doc.ControllerDescription;
import com.sequenceiq.cloudbreak.doc.Notes;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions.StackOpDescription;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.domain.StackValidation;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.model.AmbariAddressJson;
import com.sequenceiq.cloudbreak.model.IdJson;
import com.sequenceiq.cloudbreak.model.PlatformVariantsJson;
import com.sequenceiq.cloudbreak.model.StackRequest;
import com.sequenceiq.cloudbreak.model.StackResponse;
import com.sequenceiq.cloudbreak.model.StackValidationRequest;
import com.sequenceiq.cloudbreak.model.UpdateStackJson;
import com.sequenceiq.cloudbreak.service.TlsSecurityService;
import com.sequenceiq.cloudbreak.service.account.AccountPreferencesValidationFailed;
import com.sequenceiq.cloudbreak.service.account.AccountPreferencesValidator;
import com.sequenceiq.cloudbreak.service.decorator.Decorator;
import com.sequenceiq.cloudbreak.service.stack.CloudParameterService;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Component
@Api(value = "/stack", description = ControllerDescription.STACK_DESCRIPTION, position = 3)
public class StackController implements StackEndpoint {

    @Autowired
    private StackService stackService;

    @Autowired
    private TlsSecurityService tlsSecurityService;

    @Autowired
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Autowired
    private Decorator<Stack> stackDecorator;

    @Autowired
    private AccountPreferencesValidator accountPreferencesValidator;

    @Autowired
    private CloudParameterService parameterService;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Override
    @ApiOperation(value = StackOpDescription.POST_PRIVATE, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public IdJson postPrivate(StackRequest stackRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createStack(user, stackRequest, false);
    }

    @Override
    @ApiOperation(value = StackOpDescription.POST_PUBLIC, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public IdJson postPublic(StackRequest stackRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        return createStack(user, stackRequest, true);
    }

    @Override
    @ApiOperation(value = StackOpDescription.GET_PRIVATE, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public Set<StackResponse> getPrivates() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Stack> stacks = stackService.retrievePrivateStacks(user);
        return convertStacks(stacks);
    }

    @ApiOperation(value = StackOpDescription.GET_PUBLIC, produces = ContentType.JSON, notes = "")
    protected Set<StackResponse> convertStacks(Set<Stack> stacks) {
        return (Set<StackResponse>) conversionService.convert(stacks, TypeDescriptor.forObject(stacks),
                TypeDescriptor.collection(Set.class, TypeDescriptor.valueOf(StackResponse.class)));
    }

    @Override
    @ApiOperation(value = StackOpDescription.GET_PUBLIC, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public Set<StackResponse> getPublics() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Set<Stack> stacks = stackService.retrieveAccountStacks(user);
        return convertStacks(stacks);
    }

    @Override
    @ApiOperation(value = StackOpDescription.GET_BY_ID, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public StackResponse get(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Stack stack = stackService.get(id);
        StackResponse stackJson = conversionService.convert(stack, StackResponse.class);
        return stackJson;
    }

    @Override
    @ApiOperation(value = StackOpDescription.GET_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public StackResponse getPrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Stack stack = stackService.getPrivateStack(name, user);
        StackResponse stackJson = conversionService.convert(stack, StackResponse.class);
        return stackJson;
    }

    @Override
    @ApiOperation(value = StackOpDescription.GET_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public StackResponse getPublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Stack stack = stackService.getPublicStack(name, user);
        StackResponse stackJson = conversionService.convert(stack, StackResponse.class);
        return stackJson;
    }

    @Override
    @ApiOperation(value = StackOpDescription.GET_STATUS_BY_ID, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public Map<String, Object> status(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        Map<String, Object> statusMap = conversionService.convert(stackService.get(id), Map.class);
        return statusMap;
    }

    @Override
    @ApiOperation(value = StackOpDescription.DELETE_BY_ID, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public StackResponse delete(Long id, Boolean forced) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        if (forced) {
            stackService.forceDelete(id, user);
        } else {
            stackService.delete(id, user);
        }
        return new StackResponse();
    }

    @Override
    @ApiOperation(value = StackOpDescription.DELETE_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public StackResponse deletePrivate(String name, Boolean forced) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        if (forced) {
            stackService.forceDelete(name, user);
        } else {
            stackService.delete(name, user);
        }
        return new StackResponse();
    }

    @Override
    @ApiOperation(value = StackOpDescription.DELETE_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public StackResponse deletePublic(String name, Boolean forced) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        if (forced) {
            stackService.forceDelete(name, user);
        } else {
            stackService.delete(name, user);
        }
        return new StackResponse();
    }

    @Override
    @ApiOperation(value = StackOpDescription.PUT_BY_ID, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public String put(Long id, UpdateStackJson updateRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        if (updateRequest.getStatus() != null) {
            stackService.updateStatus(id, updateRequest.getStatus());
            return "";
        } else {
            Integer scalingAdjustment = updateRequest.getInstanceGroupAdjustment().getScalingAdjustment();
            validateAccountPreferences(id, scalingAdjustment);
            stackService.updateNodeCount(id, updateRequest.getInstanceGroupAdjustment());
            return "";
        }
    }

    @Override
    @ApiOperation(value = StackOpDescription.GET_BY_AMBARI_ADDRESS, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public StackResponse getStackForAmbari(AmbariAddressJson json) {
        Stack stack = stackService.get(json.getAmbariAddress());
        return conversionService.convert(stack, StackResponse.class);
    }

    @Override
    @ApiOperation(value = StackOpDescription.VALIDATE, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public IdJson validate(StackValidationRequest stackValidationRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        StackValidation stackValidation = conversionService.convert(stackValidationRequest, StackValidation.class);
        stackService.validateStack(stackValidation);
        return new IdJson();
    }

    @Override
    @ApiOperation(value = StackOpDescription.DELETE_INSTANCE_BY_ID, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public String deleteInstance(Long stackId, String instanceId) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildMdcContext(user);
        stackService.removeInstance(user, stackId, instanceId);
        return "";
    }

    @Override
    @ApiOperation(value = StackOpDescription.GET_PLATFORM_VARIANTS, produces = ContentType.JSON, notes = Notes.STACK_NOTES)
    public PlatformVariantsJson variants() {
        PlatformVariants pv = parameterService.getPlatformVariants();
        return conversionService.convert(pv, PlatformVariantsJson.class);
    }

    private IdJson createStack(CbUser user, StackRequest stackRequest, boolean publicInAccount) {
        Stack stack = conversionService.convert(stackRequest, Stack.class);
        MDCBuilder.buildMdcContext(stack);
        stack = stackDecorator.decorate(stack, stackRequest.getCredentialId(), stackRequest.getConsulServerCount(), stackRequest.getNetworkId(),
                stackRequest.getSecurityGroupId());
        stack.setPublicInAccount(publicInAccount);
        validateAccountPreferences(stack, user);
        stack = stackService.create(user, stack);
        return new IdJson(stack.getId());
    }

    private void validateAccountPreferences(Stack stack, CbUser user) {
        try {
            accountPreferencesValidator.validate(stack, user.getAccount(), user.getUserId());
        } catch (AccountPreferencesValidationFailed e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    private void validateAccountPreferences(Long stackId, Integer scalingAdjustment) {
        try {
            accountPreferencesValidator.validate(stackId, scalingAdjustment);
        } catch (AccountPreferencesValidationFailed e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

}
