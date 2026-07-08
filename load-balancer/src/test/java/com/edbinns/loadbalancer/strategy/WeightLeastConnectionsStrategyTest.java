package com.edbinns.loadbalancer.strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.edbinns.loadbalancer.models.ServerInstance;

public class WeightLeastConnectionsStrategyTest {

    private WeightLeastConnectionsStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new WeightLeastConnectionsStrategy();
    }

    @Test
    void shouldReturnServerWithLowestWeightedConnectionScore() {
        var a = new ServerInstance("A", "http://a", 1);
        var b = new ServerInstance("B", "http://b", 2);

        for (int i = 0; i < 5; i++) {
            a.incrementConnections();
        }
        for (int i = 0; i < 4; i++) {
            b.incrementConnections();
        }

        var selected = strategy.selectServer(List.of(a, b), null);

        assertEquals(b, selected);
    }

    @Test
    void shouldFavorHigherWeightServerWhenConnectionsAreEqual() {
        var lowWeight = new ServerInstance("low", "http://low", 1);
        var highWeight = new ServerInstance("high", "http://high", 4);

        for (int i = 0; i < 8; i++) {
            lowWeight.incrementConnections();
            highWeight.incrementConnections();
        }

        var selected = strategy.selectServer(List.of(lowWeight, highWeight), null);

        assertEquals(highWeight, selected);
    }
}
