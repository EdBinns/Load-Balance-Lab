package com.edbinns.loadbalancer.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edbinns.loadbalancer.services.LoadBalancerService;

@RestController
public class LoadBalancerController {

    private final LoadBalancerService loadBalancerService;

    public LoadBalancerController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", loadBalancerService.forwardRequest());
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

    
