package com.econocom.auth_backend.controller;

import com.econocom.auth_backend.dto.LoginRequest;
import com.econocom.auth_backend.exception.InvalidCredentialsException;
import com.econocom.auth_backend.service.AuthService;
import com.econocom.auth_backend.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Inicio de sesión con email y contraseña",
            description = "Valida las credenciales del usuario y devuelve un token JWT si son correctas.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario para autenticación",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "email": "test@demo.com",
                  "password": "123456"
                }
                """
                            )
                    )
            )
    )

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401",
                    description = "Credenciales inválidas"
            )
    })

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request).getToken();
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Iniciar flujo de autenticación SSO simulado",
            description = "Redirige al callback con un código simulado"
    )
    @ApiResponse(
            responseCode = "302",
            description = "Redirección al endpoint de callback"
    )

    @GetMapping("/sso")
    public ResponseEntity<Void> iniciarSso() {
        URI redirectUri = URI.create("http://localhost:4200/sso/callback?code=simulated-code");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }

    @Operation(
            summary = "Callback del proveedor SSO simulado",
            description = "Recibe el código simulado y devuelve un token si la validación es correcta."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SSO exitoso, token generado"),
            @ApiResponse(responseCode = "401", description = "Código SSO inválido")
    })

    @GetMapping("/sso/callback")
    public ResponseEntity<String> callbackSso(
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Código de autorización simulado",
                    required = true,
                    example = "simulated-code"
            )
            @RequestParam String code) {

        if ("simulated-code".equals(code)) {
            String token = jwtUtil.generateToken("sso@demo.com");
            return ResponseEntity.ok(token);
        } else {
            throw new InvalidCredentialsException("Código SSO inválido");
        }
    }
}
