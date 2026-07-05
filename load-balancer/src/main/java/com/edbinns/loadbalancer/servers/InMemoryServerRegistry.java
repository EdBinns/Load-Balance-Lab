package com.edbinns.loadbalancer.servers;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edbinns.loadbalancer.config.LoadBalancerProperties;
import com.edbinns.loadbalancer.models.ServerInstance;

@Service
public class InMemoryServerRegistry implements ServerRegistry {
    private final LoadBalancerProperties properties;

    public InMemoryServerRegistry(LoadBalancerProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<ServerInstance> getServers() {
        return properties.getServers();
    }

    @SuppressWarnings("null")
    @Override
    public List<ServerInstance> getHealthyServers() {
        return properties.getServers().stream()
                .filter(ServerInstance::isHealthy)
                .toList();
    }

    @Override
    public void updateHealth(ServerInstance server, boolean healthy) {
        server.setHealthy(healthy);
        server.setLastHealthCheck(java.time.Instant.now());
    }
}
