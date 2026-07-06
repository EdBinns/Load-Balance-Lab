package com.edbinns.loadbalancer.models;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerInstance {
    private final String name;
    private final String url;
    private final AtomicInteger activeConnections;
    private final Integer weight;
    private final AtomicInteger currentWeight;
    private boolean healthy;
    private Instant lastHealthCheck;


    public ServerInstance(String name, String url, int weight) {
        this.name = name;
        this.url = url;
        this.weight = weight;
        this.healthy = true;
        this.lastHealthCheck = Instant.now();
        this.activeConnections = new AtomicInteger(0);
        this.currentWeight = new AtomicInteger(0);
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

    public int getActiveConnections() {
        return activeConnections.get();
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
  
    public AtomicInteger getCurrentWeight() {
        return currentWeight;
    }
        
    public int getCurrentWeightValue() {
        return currentWeight.get();
    }

    public void addToCurrentWeight(int value) {
        currentWeight.addAndGet(value);
    }

    public void subtractFromCurrentWeight(int value) {
        currentWeight.addAndGet(-value);
    }


    @Override
    public String toString() {
        return String.format(
                "%s hash=%d active=%d weight=%d  currentWeight=%d healthy=%s lastHealthCheck=%s ",
                name,
                System.identityHashCode(this),
                activeConnections.get(),
                weight,
                currentWeight.get(),
                healthy,
                lastHealthCheck
        );
    }
}
