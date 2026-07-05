package com.edbinns.loadbalancer.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

@Component("roundRobin")
public class RoundRobinStrategy implements LoadBalancingStrategy {
    private int currentIndex = 0;

    @Override
    public ServerInstance selectServer(List<ServerInstance> servers) {
        if (servers.isEmpty()) {
            throw new IllegalArgumentException("Server list is empty");
        }
        ServerInstance selectedServer = servers.get(currentIndex);
        currentIndex = (currentIndex + 1) % servers.size();
        return selectedServer;
    }
}
