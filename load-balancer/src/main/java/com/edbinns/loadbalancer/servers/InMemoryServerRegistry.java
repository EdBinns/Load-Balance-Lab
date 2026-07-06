package com.edbinns.loadbalancer.servers;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

import com.edbinns.loadbalancer.config.LoadBalancerProperties;
import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.strategy.LoadBalancingStrategy;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class InMemoryServerRegistry implements ServerRegistry {
    private final LoadBalancerProperties properties;
    private final ReentrantLock lock;

    public InMemoryServerRegistry(LoadBalancerProperties properties) {
        this.properties = properties;
        this.lock = new ReentrantLock();
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

    @Override
    public ServerInstance acquireServer(LoadBalancingStrategy strategy, HttpServletRequest request) {
        lock.lock();

        try {

            var servers = getHealthyServers();
            if (servers.isEmpty()) {
                throw new IllegalStateException("No healthy servers available");
            }
            ServerInstance server = strategy.selectServer(servers, request);

            System.out.println(server);
            server.incrementConnections();

            return server;

        } finally {
            lock.unlock();
        }
    }
}
