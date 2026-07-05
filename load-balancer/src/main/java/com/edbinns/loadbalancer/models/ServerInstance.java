package com.edbinns.loadbalancer.models;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerInstance {
    private final String name;
    private final String url;
    private final AtomicInteger activeConnections;
    private final int weight;
    private boolean healthy;
    private Instant lastHealthCheck;


    public ServerInstance(String name, String url, int weight) {
        this.name = name;
        this.url = url;
        this.weight = weight;
        this.healthy = true;
        this.lastHealthCheck = Instant.now();
        this.activeConnections = new AtomicInteger(0);
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

    public AtomicInteger getActiveConnections() {
        return activeConnections;
    }

    public void incrementConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementConnections() {
        activeConnections.decrementAndGet();
    }

    public int getWeight() {
        return weight;
    }
}
