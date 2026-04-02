package com.adrian.orderservice.mapper;

import com.adrian.orderservice.dto.response.OrderResponse;
import com.adrian.orderservice.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

}
