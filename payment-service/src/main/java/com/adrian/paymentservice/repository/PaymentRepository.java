package com.adrian.paymentservice.repository;

import com.adrian.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}
