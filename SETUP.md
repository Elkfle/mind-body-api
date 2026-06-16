# Mind&Body API — Guía de Configuración del Proyecto

**Proyecto:** Mind&Body Web Services API  
**Curso:** Ingeniería de Software — UPC | Grupo 1 | 1ACC0236-202610  
**Stack:** Spring Boot 4.0.6 · Java 21 · PostgreSQL 16 · Docker  

---

## Índice

- [1. Requisitos previos](#1-requisitos-previos)
- [2. Clonar el repositorio](#2-clonar-el-repositorio)
- [3. Estructura del proyecto](#3-estructura-del-proyecto)
- [4. Configuración de perfiles](#4-configuración-de-perfiles)
- [5. Levantar la base de datos](#5-levantar-la-base-de-datos)
- [6. Correr la aplicación](#6-correr-la-aplicación)
- [7. Verificar que todo funciona](#7-verificar-que-todo-funciona)
- [8. Flujo GitFlow del equipo](#8-flujo-gitflow-del-equipo)
- [9. Dependencias del proyecto](#9-dependencias-del-proyecto)
- [10. Bounded Context: Activities](#10-bounded-context-activities)
- [11. Bounded Context: Shared](#11-bounded-context-shared)
- [12. Colección Postman](#12-colección-postman)
- [13. Bounded Context: IAM](#13-bounded-context-iam)
- [14. Bounded Context: Reservations](#14-bounded-context-reservations)
- [15. Bounded Context: Attendance](#15-bounded-context-attendance)
- [16. Bounded Context: Institutions](#16-bounded-context-institutions)

---

## 1. Requisitos previos

Antes de clonar el proyecto, asegurate de tener instalado:

| Herramienta | Versión mínima | Para qué sirve |
|---|---|---|
| Java (JDK) | 21 | Compilar y correr Spring Boot |
| Maven | 3.9+ | Gestionar dependencias |
| Docker Desktop | Cualquier versión reciente | Levantar PostgreSQL y pgAdmin |
| IntelliJ IDEA | Cualquier versión | IDE recomendado |
| Postman | Cualquier versión | Probar los endpoints |

---

## 2. Clonar el repositorio

```bash
git clone https://github.com/<org>/mind-body-api.git
cd mind-body-api/mindbody
```

Crear la rama de trabajo desde `develop`:

```bash
git checkout develop
git checkout -b feature/<tu-bounded-context>-bc
```

Ejemplo para Activities: `feature/activities-bc`

---

## 3. Estructura del proyecto

```
mind-body-api/
└── mindbody/
    ├── Dockerfile                        ← Build multi-stage para deploy en Render
    ├── .dockerignore                     ← Excluye .env, target/, .git/
    ├── compose.yaml                      ← Docker Compose (PostgreSQL + pgAdmin)
    ├── .env.example                      ← Variables de entorno de ejemplo
    ├── pom.xml                           ← Dependencias Maven
    └── src/
        └── main/
            ├── java/com/grupo1/mindbody/
            │   ├── iam/                  ← Bounded Context: IAM (Bussalleu)
            │   ├── activities/           ← Bounded Context: Activities (Colfer)
            │   ├── reservations/         ← Bounded Context: Reservations (Tejada)
            │   ├── attendance/           ← Bounded Context: Attendance (Ruiz)
            │   ├── institutions/         ← Bounded Context: Institutions (transversal)
            │   └── shared/              ← Componentes transversales
            └── resources/
                ├── application.yml           ← Config base (selecciona perfil activo)
                ├── application-local.yml     ← Config local con Docker
                └── application-prod.yml      ← Config producción (Render)
```

Cada Bounded Context sigue la estructura **Package by Feature**:

```
<bounded-context>/
├── controller/
├── service/
├── repository/
├── model/
├── dto/
├── mapper/
└── exception/
```

---

## 4. Configuración de perfiles

El proyecto usa **Spring Profiles** para separar la configuración local de producción.

### 4.1 Cómo funciona

`application.yml` define qué perfil está activo:

```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:local}
```

Si no se define la variable de entorno `SPRING_PROFILES_ACTIVE`, usa `local` por defecto — ideal para desarrollo.

### 4.2 Perfil `local` (development)

`application-local.yml` apunta a Docker con valores por defecto (no necesita `.env`):

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:55432/mindbody_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
```

El formato `${VAR:valor_default}` significa: "leer la variable de entorno, y si no existe usar el valor por defecto".

### 4.3 Perfil `prod` (producción en Render)

`application-prod.yml` no tiene defaults — todas las variables deben estar en el entorno:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

En Render, estas variables se configuran en el dashboard del servicio.

### 4.4 Archivo `.env` (opcional para local)

Para desarrollo local no es obligatorio. Los valores por defecto del perfil `local` ya funcionan.
Si querés personalizar algo:

```bash
# Copiar el ejemplo
cp .env.example .env
# Editar .env con tus valores
```

> ⚠️ El archivo `.env` está en `.gitignore` — nunca se sube al repositorio.

---

## 5. Levantar la base de datos

El proyecto incluye `compose.yaml` con PostgreSQL 16 y pgAdmin.

### Opción A: Spring Boot Docker Compose (automático)

El proyecto tiene la dependencia `spring-boot-docker-compose`. Al correr la app en modo `local`, Spring Boot **levanta Docker Compose automáticamente**. No necesitás hacer nada extra.

### Opción B: Manual (si preferís más control)

```bash
# Desde la carpeta mindbody/
docker compose up -d

# Ver que los contenedores estén corriendo
docker ps
```

### Acceder a pgAdmin

- URL: `http://localhost:8082`
- Email: `admin@mindbody.com`
- Password: `admin`

Para conectar pgAdmin a la BD:
- Host: `postgres` (nombre del servicio en compose)
- Puerto: `5432`
- Base de datos: `mindbody_db`
- Usuario: `postgres`
- Contraseña: `postgres`

---

## 6. Correr la aplicación

### Desde IntelliJ IDEA

1. Abrir el proyecto (`File > Open` → seleccionar la carpeta `mindbody/`)
2. Esperar que Maven descargue las dependencias
3. Correr `MindbodyApplication.java` (botón verde ▶)

### Desde terminal

```bash
cd mindbody/
./mvnw spring-boot:run
```

La aplicación levanta en `http://localhost:8080`.

---

## 7. Verificar que todo funciona

### Swagger UI

Abrir en el browser: `http://localhost:8080/swagger-ui.html`

Deberías ver la documentación interactiva de la API.

### Postman

Importar la colección del equipo desde `/docs/postman/` (cuando esté disponible).

---

## 8. Flujo GitFlow del equipo

```
main
└── develop
    ├── feature/iam-bc              ← Bussalleu Salcedo
    ├── feature/activities-bc       ← Colfer Mendoza
    ├── feature/reservations-bc     ← Tejada Ramirez
    └── feature/attendance-bc       ← Ruiz Ramirez
```

**Reglas:**
- Nunca commitear directo a `main` o `develop`
- Cada feature branch hace PR a `develop` al terminar
- PR necesita al menos 1 aprobación de un compañero
- Resolver conflictos antes del merge

---

## 9. Dependencias del proyecto

| Dependencia | Versión | Para qué sirve |
|---|---|---|
| Spring Boot | 4.0.6 | Framework base |
| Spring Web | (Boot managed) | Controladores REST |
| Spring Data JPA | (Boot managed) | Persistencia con Hibernate |
| Spring Security | (Boot managed) | Autenticación y autorización |
| Spring Validation | (Boot managed) | Validación de DTOs con anotaciones |
| PostgreSQL Driver | (Boot managed) | Conector a la BD |
| JJWT | 0.12.6 | Generación y validación de JWT |
| MapStruct | 1.6.3 | Conversión Entity ↔ DTO en tiempo de compilación |
| Lombok | (Boot managed) | Reducir boilerplate (getters, builders, etc.) |
| springdoc-openapi | 2.7.0 | Generar Swagger UI automáticamente |
| spring-boot-docker-compose | (Boot managed) | Gestión automática de Docker Compose |
| spring-boot-devtools | (Boot managed) | Hot reload en desarrollo |

---

---

## 10. Bounded Context: Activities

**Responsable:** Carlos Alejandro Colfer Mendoza  
**Branch:** `feature/activities-bc`  
**User Stories:** US06, US15, US16, US17

### 10.1 Archivos implementados

```
src/main/java/com/grupo1/mindbody/activities/
├── controller/
│   ├── ActivityController.java       ← CRUD + paginación
│   └── ActivityReportController.java ← Reporte por categoría
├── service/
│   ├── IActivityService.java         ← Interfaz con todos los métodos públicos
│   └── ActivityService.java          ← Implementación con lógica de negocio
├── repository/
│   └── ActivityRepository.java       ← Spring Data JPA + query nativa para reporte
├── model/
│   ├── Activity.java                 ← Entidad JPA (tabla: activities)
│   ├── ActivityCategory.java         ← Enum: YOGA, FOOTBALL, BASKETBALL, SWIMMING, GYM, TENNIS
│   └── ActivityStatus.java           ← Enum: ACTIVE, CANCELLED, COMPLETED
├── dto/
│   ├── ActivityRequest.java          ← Record con validaciones Bean Validation
│   ├── ActivityResponse.java         ← Record de respuesta (todos los campos)
│   └── ActivitySummaryReport.java    ← Record para reporte por categoría
├── mapper/
│   └── ActivityMapper.java           ← MapStruct: Activity → ActivityResponse
└── exception/
    └── ActivityNotFoundException.java ← Extiende ResourceNotFoundException
```

### 10.2 Endpoints disponibles

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| GET | `/api/v1/activities` | Bearer | Listar actividades paginadas (`?page=0&size=10`) |
| GET | `/api/v1/activities/{id}` | Bearer | Obtener actividad por ID |
| POST | `/api/v1/activities` | Bearer (ADMIN) | Crear actividad — `adminId` se extrae del JWT |
| PUT | `/api/v1/activities/{id}` | Bearer (ADMIN) | Actualizar actividad |
| DELETE | `/api/v1/activities/{id}` | Bearer (ADMIN) | Cancelar actividad (status → CANCELLED) |
| GET | `/api/v1/activities/reports/by-category` | Bearer | Reporte agrupado por categoría |

### 10.3 Reglas de negocio implementadas

- `cancel()` cambia el status a `CANCELLED` — no elimina el registro de la BD.
- `hasAvailableSlots()` verifica `currentEnrollment < maxCapacity` — usado por BC Reservations.
- `incrementEnrollment()` y `decrementEnrollment()` son llamados por BC Reservations al crear/cancelar reservas. Están en la interfaz `IActivityService` para que Reservations pueda inyectarlos sin depender de la implementación concreta.
- `currentEnrollment` arranca en 0 con `@Builder.Default` y se incrementa/decrementa en la BD (campo desnormalizado intencional para evitar COUNT(*) en cada consulta de disponibilidad).
- `createdAt` se setea automáticamente con `@PrePersist` — no se puede modificar (`updatable = false`).

### 10.4 Body de ejemplo para crear actividad

```json
{
  "title": "Yoga Matutino",
  "description": "Clase de yoga para principiantes",
  "category": "YOGA",
  "venue": "Gimnasio A",
  "location": "Pabellón H, Piso 2",
  "maxCapacity": 20,
  "date": "2026-06-15",
  "startTime": "07:00:00",
  "endTime": "08:00:00",
  "institutionId": 1
}
```

### 10.5 Casos de prueba (Postman)

| ID | Endpoint | Resultado esperado |
|---|---|---|
| TC-ACT-01 | POST `/api/v1/activities?adminId=1` | 201 Created — objeto completo con `status: ACTIVE`, `currentEnrollment: 0` |
| TC-ACT-02 | GET `/api/v1/activities?page=0&size=5` | 200 OK — `PageResponse` con actividad creada |
| TC-ACT-03 | GET `/api/v1/activities/1` | 200 OK — datos de la actividad |
| TC-ACT-04 | PUT `/api/v1/activities/1` | 200 OK — datos actualizados |
| TC-ACT-05 | GET `/api/v1/activities/9999` | 404 Not Found — `ErrorResponse` con mensaje |
| TC-ACT-06 | POST body con campos inválidos | 400 Bad Request — `details[]` con cada campo inválido |
| TC-ACT-07 | GET `/api/v1/activities/reports/by-category` | 200 OK — lista con totales por categoría |
| TC-ACT-08 | DELETE `/api/v1/activities/1` | 204 No Content — GET posterior muestra `status: CANCELLED` |

---

## 11. Bounded Context: Shared

Componentes transversales reutilizables por todos los bounded contexts.

### 11.1 Archivos implementados

```
src/main/java/com/grupo1/mindbody/shared/
├── config/
│   ├── OpenApiConfig.java      ← Configura Swagger UI en /swagger-ui.html
│   └── SecurityConfig.java     ← TEMPORAL: permite todos los requests (sin JWT)
├── exception/
│   ├── ResourceNotFoundException.java  ← Extiende RuntimeException → produce 404
│   ├── BusinessRuleException.java      ← Extiende RuntimeException → produce 400
│   ├── ErrorResponse.java              ← Record con: timestamp, status, error, message, path, details
│   └── GlobalExceptionHandler.java     ← @RestControllerAdvice: captura todas las excepciones
└── pagination/
    └── PageResponse<T>.java    ← Wrapper genérico para respuestas paginadas
```

### 11.2 Cómo usar desde otro BC

```java
// Lanzar 404 desde cualquier service:
throw new ResourceNotFoundException("Recurso no encontrado con id: " + id);

// Lanzar 400 por regla de negocio:
throw new BusinessRuleException("La actividad ya no tiene cupo disponible");

// Paginar en un controller:
return ResponseEntity.ok(PageResponse.from(service.findAll(pageable)));
```

### 11.3 SecurityConfig — estado actual

`SecurityConfig.java` implementa autenticación JWT stateless completa. Rutas públicas: `/api/v1/auth/sign-up`, `/api/v1/auth/sign-in`, `/api/v1/auth/refresh`, `/api/v1/auth/sign-out`, `/swagger-ui/**`, `/api-docs/**`, `/actuator/health`. Cualquier otro endpoint requiere `Authorization: Bearer <token>`. Errores de autenticación retornan 401 y errores de autorización retornan 403.

---

---

## 12. Colección Postman

**Archivo:** `docs/postman/mindbody-activities.postman_collection.json`

Colección lista para importar en Postman con 10 casos de prueba automatizados para el BC Activities. Sigue el mismo formato que la colección de referencia del profesor (`pagoya-api.postman_collection.json`).

### 12.1 Cómo importar

1. Abrir Postman e iniciar sesión (sin login se pierde la colección al cerrar)
2. **Import** → seleccionar `docs/postman/mindbody-activities.postman_collection.json`
3. La colección aparece como **Mind&Body API — Activities BC**

### 12.2 Variables de colección

| Variable | Valor inicial | Se actualiza en |
|---|---|---|
| `base_url` | `http://localhost:8080` | Manual (cambiar a URL de Render en prod) |
| `admin_id` | `1` | Manual |
| `activity_id` | _(vacío)_ | Automático en TC-ACT-01 al crear la actividad |

> `activity_id` se captura solo con un script de test en TC-ACT-01 y lo reutilizan TC-ACT-03, TC-ACT-04, TC-ACT-09 y TC-ACT-10. No hace falta copiarlo a mano.

### 12.3 Correr en orden automático

1. Click derecho en la colección → **Run collection**
2. Verificar que el orden sea TC-ACT-01 → TC-ACT-10
3. Click **Run Mind&Body API — Activities BC**
4. Cada test muestra verde (pasó) o rojo (falló) con el detalle del error

### 12.4 Casos de prueba incluidos

| ID | Método | Endpoint | Qué verifica |
|---|---|---|---|
| TC-ACT-01 | POST | `/api/v1/activities?adminId=1` | 201, status ACTIVE, currentEnrollment=0, captura activity_id |
| TC-ACT-02 | GET | `/api/v1/activities?page=0&size=5` | 200, estructura PageResponse con los 7 campos obligatorios |
| TC-ACT-03 | GET | `/api/v1/activities/{{activity_id}}` | 200, ID y título coinciden |
| TC-ACT-04 | PUT | `/api/v1/activities/{{activity_id}}` | 200, título y capacidad actualizados, status sigue ACTIVE |
| TC-ACT-05 | GET | `/api/v1/activities/99999` | 404, ErrorResponse con timestamp/status/error/message/path |
| TC-ACT-06 | POST | `/api/v1/activities` (campos inválidos) | 400, details[] lista los campos que fallaron validación |
| TC-ACT-07 | POST | `/api/v1/activities?adminId=1` | 201, segunda actividad (BASKETBALL) para poblar el reporte |
| TC-ACT-08 | GET | `/api/v1/activities/reports/by-category` | 200, array con category/totalActivities/totalEnrollment, mínimo 2 categorías |
| TC-ACT-09 | DELETE | `/api/v1/activities/{{activity_id}}` | 204 No Content |
| TC-ACT-10 | GET | `/api/v1/activities/{{activity_id}}` | 200, status es CANCELLED (el registro no se elimina) |

### 12.5 Convención para colecciones de otros BCs

Cada integrante debe agregar su colección en `docs/postman/` con el nombre `mindbody-<bc>.postman_collection.json`:

```
docs/postman/
├── mindbody-institutions.postman_collection.json ← Transversal ✅ (ejecutar primero)
├── mindbody-iam.postman_collection.json          ← Bussalleu ✅
├── mindbody-activities.postman_collection.json   ← Colfer ✅
├── mindbody-reservations.postman_collection.json ← Tejada ✅
└── mindbody-attendance.postman_collection.json   ← Ruiz ✅
```

> ⚠️ **Orden de ejecución**: ejecutar `mindbody-institutions` primero. Crea la institución con `id=1` que usan todos los demás BCs en sus sign-up y al crear actividades.

---

## Historial de cambios de configuración

| Fecha | Cambio | Motivo |
|---|---|---|
| 10/05/2026 | Inicialización del proyecto con Spring Initializr | Setup base del equipo |
| 10/05/2026 | Corrección de `pom.xml`: `spring-boot-starter-webmvc` → `spring-boot-starter-web` | El starter generado por Initializr era incorrecto |
| 10/05/2026 | Adición de dependencias: Security, Validation, JWT, MapStruct, Springdoc | Necesarias para el plan de implementación TB2 |
| 10/05/2026 | Migración de `application.properties` a perfiles `.yml` | Separar configuración local/prod como indica la guía del profesor |
| 10/05/2026 | Actualización de `compose.yaml` con valores Mind&Body y pgAdmin | Reemplazar valores genéricos de Spring Initializr |
| 10/05/2026 | Adición de `.env.example` y actualización de `.gitignore` | Documentar variables necesarias sin exponer secrets |
| 10/05/2026 | Eliminado `hibernate.dialect` explícito en ambos perfiles | Hibernate 7.x lo detecta automático; dejarlo causaba deprecation warning |
| 10/05/2026 | Agregado `spring.jpa.open-in-view: false` en ambos perfiles | La app es API REST pura, el open-in-view no aplica y causaba warning de rendimiento |
| 10/05/2026 | Implementado BC `shared/`: ResourceNotFoundException, BusinessRuleException, ErrorResponse, GlobalExceptionHandler, PageResponse, OpenApiConfig, SecurityConfig (temporal) | Base transversal que todos los BCs reutilizan |
| 10/05/2026 | Implementado BC `activities/` completo: Activity, enums, DTOs, repository, mapper, service, controllers | US06, US15, US16, US17 — CRUD + paginación + reporte por categoría |
| 10/05/2026 | Creada colección Postman `docs/postman/mindbody-activities.postman_collection.json` | 10 casos de prueba automatizados con scripts que capturan activity_id entre requests |
| 10/05/2026 | Actualizado `GlobalExceptionHandler`: agregados handlers para `HandlerMethodValidationException`, `HttpMessageNotReadableException` | Spring Boot 4.x usa `HandlerMethodValidationException` (vía `getParameterValidationResults()`) en lugar de `MethodArgumentNotValidException` para algunos casos — sin estos handlers la validación fallida devolvía 500 en vez de 400 |
| 12/05/2026 | Implementado BC `iam/` completo: User, RefreshToken, JwtService, JwtAuthenticationFilter, UserDetailsServiceImpl, AuthService, AuthController | US01-US05 — registro, login, refresh token rotation, sign-out, perfil propio |
| 12/05/2026 | Reemplazado `SecurityConfig.java` temporal por versión real con filtro JWT stateless | BC IAM completado — ya no se permite todo sin autenticar |
| 12/05/2026 | Actualizado `OpenApiConfig.java`: agregado esquema `bearerAuth` | Swagger UI ahora muestra botón Authorize para ingresar JWT |
| 12/05/2026 | Actualizado `GlobalExceptionHandler`: agregado handler para `AuthenticationException` → 401 | Sign-in con credenciales incorrectas devolvía 500 en lugar de 401 |
| 12/05/2026 | Creada colección Postman `docs/postman/mindbody-iam.postman_collection.json` | 11 casos de prueba: sign-up, sign-in, /me, refresh rotation, sign-out, token revocado |
| 12/05/2026 | Implementado BC `reservations/` completo: Reservation, ReservationStatus, DTOs, repository, service, controller | US08-US11 — crear/cancelar/listar reservas con validación de cupo y qrCode para Attendance |
| 12/05/2026 | Creada colección Postman `docs/postman/mindbody-reservations.postman_collection.json` | 9 casos de prueba: crear, duplicado, not found, listar por usuario, listar por actividad, cancelar, verificar CANCELLED |
| 13/05/2026 | Corregido `ActivityController`: `@RequestParam Long adminId` → `@AuthenticationPrincipal User currentUser` | Pasar la identidad del usuario como query param (`?adminId=`) no sigue REST ni las reglas del profesor — la identidad debe venir del JWT via Spring Security |
| 13/05/2026 | Corregido `ReservationController`: `@RequestParam Long userId` → `@AuthenticationPrincipal User currentUser` | Mismo motivo que Activities — `?userId=` en la URL expone datos de identidad que ya viajan en el Bearer token |
| 13/05/2026 | Actualizadas colecciones Postman `mindbody-activities` y `mindbody-reservations` | Eliminados `?adminId=` y `?userId=` de todas las URLs; agregados pasos de sign-up/sign-in al inicio para capturar el token automáticamente |
| 13/05/2026 | Implementado BC `attendance/` completo: AttendanceRecord, excepciones, repository, service, controller | US12-US14, US18 — scan QR, listado por actividad, historial propio, resumen con tasa de asistencia |
| 13/05/2026 | Creada colección Postman `docs/postman/mindbody-attendance.postman_collection.json` | 12 casos de prueba con flujo completo: setup admin+estudiante, crear actividad, crear reserva, scan QR, duplicado, inválido, summary |
| 14/05/2026 | Agregado `@SecurityRequirement(name = "bearerAuth")` a `ActivityReportController` | El endpoint de reportes estaba excluido del esquema de seguridad en Swagger |
| 14/05/2026 | Agregado handler `InvalidTokenException` → 401 en `GlobalExceptionHandler` | El refresh token revocado retornaba 400 — es un error de autenticación, no de datos |
| 14/05/2026 | Agregado handler `AccessDeniedException` → 403 en `GlobalExceptionHandler` + `accessDeniedHandler` en `SecurityConfig` | Respuesta uniforme `ErrorResponse` para accesos prohibidos |
| 14/05/2026 | Corregida documentación Swagger del endpoint `/refresh`: `400` → `401` en `@ApiResponse` | La anotación no coincidía con el comportamiento real tras el fix de `InvalidTokenException` |
| 14/05/2026 | Agregada validación `@AssertTrue isEndTimeAfterStartTime()` en `ActivityRequest` | Permitía crear actividades con `endTime` anterior a `startTime` |
| 14/05/2026 | Creados `Dockerfile` (multi-stage Maven + JRE Alpine) y `.dockerignore` | Necesarios para deploy en Render.com como indica la guía del profesor |
| 14/05/2026 | Creado `ReservationQrResult.java` — `IReservationService.findByQrCode()` retorna `Optional<ReservationQrResult>` en lugar de `Optional<Reservation>` | La entidad `Reservation` cruzaba el boundary hacia el BC Attendance; el DTO encapsula solo los campos necesarios |
| 14/05/2026 | Corregido TC-IAM-11 en colección Postman IAM: expected status `400` → `401` | Refleja el comportamiento correcto tras el fix de `InvalidTokenException` |
| 14/05/2026 | Corregido TC-ACT-06 en colección Postman Activities: body `{}` → body con campos inválidos | Un body `{}` causaba `HttpMessageNotReadableException` (sin `details`) en lugar de `MethodArgumentNotValidException` (con `details[]`) por el constructor canónico del record |
| 15/06/2026 | **Corrección profesor #1:** Creado Bounded Context `institutions/` completo (Institution, InstitutionRepository, InstitutionRequest/Response, InstitutionMapper, InstitutionService, IInstitutionService, InstitutionController, InstitutionNotFoundException) | `institution_id` existía como `Long` suelto en User y Activity sin entidad asociada ni FK real en la BD; el profesor exigió una entidad Institution con su propio BC |
| 15/06/2026 | **Corrección profesor #1 (cont.):** `User.institutionId: Long` → `User.institution: @ManyToOne(LAZY) Institution` con `@JoinColumn(name="institution_id")` | Relacionar User con Institution mediante FK real |
| 15/06/2026 | **Corrección profesor #1 (cont.):** `Activity.institutionId: Long` → `Activity.institution: @ManyToOne(LAZY, optional=false) Institution` con `@JoinColumn(name="institution_id", nullable=false)` | Idem para Activity — la FK es obligatoria (toda actividad pertenece a una institución) |
| 15/06/2026 | **Corrección profesor #1 (cont.):** Actualizado `ActivityMapper`: agregados `@Mapping(target="institutionId", source="institution.id")` y `@Mapping(target="institutionName", source="institution.name")` | MapStruct no podía inferir la ruta `institution.id` → `institutionId` automáticamente tras el cambio de campo |
| 15/06/2026 | **Corrección profesor #1 (cont.):** Actualizado `ActivityResponse`: agregado campo `String institutionName` | Permite al cliente saber el nombre de la institución sin un GET adicional |
| 15/06/2026 | **Corrección profesor #1 (cont.):** Actualizado `ActivityService.create()`: carga `Institution` desde `InstitutionRepository` antes de construir la `Activity` | Ya no se guarda solo el Long, sino la entidad completa con FK |
| 15/06/2026 | **Corrección profesor #1 (cont.):** Actualizado `AuthService.signUp()`: carga `Institution` desde `InstitutionRepository` si se recibe `institutionId` (campo opcional en sign-up) | Mismo patrón que ActivityService; si no se provee institutionId el campo queda null (admin bootstrap) |
| 15/06/2026 | **Corrección profesor #2:** `User.name: String` → `User.firstName: String` + `User.lastName: String` con columnas `first_name` y `last_name` | El profesor indicó que un campo `name` único causa redundancia con futuras entidades de perfil; debe separarse en firstName/lastName |
| 15/06/2026 | **Corrección profesor #2 (cont.):** Actualizado `SignUpRequest`: `String name` → `@NotBlank String firstName` + `@NotBlank String lastName` | DTO de entrada adaptado a los nuevos campos |
| 15/06/2026 | **Corrección profesor #2 (cont.):** Actualizado `UserProfileResponse`: `String name` → `String firstName` + `String lastName` + `String institutionName`; método factory `from(User)` actualizado | Respuesta de `/me` ahora refleja nombres separados e incluye el nombre de la institución en lugar del ID |
| 15/06/2026 | Creada colección Postman `docs/postman/mindbody-institutions.postman_collection.json` | 6 casos de prueba: TC-INS-00 sign-up admin sin institución (bootstrap), TC-INS-01 crear institución, TC-INS-02 nombre duplicado→400, TC-INS-03 listar, TC-INS-04 obtener por ID, TC-INS-05 ID inexistente→404 |
| 15/06/2026 | Actualizadas colecciones Postman: `mindbody-iam`, `mindbody-activities`, `mindbody-reservations`, `mindbody-attendance` | Todos los bodies de sign-up reemplazaron `"name": "..."` por `"firstName": "..."` + `"lastName": "..."` para alinearse con el nuevo campo en `SignUpRequest` |
| 16/06/2026 | Corregido `TokenResponse.java`: campo `String name` → `String firstName` + `String lastName`; factory `of()` usa `user.getFirstName()` y `user.getLastName()` | `TokenResponse` aún llamaba `user.getName()` que ya no existe tras la corrección del profesor — error de compilación detectado al correr la app |
| 16/06/2026 | Corregido `GET /auth/me` → 500: `AuthService.getProfile()` — agregado `@Transactional(readOnly=true)` y recarga el usuario con `userRepository.findById()` | `getProfile()` recibía el `User` del filtro JWT (sesión Hibernate cerrada); al acceder a `user.getInstitution().getName()` lanzaba `LazyInitializationException` → 500. La transacción activa permite que Hibernate resuelva la relación LAZY dentro de la misma sesión |
| 16/06/2026 | Corregidos TC-ACT-01 y TC-ACT-04 en colección Postman Activities: `"date": "2026-06-15"` → `"2026-12-01"`; TC-ACT-07: `"2026-06-20"` → `"2026-12-05"` | Las fechas eran pasadas al momento de correr los tests (hoy 16/06/2026), `@Future` las rechazaba con 400; TC-ACT-01 nunca insertaba Yoga → `activity_id` quedaba vacío → TC-ACT-09/10 iban a `/api/v1/activities/` (sin ID) → 500 |
| 16/06/2026 | Actualizado `GlobalExceptionHandler`: agregado handler para `NoResourceFoundException` → 404 | Spring lanza `NoResourceFoundException` cuando la URL tiene un segmento de path vacío (ej. `/api/v1/activities/` sin ID) y no coincide con ningún endpoint ni recurso estático; el catch-all `Exception.class` lo devolvía como 500 |

---

## 13. Bounded Context: IAM

**Responsable:** Fabrizio Bussalleu | **Guía completa:** `docs/bc/SETUP-IAM.md`

### 13.1 Archivos implementados

```
src/main/java/com/grupo1/mindbody/iam/
├── controller/AuthController.java
├── dto/SignUpRequest.java, SignInRequest.java, RefreshRequest.java,
│       TokenResponse.java, UserProfileResponse.java
├── exception/DuplicateEmailException.java, InvalidTokenException.java
├── model/Role.java (enum), User.java (UserDetails), RefreshToken.java
├── repository/UserRepository.java, RefreshTokenRepository.java
├── security/JwtService.java, JwtAuthenticationFilter.java,
│             UserDetailsServiceImpl.java
└── service/IAuthService.java, AuthService.java
```

### 13.2 Endpoints

| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| POST | `/api/v1/auth/sign-up` | No | Registrar usuario (STUDENT / UNIVERSITY_ADMIN) |
| POST | `/api/v1/auth/sign-in` | No | Iniciar sesión → access + refresh token |
| POST | `/api/v1/auth/refresh` | No | Rotar refresh token → nuevos tokens |
| POST | `/api/v1/auth/sign-out` | No | Revocar refresh token |
| GET | `/api/v1/auth/me` | Bearer | Perfil del usuario autenticado |

### 13.3 Flujo de tokens

- **Access token:** HS256, 15 min, claims `userId` + `role`
- **Refresh token:** UUID v4, 7 días, guardado en tabla `refresh_tokens`
- **Rotación:** cada `/refresh` revoca el token anterior y genera uno nuevo
- **Sign-out:** revoca el refresh token (el access token expira solo en 15 min)

### 13.4 Casos de prueba (Postman)

| ID | Endpoint | Resultado esperado |
|----|----------|-------------------|
| TC-IAM-01 | POST `/sign-up` (STUDENT) | 201, tokens generados, role=STUDENT |
| TC-IAM-02 | POST `/sign-up` (ADMIN) | 201, role=UNIVERSITY_ADMIN |
| TC-IAM-03 | POST `/sign-up` email duplicado | 400, mensaje "ya está registrado" |
| TC-IAM-04 | POST `/sign-up` body vacío | 400, details[] con campos inválidos |
| TC-IAM-05 | POST `/sign-in` correcto | 200, tokens actualizados |
| TC-IAM-06 | POST `/sign-in` clave incorrecta | 401 |
| TC-IAM-07 | GET `/me` con Bearer token | 200, UserProfileResponse |
| TC-IAM-08 | GET `/me` sin token | 401 |
| TC-IAM-09 | POST `/refresh` | 200, refreshToken diferente al anterior |
| TC-IAM-10 | POST `/sign-out` | 204 |
| TC-IAM-11 | POST `/refresh` con token revocado | 401, mensaje "inválido" |

---

## 14. Bounded Context: Reservations

**Responsable:** Ricardo Tejada | **Guía completa:** `docs/bc/SETUP-RESERVATIONS.md`

### 14.1 Archivos implementados

```
src/main/java/com/grupo1/mindbody/reservations/
├── controller/ReservationController.java
├── dto/ReservationRequest.java, ReservationResponse.java,
│       ReservationQrResult.java  ← DTO interno para comunicación con BC Attendance
├── exception/ReservationNotFoundException.java, DuplicateReservationException.java
├── model/Reservation.java, ReservationStatus.java (enum)
├── repository/ReservationRepository.java
└── service/IReservationService.java, ReservationService.java
```

### 14.2 Endpoints

| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| POST | `/api/v1/reservations` | Bearer | Crear reserva — `userId` se extrae del JWT |
| GET | `/api/v1/reservations` | Bearer | Mis reservas (paginado) |
| GET | `/api/v1/reservations/activity/{activityId}` | Bearer (ADMIN) | Reservas de actividad |
| DELETE | `/api/v1/reservations/{id}` | Bearer | Cancelar reserva propia |

### 14.3 Casos de prueba (Postman)

| ID | Endpoint | Resultado esperado |
|----|----------|-------------------|
| TC-RES-00a/b | Sign-up + Sign-in (estudiante) | Setup token para los tests siguientes |
| TC-RES-00c | POST `/activities` (setup) | 201, crea actividad con maxCapacity=3 |
| TC-RES-01 | POST `/reservations` | 201, status=CONFIRMED, qrCode generado |
| TC-RES-02 | POST `/reservations` (duplicado) | 400, "reserva activa" |
| TC-RES-03 | POST `/reservations` activityId=99999 | 404 |
| TC-RES-04 | GET `/reservations` | 200, PageResponse con al menos 1 elemento |
| TC-RES-05 | GET `/reservations/activity/{id}` | 200, array con qrCode en cada item |
| TC-RES-06 | DELETE `/reservations/{id}` | 204 |
| TC-RES-07 | GET `/reservations` | 200, reserva aparece con status CANCELLED |
| TC-RES-08 | DELETE `/reservations/{id}` (ya cancelada) | 400, "ya está cancelada" |

---

## 15. Bounded Context: Attendance

**Responsable:** Joaquín Ruiz | **Guía completa:** `docs/bc/SETUP-ATTENDANCE.md`

### 15.1 Archivos implementados

```
src/main/java/com/grupo1/mindbody/attendance/
├── controller/AttendanceController.java
├── dto/QrScanRequest.java, AttendanceResponse.java, AttendanceSummaryResponse.java
├── exception/AttendanceAlreadyRegisteredException.java, InvalidQrCodeException.java
├── model/AttendanceRecord.java
├── repository/AttendanceRepository.java
└── service/IAttendanceService.java, AttendanceService.java
```

### 15.2 Endpoints

| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| POST | `/api/v1/attendance/scan` | Bearer | Escanear QR → registrar asistencia |
| GET | `/api/v1/attendance/activity/{activityId}` | Bearer | Lista de asistentes |
| GET | `/api/v1/attendance/my` | Bearer | Mi historial de asistencia |
| GET | `/api/v1/attendance/activity/{activityId}/summary` | Bearer | Resumen con tasa de asistencia |

### 15.3 Casos de prueba (Postman)

| ID | Endpoint | Resultado esperado |
|----|----------|-------------------|
| TC-ATT-00a/b | `/auth/sign-up` + `/sign-in` (admin) | Setup token admin |
| TC-ATT-00c | POST `/activities` | 201, captura activity_id |
| TC-ATT-00d/e | `/auth/sign-up` + `/sign-in` (estudiante) | Setup token estudiante |
| TC-ATT-00f | POST `/reservations` | 201, captura qr_code |
| TC-ATT-01 | POST `/attendance/scan` | 201, registro con scannedAt |
| TC-ATT-02 | POST `/attendance/scan` (duplicado) | 400, "ya fue registrada" |
| TC-ATT-03 | POST `/attendance/scan` (QR inválido) | 400, "ninguna reserva" |
| TC-ATT-04 | GET `/attendance/activity/{id}` | 200, array con registros |
| TC-ATT-05 | GET `/attendance/my` | 200, historial del estudiante |
| TC-ATT-06 | GET `/attendance/activity/{id}/summary` | 200, totalAttended=1, attendanceRate>0 |

---

## 16. Bounded Context: Institutions

**Responsable:** Transversal (todos los BCs dependen de este)  
**Corrección:** Exigida por el profesor — `institution_id` existía suelto como `Long` en User y Activity

### 16.1 Archivos implementados

```
src/main/java/com/grupo1/mindbody/institutions/
├── controller/
│   └── InstitutionController.java     ← POST /institutions, GET /institutions, GET /institutions/{id}
├── service/
│   ├── IInstitutionService.java       ← Interfaz: create, findAll, findById
│   └── InstitutionService.java        ← Implementación con validación de nombre duplicado
├── repository/
│   └── InstitutionRepository.java     ← Spring Data JPA + existsByName()
├── model/
│   └── Institution.java               ← Entidad JPA (tabla: institutions)
├── dto/
│   ├── InstitutionRequest.java        ← Record: @NotBlank String name
│   └── InstitutionResponse.java       ← Record: Long id, String name, LocalDateTime createdAt
├── mapper/
│   └── InstitutionMapper.java         ← MapStruct: InstitutionRequest → Institution, Institution → InstitutionResponse
└── exception/
    └── InstitutionNotFoundException.java ← Extiende ResourceNotFoundException → 404
```

### 16.2 Modelo de datos

```
institutions
├── id          BIGSERIAL PRIMARY KEY
├── name        VARCHAR NOT NULL UNIQUE
└── created_at  TIMESTAMP NOT NULL
```

FK desde otras tablas:
- `users.institution_id → institutions.id` (nullable — admins bootstrap sin institución)
- `activities.institution_id → institutions.id` (NOT NULL — toda actividad pertenece a una institución)

### 16.3 Endpoints

| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| POST | `/api/v1/institutions` | Bearer | Crear institución (nombre único) |
| GET | `/api/v1/institutions` | Bearer | Listar todas las instituciones |
| GET | `/api/v1/institutions/{id}` | Bearer | Obtener institución por ID |

### 16.4 Reglas de negocio

- El nombre de la institución debe ser único: duplicado → `BusinessRuleException` → 400
- `createdAt` se asigna en `@PrePersist` — no es modificable (`updatable = false`)
- `institutionId` en `SignUpRequest` es **opcional** para permitir registrar el primer admin sin institución (flujo bootstrap)
- `institutionId` en `ActivityRequest` es **obligatorio** (`@NotNull`)

### 16.5 Flujo de arranque (bootstrap)

Antes de registrar usuarios con institución o crear actividades, se debe:

1. Ejecutar `TC-INS-00`: registrar un admin temporal **sin** `institutionId`
2. Ejecutar `TC-INS-01`: crear la institución → captura `institution_id`
3. A partir de aquí todos los demás sign-up y creaciones de actividad pueden usar `institutionId: 1`

### 16.6 Casos de prueba (Postman)

| ID | Endpoint | Resultado esperado |
|----|----------|-------------------|
| TC-INS-00 | POST `/auth/sign-up` (sin institutionId) | 201 — bootstrap para obtener token |
| TC-INS-01 | POST `/institutions` | 201 — captura institution_id |
| TC-INS-02 | POST `/institutions` (nombre duplicado) | 400, mensaje "Ya existe" |
| TC-INS-03 | GET `/institutions` | 200, array con al menos 1 elemento |
| TC-INS-04 | GET `/institutions/{id}` | 200, id y name correctos |
| TC-INS-05 | GET `/institutions/99999` | 404 |
