package com.edbinns.loadbalancer.strategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

import jakarta.servlet.http.HttpServletRequest;

@Component("roundRobin")
public class RoundRobinStrategy implements LoadBalancingStrategy {
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServerInstance selectServer(List<ServerInstance> servers,HttpServletRequest request) {
        var current = currentIndex.getAndIncrement();
        var index = current % servers.size();

        System.out.printf(
            "counter=%d index=%d size=%d server=%s%n",
            current,
            index,
            servers.size(),
            servers.get(index).getName()
        );

        return servers.get(index);
    }
}
