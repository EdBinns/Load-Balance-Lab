package com.edbinns.loadbalancer.strategy;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

@Component("random")
public class RandomStrategy implements LoadBalancingStrategy {
    
    @Override
    public ServerInstance selectServer(List<ServerInstance> servers) {
        if (servers.isEmpty()) {
            throw new IllegalArgumentException("Server list is empty");
        }
        var randomIndex = new Random().nextInt(servers.size());

        var selected = servers.get(randomIndex);
         System.out.printf(
        ">>> Selected: %s (%d active connections)%n",
        selected.getName(),
        selected.getActiveConnections()
        );

        return selected;
    }
}
