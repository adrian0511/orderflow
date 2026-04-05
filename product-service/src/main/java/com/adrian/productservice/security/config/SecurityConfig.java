package com.adrian.productservice.security.config;

import com.adrian.productservice.security.exception.CustomAccessDeniedHandler;
import com.adrian.productservice.security.exception.CustomAuthenticationEntryPoint;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomAccessDeniedHandler accessDeniedHandler,
                                                   CustomAuthenticationEntryPoint authenticationEntryPoint) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                // ADMIN
                                .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/prooducts/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/products/{id}").hasRole("ADMIN")

                                // PUBLIC
                                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                                .anyRequest().authenticated())

                // EXCEPTIONS
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .accessDeniedHandler(accessDeniedHandler)
                                .authenticationEntryPoint(authenticationEntryPoint)
                )
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
                        httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(customJwtAuthenticationConverter()))
                );

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
                List<String> scopes = source.getClaimAsStringList("scope");

                scopes.forEach(s -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + s)));
            }

            return authorities;
        });

        return converter;
    }

}
