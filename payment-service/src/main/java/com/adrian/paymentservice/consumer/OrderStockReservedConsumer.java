package com.adrian.paymentservice.consumer;

import com.adrian.paymentservice.dto.event.OrderStockReservedEvent;
import com.adrian.paymentservice.dto.request.PaymentRequest;
import com.adrian.paymentservice.service.interf.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderStockReservedConsumer {

    @Autowired
    private IPaymentService service;

    @KafkaListener(topics = "order.stock.reserved", groupId = "payment-group")
    public void consume(OrderStockReservedEvent event) {
        PaymentRequest request = new PaymentRequest(
                event.getIdempotencyKey(),
                event.getUserId(),
                event.getOrderId(),
                event.getAmount()
        );

        service.processPayment(request);
    }

}
