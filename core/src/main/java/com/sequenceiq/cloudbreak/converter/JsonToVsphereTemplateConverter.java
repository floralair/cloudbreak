package com.sequenceiq.cloudbreak.converter;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.type.ResourceStatus;
import com.sequenceiq.cloudbreak.controller.json.TemplateRequest;
import com.sequenceiq.cloudbreak.controller.validation.VsphereTemplateParam;
import com.sequenceiq.cloudbreak.domain.VsphereTemplate;

@Component
public class JsonToVsphereTemplateConverter extends AbstractConversionServiceAwareConverter<TemplateRequest, VsphereTemplate> {

    @Override
    public VsphereTemplate convert(TemplateRequest source) {
        VsphereTemplate template = new VsphereTemplate();
        template.setName(source.getName());
        template.setInstanceType(String.valueOf(source.getParameters().get(VsphereTemplateParam.INSTANCE_TYPE.getName())));
        template.setDescription(source.getDescription());
        template.setStatus(ResourceStatus.USER_MANAGED);
        template.setVolumeCount((source.getVolumeCount() == null) ? 0 : source.getVolumeCount());
        template.setVolumeSize((source.getVolumeSize() == null) ? 0 : source.getVolumeSize());
        return template;
    }
    
}
