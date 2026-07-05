package com.edbinns.loadbalancer.servers;

import java.util.List;

import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.strategy.LoadBalancingStrategy;

public interface ServerRegistry {

    List<ServerInstance> getServers();
    List<ServerInstance> getHealthyServers();

    void updateHealth(ServerInstance server, boolean healthy);
    ServerInstance acquireServer(LoadBalancingStrategy strategy);

}