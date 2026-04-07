package com.adrian.notificationservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailedMailRequest {
    private String orderId;
    private String username;
    private String userEmail;
    private String totalAmount;
    private String failedAt;
    private String errorMessage;
}
