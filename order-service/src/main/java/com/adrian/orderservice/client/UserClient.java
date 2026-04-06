package com.adrian.orderservice.client;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/users", configuration = UserFeignConfig.class)
public interface UserClient {

    @GetMapping("/{id}")
    void getByUserId(@PathVariable String id);
}

@Configuration
class UserFeignConfig {
    @Bean
    public RequestInterceptor userRequestInterceptor() {
        return template -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                String rawToken = jwtAuthenticationToken.getToken().getTokenValue();
                String cleanToken = rawToken.replaceAll("\\s+", "");
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + cleanToken);
            }

        };
    }
}
