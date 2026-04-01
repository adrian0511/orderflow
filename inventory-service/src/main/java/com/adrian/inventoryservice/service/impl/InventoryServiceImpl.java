package com.adrian.inventoryservice.service.impl;

import com.adrian.inventoryservice.dto.request.InventoryRequest;
import com.adrian.inventoryservice.dto.response.InventoryResponse;
import com.adrian.inventoryservice.entity.Inventory;
import com.adrian.inventoryservice.exception.InsufficientStockException;
import com.adrian.inventoryservice.exception.InventoryAlreadyExistsException;
import com.adrian.inventoryservice.exception.InventoryNotFoundException;
import com.adrian.inventoryservice.mapper.InventoryMapper;
import com.adrian.inventoryservice.repository.InventoryRepository;
import com.adrian.inventoryservice.service.interf.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements IInventoryService {

    private final InventoryRepository repository;
    private final InventoryMapper mapper;


    @Override
    @Transactional
    public InventoryResponse createInventory(InventoryRequest request) {

        if (repository.findByProductId(request.getProductId()).isPresent())
            throw new InventoryAlreadyExistsException("Inventory already exists by product id:" + request.getProductId());

        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .reserved(0)
                .build();

        return mapper.toResponse(repository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse restock(InventoryRequest request) {
        Inventory inventory = repository.findByProductId(request.getProductId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found with product id: " + request.getProductId()));

        inventory.setQuantity(request.getQuantity() + inventory.getQuantity());

        return mapper.toResponse(repository.save(inventory));
    }

    @Override
    @Transactional
    public void reserveStock(InventoryRequest request) {
        Inventory inventory = repository.findByProductId(request.getProductId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found with product id: " + request.getProductId()));

        if (inventory.getQuantity() < request.getQuantity())
            throw new InsufficientStockException("Insufficient Stock for product with id: " + request.getProductId());

        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventory.setReserved(inventory.getReserved() + request.getQuantity());

        repository.save(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(String productId) {
        return repository.findByProductId(productId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found with product id: " + productId));
    }

    @Override
    @Transactional
    public void confirm(InventoryRequest request) {
        Inventory inventory = repository.findByProductId(request.getProductId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found with product id: " + request.getProductId()));

        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());

        repository.save(inventory);

    }

    @Override
    @Transactional
    public void release(InventoryRequest request) {
        Inventory inventory = repository.findByProductId(request.getProductId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found with product id: " + request.getProductId()));

        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());

        repository.save(inventory);

    }
}
