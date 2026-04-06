package com.adrian.orderservice.dto.event;

import com.adrian.orderservice.dto.response.OrderItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCompletedEvent {
    private String orderId;
    private String userId;
    private String username;
    private String userEmail;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private LocalDateTime confirmedAt;
}
