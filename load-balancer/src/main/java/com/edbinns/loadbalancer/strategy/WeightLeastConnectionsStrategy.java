package com.edbinns.loadbalancer.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

@Component("weightedLeastConnections")
public class WeightLeastConnectionsStrategy implements LoadBalancingStrategy {

    @Override
    public ServerInstance selectServer(List<ServerInstance> servers) {

        if (servers.isEmpty()) {
            throw new IllegalStateException("No healthy servers available");
        }

      return servers.stream()
            .min((s1, s2) -> {
                long left  = (long) s1.getActiveConnections() * s2.getWeight();
                long right = (long) s2.getActiveConnections() * s1.getWeight();

                return Long.compare(left, right);
            })
            .orElseThrow(() ->
                    new IllegalStateException("No healthy servers available"));
    }
    
}
