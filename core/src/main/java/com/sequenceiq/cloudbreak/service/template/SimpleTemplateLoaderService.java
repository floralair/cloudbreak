package com.sequenceiq.cloudbreak.service.template;

import static com.sequenceiq.cloudbreak.EnvironmentVariableConfig.CB_TEMPLATE_DEFAULTS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sequenceiq.cloudbreak.common.type.ResourceStatus;
import com.sequenceiq.cloudbreak.controller.json.JsonHelper;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.Template;
import com.sequenceiq.cloudbreak.model.TemplateRequest;
import com.sequenceiq.cloudbreak.repository.TemplateRepository;
import com.sequenceiq.cloudbreak.util.FileReaderUtils;
import com.sequenceiq.cloudbreak.util.JsonUtil;

@Component
public class SimpleTemplateLoaderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTemplateLoaderService.class);

    @Value("#{'${cb.template.defaults:" + CB_TEMPLATE_DEFAULTS + "}'.split(',')}")
    private List<String> templateArray;

    @Inject
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Inject
    private TemplateRepository templateRepository;

    @Inject
    private TemplateService templateService;

    @Inject
    private JsonHelper jsonHelper;

    public Set<Template> loadTemplates(CbUser user) {
        Set<Template> templates = new HashSet<>();
        if (templateRepository.findAllDefaultInAccount(user.getAccount()).isEmpty()) {
            templates.addAll(createDefaultTemplates(user));
        }
        return templates;
    }

    private Set<Template> createDefaultTemplates(CbUser user) {
        Set<Template> templates = new HashSet<>();
        for (String templateName : templateArray) {
            Template oneByName = null;
            try {
                oneByName = templateService.getPublicTemplate(templateName, user);
            } catch (Exception e) {
                oneByName = null;
            }
            if (oneByName == null) {
                try {
                    JsonNode jsonNode = jsonHelper.createJsonFromString(
                            FileReaderUtils.readFileFromClasspath(String.format("defaults/templates/%s.tmpl", templateName)));
                    TemplateRequest templateRequest = JsonUtil.treeToValue(jsonNode, TemplateRequest.class);
                    Template converted = conversionService.convert(templateRequest, Template.class);
                    converted.setAccount(user.getAccount());
                    converted.setOwner(user.getUserId());
                    converted.setPublicInAccount(true);
                    converted.setStatus(ResourceStatus.DEFAULT);
                    templateRepository.save(converted);
                    templates.add(converted);
                } catch (Exception e) {
                    LOGGER.error("Template is not available for '{}' user.", e, user);
                }
            }
        }
        return templates;
    }

}
