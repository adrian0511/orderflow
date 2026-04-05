package com.adrian.userservice.consumer;

import com.adrian.userservice.dto.event.UserCreatedEvent;
import com.adrian.userservice.dto.request.UserRequest;
import com.adrian.userservice.service.interf.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCreatedListener {

    @Autowired
    private IUserService service;

    @KafkaListener(topics = "user.created", groupId = "user-group")
    public void createUser(UserCreatedEvent event) {
        service.register(new UserRequest(
                event.getId(),
                event.getUsername(),
                event.getEmail()
        ));

        log.info("User created with id {}", event.getId());
    }
}
