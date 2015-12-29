package com.sequenceiq.cloudbreak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.AccountPreferencesEndpoint;
import com.sequenceiq.cloudbreak.common.type.CbUserRole;
import com.sequenceiq.cloudbreak.doc.ContentType;
import com.sequenceiq.cloudbreak.doc.ControllerDescription;
import com.sequenceiq.cloudbreak.doc.Notes;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions;
import com.sequenceiq.cloudbreak.domain.AccountPreferences;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.model.AccountPreferencesJson;
import com.sequenceiq.cloudbreak.service.account.AccountPreferencesService;
import com.sequenceiq.cloudbreak.service.account.ScheduledAccountPreferencesValidator;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Component
@Api(value = "/accountpreferences", description = ControllerDescription.ACCOUNT_PREFERENCES_DESCRIPTION)
public class AccountPreferencesController implements AccountPreferencesEndpoint {

    @Autowired
    private AccountPreferencesService service;

    @Autowired
    private ScheduledAccountPreferencesValidator validator;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Autowired
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Override
    @ApiOperation(value = OperationDescriptions.AccountPreferencesDescription.GET_PRIVATE, produces = ContentType.JSON, notes = Notes.ACCOUNT_PREFERENCES_NOTES)
    public AccountPreferencesJson get() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        AccountPreferences preferences = service.getOneByAccount(user);
        return convert(preferences);
    }

    @Override
    @ApiOperation(value = OperationDescriptions.AccountPreferencesDescription.PUT_PRIVATE, produces = ContentType.JSON, notes = Notes.ACCOUNT_PREFERENCES_NOTES)
    public String put(AccountPreferencesJson updateRequest) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        service.saveOne(user, convert(updateRequest));
        return "";
    }

    @Override
    @ApiOperation(value = OperationDescriptions.AccountPreferencesDescription.VALIDATE, produces = ContentType.JSON, notes = Notes.ACCOUNT_PREFERENCES_NOTES)
    public String validate() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        if (user.getRoles().contains(CbUserRole.ADMIN)) {
            validator.validate();
        }
        return "";
    }

    private AccountPreferencesJson convert(AccountPreferences preferences) {
        return conversionService.convert(preferences, AccountPreferencesJson.class);
    }

    private AccountPreferences convert(AccountPreferencesJson preferences) {
        return conversionService.convert(preferences, AccountPreferences.class);
    }
}
