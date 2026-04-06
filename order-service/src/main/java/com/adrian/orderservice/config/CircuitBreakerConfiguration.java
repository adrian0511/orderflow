package com.adrian.orderservice.config;

import com.adrian.orderservice.exception.BusinessConflictException;
import com.adrian.orderservice.exception.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreaker userServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .ignoreExceptions(ResourceNotFoundException.class)
                .build();
        return registry.circuitBreaker("userService", config);
    }

    @Bean
    public CircuitBreaker productServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .ignoreExceptions(ResourceNotFoundException.class)
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();

        return registry.circuitBreaker("productService", config);
    }

    @Bean
    public CircuitBreaker inventoryServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .ignoreExceptions(
                        ResourceNotFoundException.class,
                        BusinessConflictException.class
                )
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();
        return registry.circuitBreaker("inventoryService", config);
    }
}