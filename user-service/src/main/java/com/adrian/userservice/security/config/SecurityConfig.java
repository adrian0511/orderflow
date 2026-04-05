package com.adrian.userservice.security.config;

import com.adrian.userservice.security.exception.CustomAccessDeniedHandler;
import com.adrian.userservice.security.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
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
                                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                                // USER
                                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAuthority("SCOPE_profile")
                                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAuthority("SCOPE_profile")
                                .requestMatchers(HttpMethod.GET, "/api/users/by-username").hasAuthority("SCOPE_profile")
                                
                                .anyRequest().authenticated())
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> {
                            httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer -> jwtConfigurer
                                    .jwtAuthenticationConverter(customJwtAuthenticationConverter()));
                        }
                )
                // EXCEPTIONS
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                );


        return http.build();
    }

    private JwtAuthenticationConverter customJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            if (jwt.getClaim("scope") != null) {
                List<String> scopes = jwt.getClaimAsStringList("scope");
                scopes.forEach(scope -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope)));
            }

            if (jwt.getClaim("roles") != null) {
                List<String> roles = jwt.getClaimAsStringList("roles");
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
            }

            return authorities;

        });

        return converter;
    }
}
