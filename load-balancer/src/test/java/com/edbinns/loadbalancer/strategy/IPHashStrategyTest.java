package com.edbinns.loadbalancer.strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.edbinns.loadbalancer.models.ServerInstance;

public class IPHashStrategyTest {

    private IPHashStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new IPHashStrategy();
    }

    @Test
    void shouldReturnSameServerForSameIp() {
        ServerInstance a = new ServerInstance("A", "http://a", 1);
        ServerInstance b = new ServerInstance("B", "http://b", 1);
        ServerInstance c = new ServerInstance("C", "http://c", 1);
        List<ServerInstance> servers = List.of(a, b, c);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");

        ServerInstance first = strategy.selectServer(servers, request);
        ServerInstance second = strategy.selectServer(servers, request);

        assertEquals(first, second);
    }

    @Test
    void shouldUseForwardedForHeaderWhenPresent() {
        ServerInstance a = new ServerInstance("A", "http://a", 1);
        ServerInstance b = new ServerInstance("B", "http://b", 1);
        ServerInstance c = new ServerInstance("C", "http://c", 1);
        List<ServerInstance> servers = List.of(a, b, c);

        String forwardedIp = "203.0.113.50";
        int expectedIndex = Math.abs(forwardedIp.hashCode()) % servers.size();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", forwardedIp);
        request.setRemoteAddr("10.0.0.1");

        ServerInstance selected = strategy.selectServer(servers, request);

        assertEquals(servers.get(expectedIndex), selected);
    }

    @Test
    void shouldUseRemoteAddrWhenForwardedForHeaderIsBlank() {
        ServerInstance a = new ServerInstance("A", "http://a", 1);
        ServerInstance b = new ServerInstance("B", "http://b", 1);
        List<ServerInstance> servers = List.of(a, b);

        String remoteIp = "172.16.0.25";
        int expectedIndex = Math.abs(remoteIp.hashCode()) % servers.size();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "   ");
        request.setRemoteAddr(remoteIp);

        ServerInstance selected = strategy.selectServer(servers, request);

        assertEquals(servers.get(expectedIndex), selected);
    }
}
