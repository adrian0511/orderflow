package com.adrian.userservice.mapper;

import com.adrian.userservice.dto.response.UserResponse;
import com.adrian.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
