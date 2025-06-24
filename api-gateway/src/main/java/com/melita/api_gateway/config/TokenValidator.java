package com.melita.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenValidator{
    @Value("${order.service.create-url}")
    private String createOrderUrl;

    private final GatewayFilterCustom gatewayFilterCustom;

    public TokenValidator(GatewayFilterCustom gatewayFilterCustom) {
        this.gatewayFilterCustom = gatewayFilterCustom;
    }
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("order-service",r->
                        r.path("/api/v1/orders/**")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec.filter(gatewayFilterCustom))
                                                .uri(createOrderUrl)).build();
    }

}
