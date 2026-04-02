package com.adrian.orderservice.consumer;

import com.adrian.orderservice.dto.event.PaymentCompletedEvent;
import com.adrian.orderservice.dto.event.PaymentFailedEvent;
import com.adrian.orderservice.service.interf.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    @Autowired
    private IOrderService service;

    @KafkaListener(topics = "payment.completed", groupId = "order-group")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        service.paymentCompleted(event.getOrderId());
    }

    @KafkaListener(topics = "payment.failed", groupId = "order-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        service.paymentFailed(event.getOrderId());
    }

}
