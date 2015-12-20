package com.sequenceiq.cloudbreak.converter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.type.CloudPlatform;
import com.sequenceiq.cloudbreak.controller.json.TemplateResponse;
import com.sequenceiq.cloudbreak.controller.validation.VsphereTemplateParam;
import com.sequenceiq.cloudbreak.domain.VsphereTemplate;

@Component
public class VsphereTemplateToJsonConverter extends AbstractConversionServiceAwareConverter<VsphereTemplate, TemplateResponse> {
    
    @Override
    public TemplateResponse convert(VsphereTemplate source) {
        TemplateResponse json = new TemplateResponse();
        json.setName(source.getName());
        json.setCloudPlatform(CloudPlatform.VSPHERE);
        json.setId(source.getId());
        json.setPublicInAccount(source.isPublicInAccount());
        Map<String, Object> props = new HashMap<>();
        props.put(VsphereTemplateParam.INSTANCE_TYPE.getName(), source.getInstanceType());
        json.setParameters(props);
        json.setDescription(source.getDescription() == null ? "" : source.getDescription());
        json.setVolumeCount(source.getVolumeCount());
        json.setVolumeSize(source.getVolumeSize());
        return json;
    }

}
