package com.sequenceiq.cloudbreak.domain;

import javax.persistence.Entity;

import com.sequenceiq.cloudbreak.common.type.CloudPlatform;

@Entity
public class VsphereCredential extends Credential implements ProvisionEntity {
    
    private String userName;
    private String password;

    public VsphereCredential() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public CloudPlatform cloudPlatform() {
        return CloudPlatform.VSPHERE;
    }

}
