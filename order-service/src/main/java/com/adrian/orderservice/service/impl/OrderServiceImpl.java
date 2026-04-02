package com.adrian.orderservice.service.impl;

import com.adrian.orderservice.dto.event.OrderCompletedEvent;
import com.adrian.orderservice.dto.event.OrderFailedEvent;
import com.adrian.orderservice.dto.event.OrderStockReservedEvent;
import com.adrian.orderservice.dto.request.InventoryRequest;
import com.adrian.orderservice.dto.request.OrderItemRequest;
import com.adrian.orderservice.dto.request.OrderRequest;
import com.adrian.orderservice.dto.response.OrderResponse;
import com.adrian.orderservice.dto.response.ProductResponse;
import com.adrian.orderservice.entity.Order;
import com.adrian.orderservice.entity.OrderItem;
import com.adrian.orderservice.exception.OrderNotFoundException;
import com.adrian.orderservice.integration.InventoryIntegrationService;
import com.adrian.orderservice.integration.ProductIntegrationService;
import com.adrian.orderservice.integration.UserIntegrationService;
import com.adrian.orderservice.mapper.OrderMapper;
import com.adrian.orderservice.repository.OrderRepository;
import com.adrian.orderservice.service.interf.IOrderService;
import com.adrian.orderservice.util.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final UserIntegrationService userIntegrationService;
    private final InventoryIntegrationService inventoryIntegrationService;
    private final ProductIntegrationService productIntegrationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {

        userIntegrationService.validUserExists(request.getUserId());

        Order order = Order.builder()
                .userId(request.getUserId())
                .status(Status.CREATED)
                .paymentToken(request.getPaymentToken())
                .createdAt(LocalDateTime.now())
                .build();

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductResponse product = productIntegrationService.getProduct(itemRequest.getProductId());
            OrderItem item = new OrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setOrder(order);
            items.add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setItems(items);
        order.setTotalAmount(total);

        for (OrderItem item : items) {
            InventoryRequest reserveRequest = new InventoryRequest();
            reserveRequest.setProductId(item.getProductId());
            reserveRequest.setQuantity(item.getQuantity());
            inventoryIntegrationService.reserveStock(reserveRequest);
        }

        order.setStatus(Status.STOCK_RESERVED);
        order = repository.save(order);

        OrderStockReservedEvent event = OrderStockReservedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .amount(order.getTotalAmount())
                .paymentToken(order.getPaymentToken())
                .idempotencyKey(order.getId())
                .build();

        kafkaTemplate.send("order.stock.reserved", event);

        return mapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void paymentCompleted(String orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        order.setStatus(Status.COMPLETED);
        repository.save(order);

        for (OrderItem item : order.getItems()) {
            OrderCompletedEvent completedEvent = OrderCompletedEvent.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .build();

            kafkaTemplate.send("order.completed", completedEvent);
        }

        log.info("Order {} confirmed after payment", orderId);
    }

    @Override
    @Transactional
    public void paymentFailed(String orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        order.setStatus(Status.FAILED);
        repository.save(order);

        for (OrderItem item : order.getItems()) {
            OrderFailedEvent failedEvent = OrderFailedEvent.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .build();

            kafkaTemplate.send("order.failed", failedEvent);
        }

        log.info("Order {} failed", orderId);
    }
}
