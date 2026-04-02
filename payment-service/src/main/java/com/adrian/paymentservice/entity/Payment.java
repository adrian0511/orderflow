package com.adrian.paymentservice.entity;

import com.adrian.paymentservice.util.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    private String userId;
    private String orderId;
    
    @Column(scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;
    private String gatewayResponse;
    private LocalDateTime processedAt = LocalDateTime.now();

}
