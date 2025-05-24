package com.econocom.auth_backend.service;

import com.econocom.auth_backend.dto.LoginRequest;
import com.econocom.auth_backend.dto.LoginResponse;
import com.econocom.auth_backend.exception.InvalidCredentialsException;
import com.econocom.auth_backend.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final JwtUtil jwtUtil;

    public AuthServiceImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        validarCredenciales(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(request.getEmail());
        return new LoginResponse(token);
    }

    private void validarCredenciales(String email, String password) {
        if (!"test@demo.com".equals(email) || !"123456".equals(password)) {
            throw new InvalidCredentialsException("Email o contraseña incorrectos");
        }
    }

}
