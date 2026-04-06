package com.adrian.orderservice.client;

import com.adrian.orderservice.dto.request.InventoryRequest;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", path = "/api/inventories", configuration = InventoryFeignConfig.class)
public interface InventoryClient {

    @PostMapping("/reserve")
    void reserveStock(@RequestBody InventoryRequest request);
}

@Configuration
@RequiredArgsConstructor
class InventoryFeignConfig {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Bean
    public RequestInterceptor inventoryInterceptor() {
        return template -> {
            String clientRegistrationId = "order-inventory-client";

            Authentication principal = SecurityContextHolder.getContext().getAuthentication();

            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId(clientRegistrationId)
                    .principal(principal)
                    .build();

            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient != null) {
                OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue());
            }
        };
    }
}
