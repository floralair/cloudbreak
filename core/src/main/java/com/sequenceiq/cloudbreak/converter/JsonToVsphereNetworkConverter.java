package com.sequenceiq.cloudbreak.converter;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.type.ResourceStatus;
import com.sequenceiq.cloudbreak.controller.json.NetworkJson;
import com.sequenceiq.cloudbreak.domain.VsphereNetwork;

@Component
public class JsonToVsphereNetworkConverter extends AbstractConversionServiceAwareConverter<NetworkJson, VsphereNetwork> {
    
    @Override
    public VsphereNetwork convert(NetworkJson source) {
        VsphereNetwork network = new VsphereNetwork();
        network.setName(source.getName());
        network.setDescription(source.getDescription());
        network.setSubnetCIDR(source.getSubnetCIDR());
        network.setPublicInAccount(source.isPublicInAccount());
        network.setStatus(ResourceStatus.USER_MANAGED);
        return network;
    }

}
