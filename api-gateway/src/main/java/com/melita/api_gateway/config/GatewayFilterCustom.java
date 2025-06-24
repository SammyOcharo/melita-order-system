package com.melita.api_gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Configuration
public class GatewayFilterCustom implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorizedResponse(exchange, "Missing or malformed Authorization header");
            }

            String token = authHeader.substring(7);
            jwtDecoder.decode(token); // might throw

            return chain.filter(exchange);
        } catch (JwtException | IllegalArgumentException e) {
            return unauthorizedResponse(exchange, "Invalid or expired token: " + e.getMessage());
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"timestamp\":\"%s\",\"statusCode\":401,\"statusMessage\":\"Unauthorized\",\"message\":\"%s\"}",
                LocalDateTime.now(), message);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private final JwtDecoder jwtDecoder;
    public GatewayFilterCustom(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }
}
