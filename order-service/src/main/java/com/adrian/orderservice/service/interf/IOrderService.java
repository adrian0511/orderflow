package com.adrian.orderservice.service.interf;

import com.adrian.orderservice.dto.request.OrderRequest;
import com.adrian.orderservice.dto.response.OrderResponse;

import java.util.List;

public interface IOrderService {

    OrderResponse create(OrderRequest request);
    
    List<OrderResponse> getOrdersByUserId(String userId);

    void paymentCompleted(String orderId);

    void paymentFailed(String orderId);
}
