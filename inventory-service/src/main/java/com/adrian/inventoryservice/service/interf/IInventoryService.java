package com.adrian.inventoryservice.service.interf;

import com.adrian.inventoryservice.dto.request.InventoryRequest;
import com.adrian.inventoryservice.dto.response.InventoryResponse;

public interface IInventoryService {

    InventoryResponse createInventory(InventoryRequest request);

    InventoryResponse restock(InventoryRequest request);

    void reserveStock(InventoryRequest request);

    InventoryResponse getByProductId(String productId);

    void confirm(InventoryRequest request);

    void release(InventoryRequest request);

}
