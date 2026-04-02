package com.adrian.orderservice.integration;

import com.adrian.orderservice.client.ProductClient;
import com.adrian.orderservice.dto.response.ProductResponse;
import com.adrian.orderservice.exception.ResourceNotFoundException;
import com.adrian.orderservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductIntegrationService {

    @Autowired
    private ProductClient client;

    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetProduct")
    public ProductResponse getProduct(String id) {
        return client.getById(id);
    }

    public ProductResponse fallbackGetProduct(String id, Throwable th) {

        if (th instanceof ResourceNotFoundException)
            throw (RuntimeException) th;

        throw new ServiceUnavailableException(th.getMessage());
    }
}
