package com.edbinns.loadbalancer.strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.edbinns.loadbalancer.models.ServerInstance;

public class RandomStrategyTest {

    private RandomStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new RandomStrategy();
    }

    @Test
    void shouldReturnServerFromList() {
        ServerInstance a = new ServerInstance("A", "http://a", 1);
        ServerInstance b = new ServerInstance("B", "http://b", 1);
        ServerInstance c = new ServerInstance("C", "http://c", 1);
        List<ServerInstance> servers = List.of(a, b, c);

        for (int i = 0; i < 50; i++) {
            ServerInstance selected = strategy.selectServer(servers, null);
            assertTrue(servers.contains(selected));
        }
    }
}
