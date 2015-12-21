package com.sequenceiq.cloudbreak.cloud.vsphere;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.Setup;
import com.sequenceiq.cloudbreak.cloud.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudStack;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.notification.PersistenceNotifier;
import com.sequenceiq.cloudbreak.common.type.ImageStatusResult;

@Component
public class VsphereSetup implements Setup {

    @Override
    public void prepareImage(AuthenticatedContext authenticatedContext, Image image) {
        // TODO Auto-generated method stub

    }

    @Override
    public ImageStatusResult checkImageStatus(AuthenticatedContext authenticatedContext, Image image) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void prerequisites(AuthenticatedContext authenticatedContext, CloudStack stack, PersistenceNotifier persistenceNotifier) {
        // TODO Auto-generated method stub

    }

}
