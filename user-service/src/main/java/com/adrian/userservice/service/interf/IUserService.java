package com.adrian.userservice.service.interf;

import com.adrian.userservice.dto.request.UserRequest;
import com.adrian.userservice.dto.response.UserResponse;

import java.util.List;

public interface IUserService {

    UserResponse register(UserRequest request);

    UserResponse getById(String id);

    UserResponse getByUsername(String username);

    UserResponse update(String id, UserRequest request);

    List<UserResponse> getAll();

}
