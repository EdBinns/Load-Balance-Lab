package com.edbinns.loadbalancer.services;

import jakarta.servlet.http.HttpServletRequest;

public interface LoadBalancerService {
    
    String forwardRequest(HttpServletRequest request) throws InterruptedException;
}
