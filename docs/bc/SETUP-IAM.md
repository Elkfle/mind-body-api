
## User Stories cubiertas

| US | Descripción |
|----|-------------|
| US01 | Registrar usuario (estudiante o admin) |
| US02 | Iniciar sesión y obtener JWT |
| US03 | Renovar access token con refresh token |
| US04 | Cerrar sesión |
| US05 | Consultar perfil propio |

---

## Estructura del paquete

```
iam/
├── controller/
│   └── AuthController.java          — POST /api/v1/auth/*
├── dto/
│   ├── SignUpRequest.java
│   ├── SignInRequest.java
│   ├── RefreshRequest.java
│   ├── TokenResponse.java
│   └── UserProfileResponse.java
├── exception/
│   ├── DuplicateEmailException.java  — extiende BusinessRuleException → 400
│   └── InvalidTokenException.java    — extiende BusinessRuleException → 400
├── model/
│   ├── Role.java                     — enum: STUDENT | UNIVERSITY_ADMIN
│   ├── User.java                     — @Entity, implementa UserDetails
│   └── RefreshToken.java             — @Entity, con isValid()
├── repository/
│   ├── UserRepository.java
│   └── RefreshTokenRepository.java   — revokeAllByUserId() con @Modifying
├── security/
│   ├── JwtService.java               — genera y valida HS256 tokens
│   ├── JwtAuthenticationFilter.java  — OncePerRequestFilter
│   └── UserDetailsServiceImpl.java   — carga User por email
└── service/
    ├── IAuthService.java
    └── AuthService.java              — lógica de sign-up, sign-in, refresh, sign-out
```

**Archivos shared modificados por este BC:**
- `shared/config/SecurityConfig.java` — reemplaza la versión temporal, agrega filtro JWT
- `shared/config/OpenApiConfig.java` — agrega esquema `bearerAuth` para Swagger
- `shared/exception/GlobalExceptionHandler.java` — agrega handler para `AuthenticationException` → 401

---

## Endpoints

| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| POST | `/api/v1/auth/sign-up` | No | Registrar usuario |
| POST | `/api/v1/auth/sign-in` | No | Iniciar sesión |
| POST | `/api/v1/auth/refresh` | No | Renovar tokens |
| POST | `/api/v1/auth/sign-out` | No | Revocar refresh token |
| GET | `/api/v1/auth/me` | Bearer | Perfil propio |

---

## Flujo JWT

```
sign-up / sign-in
     │
     ▼
AccessToken (HS256, 15 min)  ←── usar en Authorization: Bearer <token>
RefreshToken (UUID, 7 días)  ←── usar en POST /refresh para rotar

Al rotar:  refresh token viejo → revokedAt = NOW
           refresh token nuevo → guardado en BD
```

---

## Cómo levantar y probar

### Prerrequisitos
1. Docker Desktop corriendo
2. Proyecto clonado

### Levantar la API
```bash
cd mindbody
./mvnw spring-boot:run
```
Docker Compose inicia PostgreSQL automáticamente. Las tablas `users` y `refresh_tokens` se crean con `ddl-auto: update`.

### Probar con Postman

1. Abrir Postman
2. **Import** → seleccionar `docs/postman/mindbody-iam.postman_collection.json`
3. Ir a **Collections** → `Mind&Body API — IAM BC`
4. Hacer clic en **Run collection** (Collection Runner)
5. Ejecutar en orden (TC-IAM-01 a TC-IAM-11)

**Resultado esperado:** 11/11 tests pasando ✅

### Probar con Swagger UI

1. Abrir `http://localhost:8080/swagger-ui.html`
2. Ir a la sección **IAM**
3. Ejecutar `POST /api/v1/auth/sign-up` → copiar `accessToken`
4. Hacer clic en **Authorize** (candado arriba a la derecha)
5. Pegar el token → **Authorize**
6. Ejecutar `GET /api/v1/auth/me` → debe retornar tu perfil

---

## Variables de entorno (`.env`)

```env
SPRING_PROFILES_ACTIVE=local
DB_URL=jdbc:postgresql://localhost:55432/mindbody_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=mindbody-local-secret-key-must-be-at-least-256-bits-long
JWT_EXPIRATION_MS=900000
JWT_REFRESH_EXPIRATION_DAYS=7
```

