package com.adrian.paymentservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotBlank
    private String idempotencyKey;
    @NotBlank
    private String userId;
    @NotBlank
    private String orderId;
    @NotNull
    @Min(0)
    private BigDecimal amount;
}
