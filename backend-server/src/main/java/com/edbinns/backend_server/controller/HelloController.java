package com.edbinns.backend_server.controller;

import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${app.server-name}")
    private String serverName;

    @GetMapping("/hello")
    public Map<String, String> hello() throws InterruptedException {
        var random = new Random();
    
        switch (serverName) {
            case "A" -> Thread.sleep(random.nextInt(1000, 3000));
            case "B" -> Thread.sleep(random.nextInt(4000, 7000));
            case "C" -> Thread.sleep(random.nextInt(7000, 10000));
        }

        return Map.of("server", serverName);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
