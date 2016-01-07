package com.sequenceiq.cloudbreak.cloud.vsphere;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cloud.Authenticator;
import com.sequenceiq.cloudbreak.cloud.CloudConnector;
import com.sequenceiq.cloudbreak.cloud.CredentialConnector;
import com.sequenceiq.cloudbreak.cloud.InstanceConnector;
import com.sequenceiq.cloudbreak.cloud.MetadataCollector;
import com.sequenceiq.cloudbreak.cloud.PlatformParameters;
import com.sequenceiq.cloudbreak.cloud.ResourceConnector;
import com.sequenceiq.cloudbreak.cloud.Setup;

@Service
public class VsphereConnector implements CloudConnector {

    @Inject
    private VsphereAuthenticator vsphereAuthenticator;
    @Inject
    private VsphereCredentialConnector vsphereCredentialConnector;
    @Inject
    private VsphereResourceConnector vsphereResourceConnector;
    @Inject
    private VsphereInstanceConnector vsphereInstanceConnector;
    @Inject
    private VsphereMetadataCollector vsphereMetadataCollector;
    @Inject
    private VspherePlatformParameters vspherePlatformParameters;
    @Inject
    private VsphereSetup vsphereSetup;

    @Override
    public String platform() {
        return VsphereConstants.VSPHERE;
    }

    @Override
    public String variant() {
        return VsphereConstants.VSPHERE;
    }

    @Override
    public Authenticator authentication() {
        return vsphereAuthenticator;
    }

    @Override
    public Setup setup() {
        return vsphereSetup;
    }

    @Override
    public CredentialConnector credentials() {
        return vsphereCredentialConnector;
    }

    @Override
    public ResourceConnector resources() {
        return vsphereResourceConnector;
    }

    @Override
    public InstanceConnector instances() {
        return vsphereInstanceConnector;
    }

    @Override
    public MetadataCollector metadata() {
        return vsphereMetadataCollector;
    }

    @Override
    public PlatformParameters parameters() {
        return vspherePlatformParameters;
    }

}
