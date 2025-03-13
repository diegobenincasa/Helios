package br.lightbase.helios.bus_integra.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class ProxyController {

    @Value("${integrasiafi.token}")
    private String apiToken;

    private final WebClient webClient;

    ProxyController(WebClient webClient) {
        this.webClient = webClient;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/**")
    public Mono<ResponseEntity<String>> proxyRequest(ServerWebExchange exchange, @RequestBody(required = false) Mono<String> body) {

        // Build the request URL by removing the "/proxy/api" context path
        String requestUrl = exchange.getRequest().getURI().getPath().replaceFirst("/proxy/api", "");
        
        // Append query parameters if any
        String queryParams = exchange.getRequest().getURI().getQuery();
        if (queryParams != null) {
            requestUrl += "?" + queryParams;
        }

        // Get HTTP method and headers
        HttpMethod method = exchange.getRequest().getMethod();
        HttpHeaders headers = new HttpHeaders();
        exchange.getRequest().getHeaders().forEach(headers::put);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken);

        // Prepare WebClient request spec
        WebClient.RequestBodySpec requestSpec = webClient.method(method)
                .uri(requestUrl)
                .headers(h -> h.addAll(headers));

        Mono<ResponseEntity<String>> responseEntityMono;

        // Check the method type to handle body for POST, PUT, and PATCH requests
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            responseEntityMono = requestSpec.body(body != null ? body : Mono.empty(), String.class)
                    .retrieve()
                    .toEntity(String.class);  // This returns Mono<ResponseEntity<String>>
        } else {
            responseEntityMono = requestSpec.retrieve().toEntity(String.class);  // Same here
        }

        // Return the response as a Mono<ResponseEntity<String>>
        return responseEntityMono.flatMap(responseEntity -> {

            // Prepare response headers
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.addAll(responseEntity.getHeaders());

            // Ensure Transfer-Encoding is 'identity' and remove any chunked encoding
            responseHeaders.set(HttpHeaders.TRANSFER_ENCODING, "identity");
            responseHeaders.remove(HttpHeaders.TRANSFER_ENCODING); // Ensure chunked encoding is removed

            // Build and return a new ResponseEntity
            return Mono.just(ResponseEntity.status(responseEntity.getStatusCode())
                    .headers(responseHeaders)
                    .body(responseEntity.getBody())
            );
        });
    }
}
