package com.sequenceiq.cloudbreak.converter;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.type.CloudPlatform;
import com.sequenceiq.cloudbreak.controller.json.NetworkJson;
import com.sequenceiq.cloudbreak.domain.VsphereNetwork;

@Component
public class VsphereNetworkToJsonConverter extends AbstractConversionServiceAwareConverter<VsphereNetwork, NetworkJson> {
    
    @Override
    public NetworkJson convert(VsphereNetwork source) {
        NetworkJson json = new NetworkJson();
        json.setId(source.getId().toString());
        json.setCloudPlatform(CloudPlatform.VSPHERE);
        json.setName(source.getName());
        json.setDescription(source.getDescription());
        json.setSubnetCIDR(source.getSubnetCIDR());
        json.setPublicInAccount(source.isPublicInAccount());
        return json;
    }

}
