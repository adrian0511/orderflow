package com.adrian.orderservice.client;

import com.adrian.orderservice.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path = "/api/products")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductResponse getById(@PathVariable String id);
}
