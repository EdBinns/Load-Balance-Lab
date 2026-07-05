package com.edbinns.loadbalancer.models;

import java.time.Instant;

public class ServerInstance {
    private final String name;
    private final String url;
    private boolean healthy;
    private Instant lastHealthCheck;

    public ServerInstance(String name, String url, boolean isHealthy) {
        this.name = name;
        this.url = url;
        this.healthy = isHealthy;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public Instant getLastHealthCheck() {
        return lastHealthCheck;
    }

    public void setLastHealthCheck(Instant lastHealthCheck) {
        this.lastHealthCheck = lastHealthCheck;
    }
}
