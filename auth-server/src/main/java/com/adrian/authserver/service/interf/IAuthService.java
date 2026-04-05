package com.adrian.authserver.service.interf;

import com.adrian.authserver.dto.request.RegisterRequest;
import com.adrian.authserver.dto.response.AuthResponse;

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

}
