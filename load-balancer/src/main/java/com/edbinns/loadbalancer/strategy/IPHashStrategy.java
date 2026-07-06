package com.edbinns.loadbalancer.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.edbinns.loadbalancer.models.ServerInstance;

import jakarta.servlet.http.HttpServletRequest;

@Component("ipHash")
public class IPHashStrategy implements LoadBalancingStrategy {

    @Override
    public ServerInstance selectServer(List<ServerInstance> servers, HttpServletRequest request) {
        var clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = request.getRemoteAddr();
        }
        var hash = Math.abs(clientIp.hashCode());

    
        var index = hash % servers.size();
        return servers.get(index);

    } 
}
