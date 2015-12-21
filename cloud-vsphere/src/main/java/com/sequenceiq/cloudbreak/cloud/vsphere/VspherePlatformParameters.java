package com.sequenceiq.cloudbreak.cloud.vsphere;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cloud.PlatformParameters;

@Service
public class VspherePlatformParameters implements PlatformParameters {

    @Override
    public String diskPrefix() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer startLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> diskTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String defaultDiskType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> regions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String defaultRegion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, List<String>> availabiltyZones() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> virtualMachines() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String defaultVirtualMachine() {
        // TODO Auto-generated method stub
        return null;
    }

}
