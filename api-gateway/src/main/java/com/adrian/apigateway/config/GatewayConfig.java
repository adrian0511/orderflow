package com.adrian.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class GatewayConfig {

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                        .circuitBreaker(cb -> cb
                                                .setName("userServiceCircuitBreaker")
                                                .setFallbackUri("forward:/api/fallbacks/user"))
                                        .retry(retryConfig -> retryConfig.setRetries(3)
                                                .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                                // .tokenRelay()
                        )
                        .uri("lb://user-service"))

                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                                        .circuitBreaker(cb -> cb
                                                .setName("productServiceCircuitBreaker")
                                                .setFallbackUri("forward:/api/fallbacks/product"))
                                        .retry(retryConfig -> retryConfig.setRetries(3)
                                                .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                                //.tokenRelay()
                        )
                        .uri("lb://product-service"))

                .route("inventory-service", r -> r
                        .path("/api/inventories/**")
                        .filters(f -> f
                                        .circuitBreaker(cb -> cb
                                                .setName("inventoryServiceCircuitBreaker")
                                                .setFallbackUri("forward:/api/fallbacks/inventory"))
                                        .retry(retryConfig -> retryConfig.setRetries(3)
                                                .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                                //.tokenRelay()
                        )
                        .uri("lb://inventory-service"))

                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                        .circuitBreaker(cb -> cb
                                                .setName("orderServiceCircuitBreaker")
                                                .setFallbackUri("forward:/api/fallbacks/order"))
                                        .retry(retryConfig -> retryConfig.setRetries(3)
                                                .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                                //        .tokenRelay()
                        )
                        .uri("lb://order-service"))
                .build();
    }
}
