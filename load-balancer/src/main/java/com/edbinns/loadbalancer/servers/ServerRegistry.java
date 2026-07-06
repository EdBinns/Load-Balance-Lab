package com.edbinns.loadbalancer.servers;

import java.util.List;

import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.strategy.LoadBalancingStrategy;

import jakarta.servlet.http.HttpServletRequest;

public interface ServerRegistry {

    List<ServerInstance> getServers();
    List<ServerInstance> getHealthyServers();

    void updateHealth(ServerInstance server, boolean healthy);
    ServerInstance acquireServer(LoadBalancingStrategy strategy, HttpServletRequest request);

}