package com.sequenceiq.cloudbreak.controller;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.TestEndpoint;

@Component
public class Test implements TestEndpoint{

    @Override
    public String test() {
        return "teszt";
    }
}
