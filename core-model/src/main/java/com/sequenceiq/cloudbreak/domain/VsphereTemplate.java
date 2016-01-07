package com.sequenceiq.cloudbreak.domain;

import javax.persistence.Entity;

import com.sequenceiq.cloudbreak.common.type.CloudPlatform;

@Entity
public class VsphereTemplate extends Template implements ProvisionEntity {
    
    private String instanceType;
    
    public String getInstanceType() {
        return instanceType;
    }
    
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    @Override
    public CloudPlatform cloudPlatform() {
        return CloudPlatform.VSPHERE;
    }

    @Override
    public String getInstanceTypeName() {
        return getInstanceType();
    }

    @Override
    public String getVolumeTypeName() {
        return "HDD";
    }

}
