package com.edbinns.loadbalancer.strategy;

import java.util.List;

import com.edbinns.loadbalancer.models.ServerInstance;

import jakarta.servlet.http.HttpServletRequest;

public interface LoadBalancingStrategy {
    ServerInstance selectServer(List<ServerInstance> servers, HttpServletRequest request);
}