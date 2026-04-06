package com.adrian.inventoryservice.client;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path = "/api/products", configuration = ProductFeignConfig.class)
public interface ProductClient {

    @GetMapping("/{id}")
    void getById(@PathVariable String id);
}


@Configuration
class ProductFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAuthenticationToken.getToken().getTokenValue());
        };
    }

}