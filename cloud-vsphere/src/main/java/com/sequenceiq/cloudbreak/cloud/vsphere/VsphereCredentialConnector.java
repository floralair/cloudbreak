package com.sequenceiq.cloudbreak.cloud.vsphere;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cloud.CredentialConnector;
import com.sequenceiq.cloudbreak.cloud.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredentialStatus;

@Service
public class VsphereCredentialConnector implements CredentialConnector {

    @Override
    public CloudCredentialStatus verify(AuthenticatedContext authenticatedContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CloudCredentialStatus create(AuthenticatedContext authenticatedContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CloudCredentialStatus delete(AuthenticatedContext authenticatedContext) {
        // TODO Auto-generated method stub
        return null;
    }

}
