package com.adrian.inventoryservice.handler;

import com.adrian.inventoryservice.dto.response.ErrorResponse;
import com.adrian.inventoryservice.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInventoryNotFoundException(
            InventoryNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .service("inventory-service")
                .path(request.getRequestURI())
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .service("inventory-service")
                .path(request.getRequestURI())
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ErrorResponse error = ErrorResponse.builder()
                .service("inventory-service")
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InventoryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleInventoryAlreadyExistsException(
            InventoryAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .service("inventory-service")
                .path(request.getRequestURI())
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(
            InsufficientStockException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .service("inventory-service")
                .path(request.getRequestURI())
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(
            ServiceUnavailableException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .service("order-service")
                .path(request.getRequestURI())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler({AccessDeniedException.class,
            org.springframework.security.access.AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex,
                                                            HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .message("Access Denied")
                .path(request.getRequestURI())
                .service("inventory-service")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex,
                                                                       HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message("Invalid Credentials")
                .service("inventory-service")
                .path(request.getRequestURI())
                .status(HttpStatus.UNAUTHORIZED.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
