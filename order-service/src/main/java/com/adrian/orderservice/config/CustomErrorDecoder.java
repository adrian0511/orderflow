package com.adrian.orderservice.config;

import com.adrian.orderservice.exception.BusinessConflictException;
import com.adrian.orderservice.exception.ResourceNotFoundException;
import com.adrian.orderservice.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        String serviceName = methodKey.split("#")[0];

        return switch (response.status()) {
            case 404 -> new ResourceNotFoundException("Resource not found in the " + serviceName);

            case 409 -> new BusinessConflictException("Conflict in the " + serviceName);

            case 503 ->
                    new ServiceUnavailableException(serviceName + " is currently unavailable. Please try again later.");

            default -> new Default().decode(methodKey, response);
        };
    }
}
