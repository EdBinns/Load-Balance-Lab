package com.edbinns.loadbalancer.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

@Component("random")
public class RandomStrategy implements LoadBalancingStrategy {
    
    @Override
    public ServerInstance selectServer(List<ServerInstance> servers) {
        if (servers.isEmpty()) {
            throw new IllegalArgumentException("Server list is empty");
        }
        int randomIndex = (int) (Math.random() * servers.size());
        return servers.get(randomIndex);
    }
}
