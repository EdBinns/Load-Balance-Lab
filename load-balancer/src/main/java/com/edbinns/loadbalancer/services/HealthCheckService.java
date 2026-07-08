package com.edbinns.loadbalancer.services;

import java.util.List;

import com.edbinns.loadbalancer.models.ServiceInstanceDTO;

public interface HealthCheckService {
    void checkServers();
    List<ServiceInstanceDTO> getHealthyServers();
}
