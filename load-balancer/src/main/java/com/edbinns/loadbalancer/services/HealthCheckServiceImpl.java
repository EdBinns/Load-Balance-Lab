package com.edbinns.loadbalancer.services;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.edbinns.loadbalancer.api.RestClient;
import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.models.ServiceInstanceDTO;
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
            var healthy = isHealthy(server);
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
            var status = restClient.getStatus(server.getUrl() + "/health");
            System.out.printf("[HealthCheck] %s -> Status: %d%n", server.getName(), status);
            return status >= 200 && status < 300;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<ServiceInstanceDTO> getHealthyServers() {
        var servers = serverRegistry.getServers();
        
        if (servers == null || servers.isEmpty()) {
            throw new RuntimeException("No servers instances are available to handle requests.");
        }
        
        return servers.stream().map(server -> {
            return ServiceInstanceDTO.builder()
                .name(server.getName())
                .url(server.getUrl())
                .healthy(server.isHealthy())
                .lastHealthCheck(server.getLastHealthCheck())
                .activeConnections(server.getActiveConnections())
                .build();
        }).toList();
    }
}
