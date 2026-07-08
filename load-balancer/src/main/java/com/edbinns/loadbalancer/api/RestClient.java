package com.edbinns.loadbalancer.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;


@Service
public class RestClient {
    private static final Set<String> FORWARDED_HEADERS = Set.of(
        "authorization",
        "content-type",
        "accept",
        "accept-language",
        "accept-encoding",
        "user-agent",
        "cookie",
        "origin",
        "referer"
);

    private final HttpClient httpClient;
    

    public RestClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public HttpResponse<String> send(String url, HttpServletRequest request) {

        var builder = HttpRequest.newBuilder()
                .uri(URI.create(url));

        var headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {

            var header = headerNames.nextElement();

            if (!FORWARDED_HEADERS.contains(header.toLowerCase())) {
                continue;
            }

            var values = request.getHeaders(header);

            while (values.hasMoreElements()) {
                builder.header(header, values.nextElement());
            }
        }

        builder.header("X-Forwarded-For", request.getRemoteAddr());
        builder.header("X-Forwarded-Proto", request.getScheme());
        builder.header("X-Forwarded-Host", request.getServerName());

        try {

            var method = request.getMethod().toUpperCase();

            var body = request.getInputStream().readAllBytes();

            var publisher =body.length == 0
                            ? HttpRequest.BodyPublishers.noBody()
                            : HttpRequest.BodyPublishers.ofByteArray(body);

            builder.method(method, publisher);

            return httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

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
