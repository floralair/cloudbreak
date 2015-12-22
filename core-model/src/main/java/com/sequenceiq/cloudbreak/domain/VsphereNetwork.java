package com.sequenceiq.cloudbreak.domain;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

import com.sequenceiq.cloudbreak.common.type.CloudPlatform;

@Entity
public class VsphereNetwork extends Network {

    @Override
    public List<CloudPlatform> cloudPlatform() {
        return Arrays.asList(CloudPlatform.VSPHERE);
    }

}
