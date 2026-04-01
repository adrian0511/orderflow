package com.adrian.inventoryservice.controller;

import com.adrian.inventoryservice.dto.request.InventoryRequest;
import com.adrian.inventoryservice.dto.response.InventoryResponse;
import com.adrian.inventoryservice.service.interf.IInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final IInventoryService service;

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@RequestBody @Valid InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createInventory(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getByProductId(@PathVariable("id") String productId) {
        return ResponseEntity.ok(service.getByProductId(productId));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@RequestBody @Valid InventoryRequest request) {
        service.reserveStock(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/restock")
    public ResponseEntity<InventoryResponse> restock(@RequestBody @Valid InventoryRequest request) {
        return ResponseEntity.ok(service.restock(request));
    }

}
