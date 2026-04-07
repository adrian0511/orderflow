package com.adrian.notificationservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmMailRequest {

    private String orderId;
    private String username;
    private String userEmail;
    private BigDecimal totalAmount;
    private String items;
    private String confirmedAt;
}
