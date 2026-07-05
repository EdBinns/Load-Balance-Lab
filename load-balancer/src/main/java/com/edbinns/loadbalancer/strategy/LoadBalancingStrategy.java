package com.edbinns.loadbalancer.strategy;

import java.util.List;

import com.edbinns.loadbalancer.models.ServerInstance;

public interface LoadBalancingStrategy {
    ServerInstance selectServer(List<ServerInstance> servers);
}