package com.edbinns.loadbalancer.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.edbinns.loadbalancer.api.RestClient;
import com.edbinns.loadbalancer.config.LoadBalancerProperties;
import com.edbinns.loadbalancer.servers.ServerRegistry;
import com.edbinns.loadbalancer.strategy.LoadBalancingStrategy;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoadBalancerServiceImpl implements LoadBalancerService {
    
    private final RestClient restClient;
    private final Map<String, LoadBalancingStrategy> strategies;
    private final ServerRegistry serverRegistry;
    private final LoadBalancerProperties properties;
    
   public LoadBalancerServiceImpl(
        RestClient restClient,
        Map<String, LoadBalancingStrategy> strategies,
        ServerRegistry serverRegistry,
        LoadBalancerProperties properties
    ) {
        this.restClient = restClient;
        this.strategies = strategies;
        this.serverRegistry = serverRegistry;
        this.properties = properties;
    }
        
    @Override
    public String forwardRequest(HttpServletRequest request) throws InterruptedException {

        var strategy = strategies.get(properties.getStrategy());

        if (strategy == null) {
        throw new IllegalArgumentException(
            "Unknown load balancing strategy: " + properties.getStrategy());
        }
        
        var server = serverRegistry.acquireServer(strategy, request);
        var api = UriComponentsBuilder.fromUriString(server.getUrl())
         .path(request.getRequestURI())
         .query(request.getQueryString())
         .build(true)
         .toUriString();
        
         System.out.println("Forwarding request to: " + api + " with method: " + request.getMethod());
        
        try {
            return restClient.send(api, request).body();
        } finally {
             server.decrementConnections();
        }
    
    }


}
