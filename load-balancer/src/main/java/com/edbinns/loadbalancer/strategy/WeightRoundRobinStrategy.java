package com.edbinns.loadbalancer.strategy;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

@Component("weightedRoundRobin")
public class WeightRoundRobinStrategy implements LoadBalancingStrategy {

    @Override
    public ServerInstance selectServer(List<ServerInstance> servers) {
                
        if (servers.isEmpty()) {
            throw new IllegalStateException("No healthy servers available");
        }

        int totalWeight = servers.stream()
        .mapToInt(ServerInstance::getWeight)
        .sum();

        for (ServerInstance server : servers) {
            server.addToCurrentWeight(server.getWeight());
        }

        ServerInstance selected = servers.stream()
        .max(Comparator.comparingInt(ServerInstance::getCurrentWeightValue))
        .orElseThrow();

        selected.subtractFromCurrentWeight(totalWeight);
        return selected;
    }
    
}
