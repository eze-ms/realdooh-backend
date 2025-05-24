package com.econocom.auth_backend.controller;

import com.econocom.auth_backend.dto.LoginRequest;
import com.econocom.auth_backend.dto.LoginResponse;
import com.econocom.auth_backend.exception.InvalidCredentialsException;
import com.econocom.auth_backend.service.AuthService;
import com.econocom.auth_backend.util.JwtUtil;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import java.net.URI;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private Validator validator;

    @BeforeEach
    void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void login_CredencialesValidas_RetornaTokenEnOk() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        String fakeToken = "jwt.token.ejemplo";

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse(fakeToken));

        ResponseEntity<String> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(fakeToken, response.getBody());
    }

    @Test
    void login_CredencialesInvalidas_LanzaUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrongpassword");

        when(authService.login(request))
                .thenThrow(new InvalidCredentialsException("Credenciales inválidas"));

        InvalidCredentialsException thrown = assertThrows(
                InvalidCredentialsException.class,
                () -> authController.login(request)
        );

        assertEquals("Credenciales inválidas", thrown.getMessage());
    }

    @Test
    void login_CamposInvalidos_ValidaConBeanValidation() {
        LoginRequest request = new LoginRequest();
        request.setEmail("");

        request.setPassword("");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    @Test
    void login_ErrorInternoEnServicio_LanzaExcepcion() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Fallo interno"));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> authController.login(request)
        );

        assertEquals("Fallo interno", thrown.getMessage());
    }

    @Test
    void iniciarSso_RetornaRedirectConLocationEsperado() {
        ResponseEntity<Void> response = authController.iniciarSso();
        URI location = response.getHeaders().getLocation();

        assertEquals("http://localhost:4200/sso/callback?code=simulated-code", location.toString());
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }


    @Test
    void callbackSso_CodigoValido_RetornaToken() {
        String tokenEsperado = "jwt.token.simulado";

        when(jwtUtil.generateToken("sso@demo.com"))
                .thenReturn(tokenEsperado);

        ResponseEntity<String> response = authController.callbackSso("simulated-code");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tokenEsperado, response.getBody());
    }

    @Test
    void callbackSso_CodigoInvalido_LanzaExcepcion() {
        String codigoInvalido = "otro-codigo";

        InvalidCredentialsException ex = assertThrows(
                InvalidCredentialsException.class,
                () -> authController.callbackSso(codigoInvalido)
        );

        assertEquals("Código SSO inválido", ex.getMessage());
    }
}
