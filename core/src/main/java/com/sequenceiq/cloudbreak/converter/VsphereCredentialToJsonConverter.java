package com.sequenceiq.cloudbreak.converter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.type.CloudPlatform;
import com.sequenceiq.cloudbreak.controller.json.CredentialResponse;
import com.sequenceiq.cloudbreak.domain.VsphereCredential;

@Component
public class VsphereCredentialToJsonConverter extends AbstractConversionServiceAwareConverter<VsphereCredential, CredentialResponse> {
    
    @Override
    public CredentialResponse convert(VsphereCredential source) {
        CredentialResponse credentialJson = new CredentialResponse();
        credentialJson.setId(source.getId());
        credentialJson.setCloudPlatform(CloudPlatform.VSPHERE);
        credentialJson.setName(source.getName());
        credentialJson.setDescription(source.getDescription());
        credentialJson.setPublicKey(source.getPublicKey());
        credentialJson.setPublicInAccount(source.isPublicInAccount());
        Map<String, Object> parameters = new HashMap<>();
        credentialJson.setParameters(parameters);
        credentialJson.setLoginUserName(source.getLoginUserName());
        return credentialJson;
    }

}
