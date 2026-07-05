package com.edbinns.loadbalancer.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.stereotype.Service;

@Service
public class RestClient {

    private final HttpClient httpClient;

    public RestClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public String get(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            }

            throw new RuntimeException(
                    "Request failed. Status: " + response.statusCode()
                            + ", Body: " + response.body());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error calling " + url, e);
        }
    }

    public int getStatus(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );

            return response.statusCode();

        } catch (IOException | InterruptedException e) {
            return -1;
        }
    }
}
