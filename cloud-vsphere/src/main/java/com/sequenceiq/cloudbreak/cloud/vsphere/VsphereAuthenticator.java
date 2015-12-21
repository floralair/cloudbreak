package com.sequenceiq.cloudbreak.cloud.vsphere;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cloud.Authenticator;
import com.sequenceiq.cloudbreak.cloud.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.context.CloudContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;

@Service
public class VsphereAuthenticator implements Authenticator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VsphereAuthenticator.class);

    @Override
    public String platform() {
        return VsphereConstants.VSPHERE;
    }

    @Override
    public String variant() {
        return VsphereConstants.VSPHERE;
    }

    @Override
    public AuthenticatedContext authenticate(CloudContext cloudContext, CloudCredential cloudCredential) {
        LOGGER.info("Authenticating to vSphere ...");
        // TODO Auto-generated method stub
        return null;
    }

}
