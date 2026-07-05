package com.edbinns.loadbalancer.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edbinns.loadbalancer.api.RestClient;
import com.edbinns.loadbalancer.models.ServerInstance;

@Service
public class LoadBalancerServiceImpl implements LoadBalancerService {
    
    private final RestClient restClient;

    private final List<ServerInstance> servers = List.of(
    new ServerInstance("A", "http://localhost:8081"),
    new ServerInstance("B", "http://localhost:8082"),
    new ServerInstance("C", "http://localhost:8083"));

    public LoadBalancerServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String forwardRequest() {
        ServerInstance server = servers.getFirst();
        var api = server.url() + "/hello";
        
        return restClient.get(api);
    
    }


}
