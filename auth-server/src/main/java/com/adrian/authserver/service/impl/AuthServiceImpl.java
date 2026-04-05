package com.adrian.authserver.service.impl;

import com.adrian.authserver.dto.event.UserCreatedEvent;
import com.adrian.authserver.dto.request.RegisterRequest;
import com.adrian.authserver.dto.response.AuthResponse;
import com.adrian.authserver.entity.AuthUser;
import com.adrian.authserver.repository.AuthUserRepository;
import com.adrian.authserver.service.interf.IAuthService;
import com.adrian.authserver.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthUserRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PasswordEncoder encoder;

    @Override
    public AuthResponse register(RegisterRequest request) {
        AuthUser user = AuthUser.builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .build();

        user = repository.save(user);

        kafkaTemplate.send("user.created", new UserCreatedEvent(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        ));

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
