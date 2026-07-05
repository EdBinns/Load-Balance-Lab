package com.edbinns.backend_server.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${app.server-name}")
    private String serverName;

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("server", serverName);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
