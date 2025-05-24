package com.econocom.auth_backend.service;

import com.econocom.auth_backend.dto.LoginRequest;
import com.econocom.auth_backend.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
