package com.adrian.orderservice.client;

import com.adrian.orderservice.dto.request.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", path = "/api/inventories", url = "http://localhost:8003")
public interface InventoryClient {

    @PostMapping("/reserve")
    void reserveStock(@RequestBody InventoryRequest request);
}
