package com.edbinns.loadbalancer.strategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.edbinns.loadbalancer.models.ServerInstance;

public class WeightRoundRobinStrategyTest {

    private WeightRoundRobinStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new WeightRoundRobinStrategy();
    }

    @Test
    void shouldReturnServersInRoundRobinOrderWhenWeightsAreEqual() {
        ServerInstance a = new ServerInstance("A", "http://a", 1);
        ServerInstance b = new ServerInstance("B", "http://b", 1);
        ServerInstance c = new ServerInstance("C", "http://c", 1);
        List<ServerInstance> servers = List.of(a, b, c);

        assertEquals(a, strategy.selectServer(servers, null));
        assertEquals(b, strategy.selectServer(servers, null));
        assertEquals(c, strategy.selectServer(servers, null));
        assertEquals(a, strategy.selectServer(servers, null));
        assertEquals(b, strategy.selectServer(servers, null));
        assertEquals(c, strategy.selectServer(servers, null));
    }

    @Test
    void shouldDistributeSelectionsAccordingToWeight() {
        ServerInstance a = new ServerInstance("A", "http://a", 5);
        ServerInstance b = new ServerInstance("B", "http://b", 1);
        ServerInstance c = new ServerInstance("C", "http://c", 1);
        List<ServerInstance> servers = List.of(a, b, c);

        int countA = 0;
        int countB = 0;
        int countC = 0;

        for (int i = 0; i < 7; i++) {
            ServerInstance selected = strategy.selectServer(servers, null);
            if (selected == a) {
                countA++;
            } else if (selected == b) {
                countB++;
            } else {
                countC++;
            }
        }

        assertEquals(5, countA);
        assertEquals(1, countB);
        assertEquals(1, countC);
    }
}
