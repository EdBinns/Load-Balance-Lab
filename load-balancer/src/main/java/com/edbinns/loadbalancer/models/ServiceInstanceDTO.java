package com.edbinns.loadbalancer.models;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceInstanceDTO {
    private final String name;
    private final String url;
    private final int activeConnections;
    private final boolean healthy;
    private final Instant lastHealthCheck;
}