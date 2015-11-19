package com.sequenceiq.cloudbreak.core.bootstrap.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "docker")
public class ContainerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerConfiguration.class);


    private Map<String, Map<String, String>> images;

    public Map<String, Map<String, String>> getImages() {
        return images;
    }

    public void setImages(Map<String, Map<String, String>> images) {
        this.images = images;
    }
}
