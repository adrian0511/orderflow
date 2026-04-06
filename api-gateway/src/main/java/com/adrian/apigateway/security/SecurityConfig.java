package com.adrian.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    @Order(1)
    public SecurityWebFilterChain clientFilterChain(ServerHttpSecurity http) {
        http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                        "/oauth2/**",
                        "/login/**",
                        "/api/auth/register",
                        "/.well-known/**"
                ))
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .oauth2Login(oauth2 -> {
                })
                .oauth2Client(oauth2 -> {
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain resourceServerFilterChain(ServerHttpSecurity http) {
        http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/fallbacks/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                        })
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

}
