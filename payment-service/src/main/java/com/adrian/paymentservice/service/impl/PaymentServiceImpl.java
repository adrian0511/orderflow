package com.adrian.paymentservice.service.impl;

import com.adrian.paymentservice.dto.event.PaymentCompletedEvent;
import com.adrian.paymentservice.dto.event.PaymentFailedEvent;
import com.adrian.paymentservice.dto.request.PaymentRequest;
import com.adrian.paymentservice.entity.Payment;
import com.adrian.paymentservice.repository.PaymentRepository;
import com.adrian.paymentservice.service.interf.IPaymentService;
import com.adrian.paymentservice.util.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public void processPayment(PaymentRequest request) {
        if (repository.existsByIdempotencyKey(request.getIdempotencyKey())) {
            log.info("Payment already processed for key {}", request.getIdempotencyKey());
            return;
        }

        boolean success = ThreadLocalRandom.current().nextBoolean();

        Payment payment = Payment.builder()
                .idempotencyKey(request.getIdempotencyKey())
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(success ? Status.SUCCESS : Status.FAILED)
                .gatewayResponse(success ? "simulated_tx_123" : "Simulated failure")
                .build();

        repository.save(payment);

        if (success)
            kafkaTemplate.send("payment.completed", new PaymentCompletedEvent(request.getOrderId(), payment.getId()));
        else
            kafkaTemplate.send("payment.failed", new PaymentFailedEvent(request.getOrderId(), payment.getGatewayResponse()));
    }
}
