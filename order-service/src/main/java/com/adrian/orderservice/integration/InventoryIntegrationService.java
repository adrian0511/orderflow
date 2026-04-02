package com.adrian.orderservice.integration;

import com.adrian.orderservice.client.InventoryClient;
import com.adrian.orderservice.dto.request.InventoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryIntegrationService {

    @Autowired
    private InventoryClient client;

    public void reserveStock(InventoryRequest request) {
        client.reserveStock(request);
    }
}
