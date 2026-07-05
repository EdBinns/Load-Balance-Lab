package com.edbinns.loadbalancer.services;

public interface LoadBalancerService {
    
    String forwardRequest() throws InterruptedException;
}
