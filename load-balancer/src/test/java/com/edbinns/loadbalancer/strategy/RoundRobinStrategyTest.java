package com.edbinns.loadbalancer.strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.edbinns.loadbalancer.models.ServerInstance;

public class RoundRobinStrategyTest {
    private RoundRobinStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new RoundRobinStrategy();
    }

    @Test
    void shouldReturnServersInRoundRobinOrder() {

        var a = new ServerInstance("A", "http://a", 1);
        var b = new ServerInstance("B", "http://b", 1);
        var c = new ServerInstance("C", "http://c", 1);

        var servers = List.of(a, b, c);

        assertEquals(a, strategy.selectServer(servers, null));
        assertEquals(b, strategy.selectServer(servers, null));
        assertEquals(c, strategy.selectServer(servers, null));
        assertEquals(a, strategy.selectServer(servers, null));
        assertEquals(b, strategy.selectServer(servers, null));
        
    }

}
