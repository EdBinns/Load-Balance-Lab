package com.edbinns.loadbalancer.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.edbinns.loadbalancer.models.ServerInstance;

@ConfigurationProperties(prefix = "loadbalancer")
public class LoadBalancerProperties {
    private String strategy;

    private List<ServerInstance> servers;

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public List<ServerInstance> getServers() {
        return servers;
    }

    public void setServers(List<ServerInstance> servers) {
        this.servers = servers;
    }
}
