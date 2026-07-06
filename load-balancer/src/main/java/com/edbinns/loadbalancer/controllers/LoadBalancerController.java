package com.edbinns.loadbalancer.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edbinns.loadbalancer.services.LoadBalancerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class LoadBalancerController {

    private final LoadBalancerService loadBalancerService;

    public LoadBalancerController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @GetMapping("/hello")
    public Map<String, String> hello(HttpServletRequest request) throws InterruptedException {
        return Map.of("message", loadBalancerService.forwardRequest(request));
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

    
