package com.adrian.inventoryservice.consumer;

import com.adrian.inventoryservice.dto.event.OrderCompletedEvent;
import com.adrian.inventoryservice.dto.event.OrderFailedEvent;
import com.adrian.inventoryservice.dto.request.InventoryRequest;
import com.adrian.inventoryservice.service.interf.IInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    @Autowired
    private IInventoryService service;

    @KafkaListener(topics = "order.completed", groupId = "inventory-group")
    public void handleOrderCompleted(OrderCompletedEvent event) {
        InventoryRequest request = new InventoryRequest(
                event.getProductId(),
                event.getQuantity()
        );

        service.confirm(request);
        log.info("Order confirm with product: {}", event.getProductId());
    }

    @KafkaListener(topics = "order.failed", groupId = "inventory-group")
    public void handleOrderFailed(OrderFailedEvent event) {
        InventoryRequest request = new InventoryRequest(
                event.getProductId(),
                event.getQuantity()
        );
        service.release(request);

        log.info("Order with product {} failed", event.getProductId());
    }


}
