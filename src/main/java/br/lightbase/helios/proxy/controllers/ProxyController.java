package br.lightbase.helios.proxy.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import br.lightbase.helios.authentication.service.AuthenticationService;
import br.lightbase.helios.logs.entities.RequestLog;
import br.lightbase.helios.logs.services.LogService;
import reactor.core.publisher.Mono;

@RestController
public class ProxyController {

    @Value("${integrasiafi.token}")
    private String apiToken;

    private final WebClient webClient;
    private final LogService logService;
    private final AuthenticationService authenticationService;

    ProxyController(WebClient webClient, LogService logService, AuthenticationService authenticationService) {
        this.webClient = webClient;
        this.logService = logService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/**")
    public Mono<ResponseEntity<String>> proxyRequest(ServerWebExchange exchange, @RequestBody(required = false) Mono<String> body) {

        // Extract request details
        String requestUrl = exchange.getRequest().getURI().getPath().replaceFirst("/proxy/api", "");
        String queryParams = exchange.getRequest().getURI().getQuery();
        if (queryParams != null) {
            requestUrl += "?" + queryParams;
        }

        HttpMethod method = exchange.getRequest().getMethod();
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        String username = extractUsernameFromToken(authHeader);

        // Determine if request is allowed
        boolean isAllowed = method == HttpMethod.GET;
        RequestLog requestLog = new RequestLog();
        requestLog.setUsername(username);
        requestLog.setUri(requestUrl);
        requestLog.setMethod(method.name());
        requestLog.setPassed(isAllowed);

        if (!isAllowed) {
            body.doOnNext(requestLog::setBody).subscribe();
            logService.save(requestLog);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Blocked by proxy"));
        }

        // Prepare request for forwarding
        WebClient.RequestBodySpec requestSpec = webClient.method(method)
                .uri(requestUrl)
                .headers(h -> h.addAll(headers))
                .headers(h -> h.set(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken));

        Mono<String> requestBodyMono = body != null ? body.defaultIfEmpty("") : Mono.just("");

        return requestBodyMono.flatMap(requestBody -> {
            requestLog.setBody(requestBody);

            return requestSpec.exchangeToMono(response -> {
                HttpStatusCode status = response.statusCode();
            
                return response.bodyToMono(String.class)
                        .defaultIfEmpty("") // Ensures empty responses don't cause issues
                        .doOnSuccess(bd -> {
                            requestLog.setStatus(status.value());
                            logService.save(requestLog);
                        })
                        .map(bdy -> ResponseEntity.status(status).body(bdy));
            });
            
        });
    }

    private String extractUsernameFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return "anonymous";
        }
        String extractedToken = token.substring(7);
        return authenticationService.extractUsername(extractedToken); 
    }
}
