package com.edbinns.loadbalancer.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edbinns.loadbalancer.services.HealthCheckService;
import com.edbinns.loadbalancer.services.LoadBalancerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class LoadBalancerController {

    private final LoadBalancerService loadBalancerService;
    private final HealthCheckService healthCheckService;

    public LoadBalancerController(LoadBalancerService loadBalancerService, HealthCheckService healthCheckService) {
        this.loadBalancerService = loadBalancerService;
        this.healthCheckService = healthCheckService;
    }

    @RequestMapping("/**")
    public ResponseEntity<?> proxy(HttpServletRequest request) throws InterruptedException {
        return ResponseEntity.ok(loadBalancerService.forwardRequest(request));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(healthCheckService.getHealthyServers());
    }
}

    
