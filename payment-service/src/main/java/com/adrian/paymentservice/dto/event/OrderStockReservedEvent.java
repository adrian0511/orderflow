package com.adrian.paymentservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStockReservedEvent {
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String paymentToken;
    private String idempotencyKey;
}
