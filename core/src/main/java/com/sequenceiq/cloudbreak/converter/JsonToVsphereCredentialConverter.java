package com.sequenceiq.cloudbreak.converter;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.controller.json.CredentialRequest;
import com.sequenceiq.cloudbreak.controller.validation.VsphereCredentialParam;
import com.sequenceiq.cloudbreak.domain.VsphereCredential;

@Component
public class JsonToVsphereCredentialConverter extends AbstractConversionServiceAwareConverter<CredentialRequest, VsphereCredential> {
    
    @Override
    public VsphereCredential convert(CredentialRequest source) {
        VsphereCredential vsphereCredential = new VsphereCredential();
        vsphereCredential.setName(source.getName());
        vsphereCredential.setDescription(source.getDescription());
        String userName = String.valueOf(source.getParameters().get(VsphereCredentialParam.USER.getName()));
        vsphereCredential.setUserName(userName);
        String password = String.valueOf(source.getParameters().get(VsphereCredentialParam.PASSWORD.getName()));
        vsphereCredential.setPassword(password);
        vsphereCredential.setPublicKey(source.getPublicKey());
        return vsphereCredential;
    }

}
