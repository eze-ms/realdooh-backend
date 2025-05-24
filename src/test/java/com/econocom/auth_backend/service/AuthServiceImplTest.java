package com.econocom.auth_backend.service;

import com.econocom.auth_backend.dto.LoginRequest;
import com.econocom.auth_backend.dto.LoginResponse;
import com.econocom.auth_backend.exception.InvalidCredentialsException;
import com.econocom.auth_backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_credencialesValidas_devuelveLoginResponseConToken() {
        String email = "test@demo.com";
        String password = "123456";
        String tokenEsperado = "tokenGenerado";

        when(jwtUtil.generateToken(email)).thenReturn(tokenEsperado);

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(tokenEsperado, response.getToken());
        verify(jwtUtil).generateToken(email);
    }

    @Test
    void login_emailIncorrecto_lanzaInvalidCredentialsException() {
        String email = "otro@correo.com";
        String password = "123456";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_contrasenaIncorrecta_lanzaInvalidCredentialsException() {
        String email = "test@demo.com";
        String password = "wrongpass";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_emailYContrasenaIncorrectos_lanzaInvalidCredentialsException() {
        String email = "otro@correo.com";
        String password = "wrongpass";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }


}

