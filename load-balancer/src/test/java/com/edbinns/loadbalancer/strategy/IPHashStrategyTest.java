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
        var a = new ServerInstance("A", "http://a", 1);
        var b = new ServerInstance("B", "http://b", 1);
        var c = new ServerInstance("C", "http://c", 1);
        var servers = List.of(a, b, c);

        var request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");

        var first = strategy.selectServer(servers, request);
        var second = strategy.selectServer(servers, request);

        assertEquals(first, second);
    }

    @Test
    void shouldUseForwardedForHeaderWhenPresent() {
        var a = new ServerInstance("A", "http://a", 1);
        var b = new ServerInstance("B", "http://b", 1);
        var c = new ServerInstance("C", "http://c", 1);
        var servers = List.of(a, b, c);

        var forwardedIp = "203.0.113.50";
        var expectedIndex = Math.abs(forwardedIp.hashCode()) % servers.size();

        var request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", forwardedIp);
        request.setRemoteAddr("10.0.0.1");

        var selected = strategy.selectServer(servers, request);

        assertEquals(servers.get(expectedIndex), selected);
    }

    @Test
    void shouldUseRemoteAddrWhenForwardedForHeaderIsBlank() {
        var a = new ServerInstance("A", "http://a", 1);
        var b = new ServerInstance("B", "http://b", 1);
        var servers = List.of(a, b);

        var remoteIp = "172.16.0.25";
        var expectedIndex = Math.abs(remoteIp.hashCode()) % servers.size();

        var request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "   ");
        request.setRemoteAddr(remoteIp);

        var selected = strategy.selectServer(servers, request);

        assertEquals(servers.get(expectedIndex), selected);
    }
}
