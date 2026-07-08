package com.edbinns.loadbalancer.services;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.edbinns.loadbalancer.api.RestClient;
import com.edbinns.loadbalancer.config.LoadBalancerProperties;
import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.servers.InMemoryServerRegistry;

class HealthCheckServiceImplTest {

    private ServerInstance serverA;
    private StubRestClient restClient;
    private HealthCheckServiceImpl healthCheckService;

    @BeforeEach
    void setup() {
        serverA = new ServerInstance("A", "http://a", 1);

        LoadBalancerProperties properties = new LoadBalancerProperties();
        properties.setServers(List.of(serverA));

        restClient = new StubRestClient();
        healthCheckService = new HealthCheckServiceImpl(
                restClient,
                new InMemoryServerRegistry(properties)
        );
    }

    @Test
    void shouldMarkServerHealthyWhenStatusIs2xx() {
        restClient.statusCode = 200;

        healthCheckService.checkServers();

        assertTrue(serverA.isHealthy());
        assertEquals("http://a/health", restClient.lastStatusUrl);
    }

    @Test
    void shouldMarkServerUnhealthyWhenStatusIsNot2xx() {
        restClient.statusCode = 503;

        healthCheckService.checkServers();

        assertFalse(serverA.isHealthy());
    }

    @Test
    void shouldMarkServerUnhealthyWhenHealthCheckFails() {
        restClient.statusCode = -1;

        healthCheckService.checkServers();

        assertFalse(serverA.isHealthy());
    }

    @Test
    void shouldReturnMappedDtosWhenServersArePresent() {
        var result = healthCheckService.getHealthyServers();

        assertEquals(1, result.size());
        
        var dto = result.get(0);
        assertEquals("A", dto.getName() );
        assertEquals("http://a", dto.getUrl());
  
        assertEquals(serverA.isHealthy(), dto.isHealthy());
        assertEquals(serverA.getLastHealthCheck(), dto.getLastHealthCheck());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenNoServersAreAvailable() {
        var emptyProperties = new LoadBalancerProperties();
        emptyProperties.setServers(List.of()); 
        
        var emptyRegistry = new InMemoryServerRegistry(emptyProperties);
        var serviceWithNoServers = new HealthCheckServiceImpl(restClient, emptyRegistry);

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class, 
            () -> serviceWithNoServers.getHealthyServers()
        );

        assertEquals("No servers instances are available to handle requests.", exception.getMessage());
    }
    private static class StubRestClient extends RestClient {

        String lastStatusUrl;
        int statusCode = 200;

        @Override
        public int getStatus(String url) {
            lastStatusUrl = url;
            return statusCode;
        }
    }
}
