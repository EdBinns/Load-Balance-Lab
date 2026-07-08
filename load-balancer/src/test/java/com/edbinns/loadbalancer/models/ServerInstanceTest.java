package com.edbinns.loadbalancer.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ServerInstanceTest {

    @Test
    void shouldTrackActiveConnections() {
        var server = new ServerInstance("A", "http://a", 1);

        assertEquals(0, server.getActiveConnections());

        server.incrementConnections();
        server.incrementConnections();
        assertEquals(2, server.getActiveConnections());

        server.decrementConnections();
        assertEquals(1, server.getActiveConnections());
    }

    @Test
    void shouldTrackHealthStatus() {
        var server = new ServerInstance("A", "http://a", 1);

        assertTrue(server.isHealthy());

        server.setHealthy(false);
        assertFalse(server.isHealthy());
    }

    @Test
    void shouldTrackCurrentWeight() {
        var server = new ServerInstance("A", "http://a", 3);

        assertEquals(0, server.getCurrentWeightValue());

        server.addToCurrentWeight(3);
        assertEquals(3, server.getCurrentWeightValue());

        server.subtractFromCurrentWeight(2);
        assertEquals(1, server.getCurrentWeightValue());
    }
}
