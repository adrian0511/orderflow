package com.adrian.orderservice.controller;

import com.adrian.orderservice.dto.request.OrderRequest;
import com.adrian.orderservice.dto.response.OrderResponse;
import com.adrian.orderservice.service.interf.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService service;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid OrderRequest request,
                                                @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.accepted().body(service.create(request, jwt.getSubject()));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.subject")
    public ResponseEntity<List<OrderResponse>> getByUserId(@PathVariable("id") String userId) {
        return ResponseEntity.ok(service.getOrdersByUserId(userId));
    }

}
