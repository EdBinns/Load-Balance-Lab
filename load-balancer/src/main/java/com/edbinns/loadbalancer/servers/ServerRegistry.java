package com.edbinns.loadbalancer.servers;

import java.util.List;

import com.edbinns.loadbalancer.models.ServerInstance;

public interface ServerRegistry {

    List<ServerInstance> getServers();

}