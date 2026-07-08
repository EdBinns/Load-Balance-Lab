package com.edbinns.loadbalancer.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.edbinns.loadbalancer.services.LoadBalancerService;

class LoadBalancerControllerTest {

    @Test
    void shouldReturnMessageFromLoadBalancerService() throws InterruptedException {
        LoadBalancerService service = request -> "Hello from backend";
        LoadBalancerController controller = new LoadBalancerController(service);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.1");

        var response = controller.hello(request);

        assertEquals("Hello from backend", response.get("message"));
    }

    @Test
    void shouldReturnOkForHealthEndpoint() {
        LoadBalancerController controller = new LoadBalancerController(request -> "");

        assertEquals("OK", controller.health());
    }
}
