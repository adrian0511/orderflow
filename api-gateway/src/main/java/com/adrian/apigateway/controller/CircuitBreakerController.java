package com.adrian.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fallbacks")
public class CircuitBreakerController {


    @RequestMapping("/user")
    public ResponseEntity<Map<String, String>> userFallback() {
        return fallbackResponse("user-service");
    }

    @RequestMapping("/product")
    public ResponseEntity<Map<String, String>> productFallback() {
        return fallbackResponse("product-service");
    }

    @RequestMapping("/inventory")
    public ResponseEntity<Map<String, String>> inventoryFallback() {
        return fallbackResponse("inventory-service");
    }

    @RequestMapping("/order")
    public ResponseEntity<Map<String, String>> orderFallback() {
        return fallbackResponse("order-service");
    }

    public ResponseEntity<Map<String, String>> docsFallback() {
        return fallbackResponse("API Docs");
    }

    private ResponseEntity<Map<String, String>> fallbackResponse(String service) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "503");
        response.put("message", "The service " + service + " is not available. Try again later.");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

}
