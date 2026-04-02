package com.adrian.inventoryservice.integration;

import com.adrian.inventoryservice.client.ProductClient;
import com.adrian.inventoryservice.exception.ResourceNotFoundException;
import com.adrian.inventoryservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductIntegrationService {

    @Autowired
    private ProductClient client;

    @CircuitBreaker(name = "productService", fallbackMethod = "fallBackExistsProductById")
    public void existsProductById(String productId) {
        client.getById(productId);
    }

    public void fallBackExistsProductById(String productId, Throwable th) {
        if (th instanceof ResourceNotFoundException)
            throw (RuntimeException) th;

        throw new ServiceUnavailableException(th.getMessage());
    }

}
