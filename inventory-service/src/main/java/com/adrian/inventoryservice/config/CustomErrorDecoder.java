package com.adrian.inventoryservice.config;

import com.adrian.inventoryservice.exception.ResourceNotFoundException;
import com.adrian.inventoryservice.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        String serviceName = methodKey.split("#")[0];

        if (response.status() == 404) {
            return new ResourceNotFoundException("Resource not found in the " + serviceName);
        } else if (response.status() == 503) {
            return new ServiceUnavailableException(serviceName + " is currently unavailable. Please try again later.");
        }

        System.out.println(response.status());

        return new Default().decode(methodKey, response);
    }
}
