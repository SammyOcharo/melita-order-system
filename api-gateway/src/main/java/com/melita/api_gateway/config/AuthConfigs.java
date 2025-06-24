package com.melita.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class AuthConfigs {

    @Value("${nimbus.jwk.setUri}")
    private String jwkSetUri;
    @Bean
    public JwtDecoder decoder(){
        NimbusJwtDecoder nimbusJwtDecoder= NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .build();
        return nimbusJwtDecoder;
    }
    @Bean
    SecurityWebFilterChain springWebFilter(ServerHttpSecurity httpSecurity){
        return httpSecurity
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec
                                .anyExchange().permitAll()).build();
    }
}
