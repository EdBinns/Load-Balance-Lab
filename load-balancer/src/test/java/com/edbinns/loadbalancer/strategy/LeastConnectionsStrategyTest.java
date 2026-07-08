package com.edbinns.loadbalancer.strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.edbinns.loadbalancer.models.ServerInstance;

public class LeastConnectionsStrategyTest {

    private LeastConnectionsStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new LeastConnectionsStrategy();
    }

    @Test
    void shouldReturnServerWithLeastConnections() {
        ServerInstance a = new ServerInstance("A", "http://a", 1);
        ServerInstance b = new ServerInstance("B", "http://b", 1);
        ServerInstance c = new ServerInstance("C", "http://c", 1);

        for (int i = 0; i < 5; i++) {
            a.incrementConnections();
        }
        for (int i = 0; i < 2; i++) {
            b.incrementConnections();
        }
        for (int i = 0; i < 7; i++) {
            c.incrementConnections();
        }

        ServerInstance selected = strategy.selectServer(List.of(a, b, c), null);

        assertEquals(b, selected);
    }
}
