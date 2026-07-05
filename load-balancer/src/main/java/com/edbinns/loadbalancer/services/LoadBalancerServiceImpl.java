package com.edbinns.loadbalancer.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.edbinns.loadbalancer.api.RestClient;
import com.edbinns.loadbalancer.models.ServerInstance;
import com.edbinns.loadbalancer.strategy.LoadBalancingStrategy;

@Service
public class LoadBalancerServiceImpl implements LoadBalancerService {
    
    private final RestClient restClient;
    private final Map<String, LoadBalancingStrategy> strategies;
    
    @Value("${loadbalancer.strategy}")
    private String currentStrategy;

    private final List<ServerInstance> servers = List.of(
    new ServerInstance("A", "http://localhost:8081"),
    new ServerInstance("B", "http://localhost:8082"),
    new ServerInstance("C", "http://localhost:8083"));
    
   public LoadBalancerServiceImpl(
        RestClient restClient,
        Map<String, LoadBalancingStrategy> strategies
    ) {
        this.restClient = restClient;
        this.strategies = strategies;
    }
        
    @Override
    public String forwardRequest() {

        var strategy = strategies.get(currentStrategy);
        var server = strategy.selectServer(servers);
        var api = server.url() + "/hello";
        
        return restClient.get(api);
    
    }


}
