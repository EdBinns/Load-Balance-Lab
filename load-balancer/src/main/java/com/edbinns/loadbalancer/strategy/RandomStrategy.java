package com.edbinns.loadbalancer.strategy;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

import jakarta.servlet.http.HttpServletRequest;

@Component("random")
public class RandomStrategy implements LoadBalancingStrategy {
    
    @Override
    public ServerInstance selectServer(List<ServerInstance> servers,HttpServletRequest request) {
        var randomIndex = new Random().nextInt(servers.size());
        return servers.get(randomIndex);
    }
}
