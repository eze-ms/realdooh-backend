# Backend Spring Boot — Prueba Técnica de Autenticación y SSO

![Status](https://img.shields.io/badge/status-live-success?style=flat-square)
![Backend](https://img.shields.io/badge/backend-Java%2017-orange?style=flat-square)
![Framework](https://img.shields.io/badge/framework-Spring%20WebFlux-6db33f?style=flat-square)
![Tests](https://img.shields.io/badge/tests-JUnit%205-blue?style=flat-square)

---

# 📄 Descripción
Este backend desarrollado en Java 17 con Spring Boot implementa una API de autenticación segura basada en JWT. Ofrece login tradicional con email y contraseña, así como una simulación de flujo SSO (Single Sign-On) con redirección y validación de código. Está diseñado con arquitectura modular, validaciones robustas y pruebas unitarias e integradas.
---

## ✨ Funcionalidades
- Autenticación tradicional mediante endpoint /api/auth/login
- Generación de token JWT como respuesta en texto plano
- Simulación de proveedor SSO:
- Redirección con código simulado
- Callback que devuelve un token si el código es válido
- Validación de campos con Bean Validation (@Valid)
- Control de errores y excepciones personalizadas
- Documentación automática con Swagger / OpenAPI
- Configuración de CORS para permitir frontend en localhost:4200

---

## 🧱 Estructura del Proyecto
```bash
src/main/java/com/econocom/auth_backend/
├── config/              # Seguridad JWT, CORS y Swagger
├── controller/          # AuthController con login y SSO
├── exception/           # Excepciones personalizadas y global handler
├── model/               # LoginRequest, LoginResponse
├── security/            # Filtro JwtAuthenticationFilter y JwtUtil
├── service/             # AuthService y AuthServiceImpl
└── AuthBackendApplication.java
```
---

## 💻 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot**
- **JWT** para autenticación y autorización
- **Swagger/OpenAPI** para documentación
- **JUnit 5 + Mockito** para testing unitario

---

## 📋 Requisitos

- **Java 17** o superior
- **IDE compatible con Spring (recomendado: IntelliJ IDEA)**
- **Maven 3+**

---

## 🧪 Testing
El proyecto incluye cobertura de test unitario y de integración:

### Test Unitarios

- Login con credenciales válidas e inválidas.
- Validación de campos con Bean Validation.
- Lógica del filtro JWT con múltiples escenarios.
- Flujo SSO completo (redirección y callback).

### Test de integración
- Login real a través del endpoint /api/auth/login.

Frameworks utilizados:

- JUnit 5
- Mockito
---

## 🛠️ Instalación
```bash
git clone https://github.com/eze-ms/realdooh-backend
```


#### Iniciar Spring Boot

```bash
./mvn spring-boot:run

```
#### Iniciar Spring Boot
Swagger disponible en:
```bash
http://localhost:8080/swagger-ui.html

```

---

© 2025. Proyecto desarrollado por Ezequiel Macchi Seoane