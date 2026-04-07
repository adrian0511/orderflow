package com.adrian.inventoryservice.security.config;

import com.adrian.inventoryservice.security.exception.CustomAccessDeniedHandler;
import com.adrian.inventoryservice.security.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final static String[] FREE_RESOURCES_URLS = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-resources/**", "/api-docs/**", "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomAccessDeniedHandler accessDeniedHandler,
                                                   CustomAuthenticationEntryPoint authenticationEntryPoint) {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry

                                // INTERNAL
                                .requestMatchers(HttpMethod.POST, "/api/inventories/reserve").hasAllAuthorities("ROLE_SERVICE", "SCOPE_inventory.write")

                                // ADMIN
                                .requestMatchers(HttpMethod.POST, "/api/inventories").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/inventories/restock").hasRole("ADMIN")

                                // PUBLIC
                                .requestMatchers(FREE_RESOURCES_URLS).permitAll()

                                .anyRequest().authenticated()
                )

                // EXCEPTIONS
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                )

                // OAuth2
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
                        httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(customJwtAuthenticationConverter()))
                )
        ;


        return http.build();
    }

    private JwtAuthenticationConverter customJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(source -> {

            Collection<GrantedAuthority> authorities = new ArrayList<>();

            if (source.getClaim("roles") != null) {
                List<String> roles = source.getClaimAsStringList("roles");

                roles.forEach(s -> authorities.add(new SimpleGrantedAuthority(s)));
            }

            if (source.getClaim("scope") != null) {
                List<String> roles = source.getClaimAsStringList("scope");

                roles.forEach(s -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + s)));
            }

            return authorities;
        });

        return converter;
    }
}
