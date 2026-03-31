package com.adrian.userservice.service.impl;

import com.adrian.userservice.dto.request.UserRequest;
import com.adrian.userservice.dto.response.UserResponse;
import com.adrian.userservice.entity.User;
import com.adrian.userservice.exception.UserNotFoundException;
import com.adrian.userservice.mapper.UserMapper;
import com.adrian.userservice.repository.UserRepository;
import com.adrian.userservice.service.interf.IUserService;
import com.adrian.userservice.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final UserRepository repository;

    @Override
    @Transactional
    public UserResponse register(UserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Default role is USER
                .createdAt(LocalDateTime.now())
                .build();

        return mapper.toResponse(repository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(String id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }

    @Override
    @Transactional
    public UserResponse update(String id, UserRequest request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapper.toResponse(repository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}
