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
                                .tokenRelay()
                                .circuitBreaker(cb -> cb
                                        .setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/user"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                        )
                        .uri("lb://user-service"))

                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                                .tokenRelay()
                                .circuitBreaker(cb -> cb
                                        .setName("productServiceCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/product"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                        )
                        .uri("lb://product-service"))

                .route("inventory-service", r -> r
                        .path("/api/inventories/**")
                        .filters(f -> f
                                .tokenRelay()
                                .circuitBreaker(cb -> cb
                                        .setName("inventoryServiceCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/inventory"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                        )
                        .uri("lb://inventory-service"))

                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .tokenRelay()
                                .circuitBreaker(cb -> cb
                                        .setName("orderServiceCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/order"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                        )
                        .uri("lb://order-service"))

                .route("auth-server", r -> r
                        .path("/api/auth/register", "/oauth2/**", "/login/**",
                                "/.well-known/**")
                        .filters(f -> f
                                .circuitBreaker(cb -> cb
                                        .setName("authServerCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/auth"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR)))
                        .uri("lb://auth-server")
                )

                // SWAGGER

                .route("swagger-users", r -> r
                        .path("/v3/api-docs/users")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/users", "/v3/api-docs")
                                .circuitBreaker(cb -> cb
                                        .setName("swaggerUserCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/docs")))
                        .uri("lb://user-service")
                )

                .route("swagger-products", r -> r
                        .path("/v3/api-docs/products")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/products", "/v3/api-docs")
                                .circuitBreaker(cb -> cb
                                        .setName("swaggerProductCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/docs")))
                        .uri("lb://product-service")
                )

                .route("swagger-inventories", r -> r
                        .path("/v3/api-docs/inventories")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/inventories", "/v3/api-docs")
                                .circuitBreaker(cb -> cb
                                        .setName("swaggerProductCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/docs")))
                        .uri("lb://inventory-service")
                )

                .route("swagger-orders", r -> r
                        .path("/v3/api-docs/orders")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/orders", "/v3/api-docs")
                                .circuitBreaker(cb -> cb
                                        .setName("swaggerOrderCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/docs")))
                        .uri("lb://order-service")
                )

                .route("swagger-auth", r -> r
                        .path("/v3/api-docs/auth")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/auth", "/v3/api-docs")
                                .circuitBreaker(cb -> cb
                                        .setName("swaggerAuthCircuitBreaker")
                                        .setFallbackUri("forward:/api/fallbacks/docs")))
                        .uri("lb://auth-server")
                )
                .build();
    }
}
