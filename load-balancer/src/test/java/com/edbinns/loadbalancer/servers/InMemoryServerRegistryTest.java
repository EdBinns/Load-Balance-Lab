package com.edbinns.loadbalancer.servers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.edbinns.loadbalancer.config.LoadBalancerProperties;
import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.strategy.RoundRobinStrategy;

class InMemoryServerRegistryTest {

    private LoadBalancerProperties properties;
    private InMemoryServerRegistry registry;
    private ServerInstance serverA;
    private ServerInstance serverB;

    @BeforeEach
    void setup() {
        serverA = new ServerInstance("A", "http://a", 1);
        serverB = new ServerInstance("B", "http://b", 1);

        properties = new LoadBalancerProperties();
        properties.setServers(List.of(serverA, serverB));

        registry = new InMemoryServerRegistry(properties);
    }

    @Test
    void shouldReturnConfiguredServers() {
        assertEquals(List.of(serverA, serverB), registry.getServers());
    }

    @Test
    void shouldReturnOnlyHealthyServers() {
        serverB.setHealthy(false);

        assertEquals(List.of(serverA), registry.getHealthyServers());
    }

    @Test
    void shouldUpdateServerHealth() {
        registry.updateHealth(serverA, false);

        assertFalse(serverA.isHealthy());
        assertTrue(serverA.getLastHealthCheck() != null);
    }

    @Test
    void shouldAcquireServerAndIncrementConnections() {
        var strategy = new RoundRobinStrategy();

        var selected = registry.acquireServer(strategy, null);

        assertEquals(serverA, selected);
        assertEquals(1, serverA.getActiveConnections());
    }

    @Test
    void shouldThrowWhenNoHealthyServersAreAvailable() {
        serverA.setHealthy(false);
        serverB.setHealthy(false);

        assertThrows(
                IllegalStateException.class,
                () -> registry.acquireServer(new RoundRobinStrategy(), null)
        );
    }

}
