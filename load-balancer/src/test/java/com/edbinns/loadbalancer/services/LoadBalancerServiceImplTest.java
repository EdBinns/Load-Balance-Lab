package com.edbinns.loadbalancer.services;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.edbinns.loadbalancer.api.RestClient;
import com.edbinns.loadbalancer.config.LoadBalancerProperties;
import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.servers.InMemoryServerRegistry;
import com.edbinns.loadbalancer.strategy.RoundRobinStrategy;

import jakarta.servlet.http.HttpServletRequest;

class LoadBalancerServiceImplTest {

    private ServerInstance serverA;
    private LoadBalancerProperties properties;
    private InMemoryServerRegistry serverRegistry;
    private StubRestClient restClient;
    private LoadBalancerServiceImpl service;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setup() {
        serverA = new ServerInstance("A", "http://a", 1);

        properties = new LoadBalancerProperties();
        properties.setStrategy("roundRobin");
        properties.setServers(List.of(serverA));

        serverRegistry = new InMemoryServerRegistry(properties);
        restClient = new StubRestClient();
        
        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/hello");
        when(mockRequest.getQueryString()).thenReturn(null);
        when(mockRequest.getMethod()).thenReturn("GET");

        service = new LoadBalancerServiceImpl(
                restClient,
                Map.of("roundRobin", new RoundRobinStrategy()),
                serverRegistry,
                properties
        );
    }

    @Test
    void shouldForwardRequestToSelectedServer() throws InterruptedException {
        String response = service.forwardRequest(mockRequest);

        assertEquals("Hello from A", response);
        assertEquals("http://a/hello", restClient.lastGetUrl);
        assertEquals(0, serverA.getActiveConnections());
    }

    @Test
    void shouldDecrementConnectionsWhenRequestFails() {
        restClient.throwOnGet = true;

        assertThrows(RuntimeException.class, () -> service.forwardRequest(mockRequest));
        assertEquals(0, serverA.getActiveConnections());
    }

    @Test
    void shouldThrowForUnknownStrategy() {
        properties.setStrategy("unknown");

        assertThrows(IllegalArgumentException.class, () -> service.forwardRequest(mockRequest));
    }

    private static class StubRestClient extends RestClient {

        String lastGetUrl;
        boolean throwOnGet;
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        public StubRestClient() {
            when(httpResponse.body()).thenReturn("Hello from A");
        }

        @Override
        public HttpResponse<String> send(String url, HttpServletRequest request) {
            lastGetUrl = url;
            if (throwOnGet) {
                throw new RuntimeException("backend unavailable");
            }
            return httpResponse;
        }
    }
}