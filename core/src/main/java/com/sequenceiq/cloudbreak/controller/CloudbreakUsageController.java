package com.sequenceiq.cloudbreak.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.UsageEndpoint;
import com.sequenceiq.cloudbreak.doc.ContentType;
import com.sequenceiq.cloudbreak.doc.ControllerDescription;
import com.sequenceiq.cloudbreak.doc.Notes;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions.UsagesOpDescription;
import com.sequenceiq.cloudbreak.domain.CbUsageFilterParameters;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.facade.CloudbreakUsagesFacade;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.model.CloudbreakUsageJson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Component
@Api(value = "/usages", description = ControllerDescription.USAGES_DESCRIPTION, position = 6)
public class CloudbreakUsageController implements UsageEndpoint {

    @Autowired
    private CloudbreakUsagesFacade cloudbreakUsagesFacade;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Override
    @ApiOperation(value = UsagesOpDescription.GET_ALL, produces = ContentType.JSON, notes = Notes.USAGE_NOTES)
    public List<CloudbreakUsageJson> getDeployer(
            Long since,
            Long filterEndDate,
            String userId,
            String accountId,
            String cloud,
            String zone) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        CbUsageFilterParameters params = new CbUsageFilterParameters.Builder().setAccount(accountId).setOwner(userId)
                .setSince(since).setCloud(cloud).setRegion(zone).setFilterEndDate(filterEndDate).build();
        List<CloudbreakUsageJson> usages = cloudbreakUsagesFacade.getUsagesFor(params);
        return usages;
    }

    @ApiOperation(value = UsagesOpDescription.GET_PUBLIC, produces = ContentType.JSON, notes = Notes.USAGE_NOTES)
    public List<CloudbreakUsageJson> getAccount(
            Long since,
            Long filterEndDate,
            String userId,
            String cloud,
            String zone) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        CbUsageFilterParameters params = new CbUsageFilterParameters.Builder().setAccount(user.getAccount()).setOwner(userId)
                .setSince(since).setCloud(cloud).setRegion(zone).setFilterEndDate(filterEndDate).build();
        List<CloudbreakUsageJson> usages = cloudbreakUsagesFacade.getUsagesFor(params);
        return usages;
    }

    @Override
    @ApiOperation(value = UsagesOpDescription.GET_PRIVATE, produces = ContentType.JSON, notes = Notes.USAGE_NOTES)
    public List<CloudbreakUsageJson> getUser(
            Long since,
            Long filterEndDate,
            String cloud,
            String zone) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        CbUsageFilterParameters params = new CbUsageFilterParameters.Builder().setAccount(user.getAccount()).setOwner(user.getUserId())
                .setSince(since).setCloud(cloud).setRegion(zone).setFilterEndDate(filterEndDate).build();
        List<CloudbreakUsageJson> usages = cloudbreakUsagesFacade.getUsagesFor(params);
        return usages;
    }

    @Override
    @ApiOperation(value = UsagesOpDescription.GENERATE, produces = ContentType.JSON, notes = Notes.USAGE_NOTES)
    public List<CloudbreakUsageJson> generate() {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        cloudbreakUsagesFacade.generateUserUsages();
        return new ArrayList<>();
    }
}
