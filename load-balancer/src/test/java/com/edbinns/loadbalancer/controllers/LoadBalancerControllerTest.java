package com.edbinns.loadbalancer.controllers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.edbinns.loadbalancer.models.ServiceInstanceDTO;
import com.edbinns.loadbalancer.services.HealthCheckService;
import com.edbinns.loadbalancer.services.LoadBalancerService;

class LoadBalancerControllerTest {

    LoadBalancerService service = request -> "Hello from backend";
        HealthCheckService healthCheckService = new HealthCheckService() {
            @Override
            public void checkServers() {
                // No-op for testing
            }

            @Override
            public List<ServiceInstanceDTO> getHealthyServers() {
                return List.of();
            }
        };
    LoadBalancerController controller = new LoadBalancerController(service, healthCheckService);

    @Test
    void shouldReturnMessageFromLoadBalancerService() throws InterruptedException {
     
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.1");

        var response = controller.proxy(request);

        assertEquals("Hello from backend", response.getBody());
    }

    @Test
    void shouldReturnOkForHealthEndpoint() {
    
        ResponseEntity<?> response = controller.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(List.of(), response.getBody());
    }

    @Test
    void shouldReturnServersListWhenHealthEndpointHasHealthyServers() {
        LoadBalancerService mockLoadBalancer = request -> "";
        var mockHealthCheck = mock(HealthCheckService.class);

        var serverA = ServiceInstanceDTO.builder()
                .name("Server-A")
                .url("http://localhost:8081")
                .activeConnections(2)
                .healthy(true)
                .lastHealthCheck(Instant.now())
                .build();

        var mockHealthyList = List.of(serverA);

        when(mockHealthCheck.getHealthyServers()).thenReturn(mockHealthyList);

        var controller = new LoadBalancerController(mockLoadBalancer, mockHealthCheck);

        var response = controller.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "El cuerpo de la respuesta no debería ser nulo");
        
        var bodyList = (List<?>) response.getBody();
        assertFalse(bodyList.isEmpty(), "La lista de servidores saludables no debería estar vacía");
        assertEquals(1, bodyList.size(), "Debería retornar exactamente 1 servidor");

        var resultDto = (ServiceInstanceDTO) bodyList.get(0);
        assertEquals("Server-A", resultDto.getName());
        assertEquals("http://localhost:8081", resultDto.getUrl());
    }
}
