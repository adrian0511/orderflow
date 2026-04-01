package com.adrian.inventoryservice.mapper;

import com.adrian.inventoryservice.dto.response.InventoryResponse;
import com.adrian.inventoryservice.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryResponse toResponse(Inventory inventory);

}
