package com.edbinns.loadbalancer.strategy;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

@Component("leastConnections")
public class LeastConnectionsStrategy  implements LoadBalancingStrategy {

   @Override
public ServerInstance selectServer(List<ServerInstance> servers) {

    System.out.println("\n========== LEAST CONNECTIONS ==========");

    servers.forEach(System.out::println);

    ServerInstance selected = servers.stream()
            .min(Comparator.comparingInt(s -> s.getActiveConnections()))
            .orElseThrow(() -> new IllegalStateException("No healthy servers available"));

    System.out.printf(
        ">>> Selected: %s (%d active connections)%n",
        selected.getName(),
        selected.getActiveConnections()
    );

    return selected;
}
    
}
