package com.adrian.orderservice.integration;

import com.adrian.orderservice.client.ProductClient;
import com.adrian.orderservice.dto.response.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductIntegrationService {

    @Autowired
    private ProductClient client;

    public ProductResponse getProduct(String id) {
        return client.getById(id);
    }
}
