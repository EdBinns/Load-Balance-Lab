package com.edbinns.loadbalancer.services;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.edbinns.loadbalancer.api.RestClient;
import com.edbinns.loadbalancer.config.LoadBalancerProperties;
import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.servers.InMemoryServerRegistry;
import com.edbinns.loadbalancer.strategy.RoundRobinStrategy;

class LoadBalancerServiceImplTest {

    private ServerInstance serverA;
    private LoadBalancerProperties properties;
    private InMemoryServerRegistry serverRegistry;
    private StubRestClient restClient;
    private LoadBalancerServiceImpl service;

    @BeforeEach
    void setup() {
        serverA = new ServerInstance("A", "http://a", 1);

        properties = new LoadBalancerProperties();
        properties.setStrategy("roundRobin");
        properties.setServers(List.of(serverA));

        serverRegistry = new InMemoryServerRegistry(properties);
        restClient = new StubRestClient();
        service = new LoadBalancerServiceImpl(
                restClient,
                Map.of("roundRobin", new RoundRobinStrategy()),
                serverRegistry,
                properties
        );
    }

    @Test
    void shouldForwardRequestToSelectedServer() throws InterruptedException {
        restClient.response = "Hello from A";

        String response = service.forwardRequest(null);

        assertEquals("Hello from A", response);
        assertEquals("http://a/hello", restClient.lastGetUrl);
        assertEquals(0, serverA.getActiveConnections());
    }

    @Test
    void shouldDecrementConnectionsWhenRequestFails() {
        restClient.throwOnGet = true;

        assertThrows(RuntimeException.class, () -> service.forwardRequest(null));
        assertEquals(0, serverA.getActiveConnections());
    }

    @Test
    void shouldThrowForUnknownStrategy() {
        properties.setStrategy("unknown");

        assertThrows(IllegalArgumentException.class, () -> service.forwardRequest(null));
    }

    private static class StubRestClient extends RestClient {

        String lastGetUrl;
        String response = "";
        boolean throwOnGet;

        @Override
        public String get(String url) {
            lastGetUrl = url;
            if (throwOnGet) {
                throw new RuntimeException("backend unavailable");
            }
            return response;
        }
    }
}
