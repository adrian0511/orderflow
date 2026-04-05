package com.adrian.userservice.controller;

import com.adrian.userservice.dto.request.UserRequest;
import com.adrian.userservice.dto.response.UserResponse;
import com.adrian.userservice.service.interf.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.subject")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/by-username")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.claims['username']")
    public ResponseEntity<UserResponse> getByUsername(@RequestParam String username) {
        return ResponseEntity.ok(service.getByUsername(username));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.subject")
    public ResponseEntity<UserResponse> update(@PathVariable String id, @RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
