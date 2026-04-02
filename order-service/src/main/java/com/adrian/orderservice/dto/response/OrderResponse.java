package com.adrian.orderservice.dto.response;

import com.adrian.orderservice.util.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String userId;
    private BigDecimal totalAmount;
    private Status status;
}
