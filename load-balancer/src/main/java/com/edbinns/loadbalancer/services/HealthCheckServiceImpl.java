package com.edbinns.loadbalancer.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.edbinns.loadbalancer.api.RestClient;
import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.servers.ServerRegistry;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    private final RestClient restClient;
    private final ServerRegistry serverRegistry;

    public HealthCheckServiceImpl(RestClient restClient, ServerRegistry serverRegistry) {
        this.restClient = restClient;
        this.serverRegistry = serverRegistry;
    }

    @Override
    @Scheduled(fixedRate = 5000)
    public void checkServers() {      
        for (ServerInstance server : serverRegistry.getServers()) {
            boolean healthy = isHealthy(server);
            serverRegistry.updateHealth(server, healthy);

            System.out.printf(
            "[HealthCheck] %s -> %s %s%n",
            server.getName(),
            healthy ? "UP" : "DOWN",
            server.getLastHealthCheck()
        );
        }
    }

    private boolean isHealthy(ServerInstance server) {
        try {
            int status = restClient.getStatus(server.getUrl() + "/health");
            System.out.printf("[HealthCheck] %s -> Status: %d%n", server.getName(), status);
            return status >= 200 && status < 300;
        } catch (Exception e) {
            return false;
        }
    }
    
}
