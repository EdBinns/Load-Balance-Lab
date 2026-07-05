package com.edbinns.loadbalancer.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

@Component("leastConnections")
public class LeastConnectionsStrategy  implements LoadBalancingStrategy {

    @Override
    public ServerInstance selectServer(List<ServerInstance> servers) {

        return servers.stream()
                .min((s1, s2) -> Integer.compare(s1.getActiveConnections().get(), s2.getActiveConnections().get()))
                .orElseThrow(() -> new IllegalStateException("No healthy servers available"));        
    }
    
}
