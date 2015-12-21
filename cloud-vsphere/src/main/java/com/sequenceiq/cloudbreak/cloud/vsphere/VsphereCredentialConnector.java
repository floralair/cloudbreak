package com.sequenceiq.cloudbreak.cloud.vsphere;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cloud.CredentialConnector;
import com.sequenceiq.cloudbreak.cloud.context.AuthenticatedContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredentialStatus;
import com.sequenceiq.cloudbreak.cloud.model.CredentialStatus;

@Service
public class VsphereCredentialConnector implements CredentialConnector {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VsphereCredentialConnector.class);

    @Override
    public CloudCredentialStatus verify(AuthenticatedContext authenticatedContext) {
        //Fake implementation.
        CloudCredential credential = authenticatedContext.getCloudCredential();
        LOGGER.info("Create credential: {}", credential);
        CloudCredentialStatus cloudCredentialStatus =  new CloudCredentialStatus(credential, CredentialStatus.CREATED);
        if (cloudCredentialStatus.getStatus().equals(CredentialStatus.FAILED)) {
            return cloudCredentialStatus;
        }
        return new CloudCredentialStatus(credential, CredentialStatus.VERIFIED);
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
