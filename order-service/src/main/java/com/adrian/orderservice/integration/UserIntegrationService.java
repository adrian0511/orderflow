package com.adrian.orderservice.integration;

import com.adrian.orderservice.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserIntegrationService {
    @Autowired
    private UserClient client;

    public void validUserExists(String id) {
        client.getByUserId(id);
    }
}
