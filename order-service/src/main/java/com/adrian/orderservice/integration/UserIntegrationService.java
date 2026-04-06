package com.adrian.orderservice.integration;

import com.adrian.orderservice.client.UserClient;
import com.adrian.orderservice.exception.ResourceNotFoundException;
import com.adrian.orderservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserIntegrationService {
    @Autowired
    private UserClient client;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackValidUserExists")
    public void validUserExists(String id) {
        client.getByUserId(id);
    }

    public void fallbackValidUserExists(String id, Throwable th) {
        if (th instanceof ResourceNotFoundException)
            throw (RuntimeException) th;

        throw new ServiceUnavailableException(th.getMessage());
    }
}
