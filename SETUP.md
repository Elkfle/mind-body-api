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
| GET | `/api/v1/activities` | — | Listar actividades paginadas (`?page=0&size=10`) |
| GET | `/api/v1/activities/{id}` | — | Obtener actividad por ID |
| POST | `/api/v1/activities?adminId={id}` | ADMIN* | Crear actividad |
| PUT | `/api/v1/activities/{id}` | ADMIN* | Actualizar actividad |
| DELETE | `/api/v1/activities/{id}` | ADMIN* | Cancelar actividad (status → CANCELLED) |
| GET | `/api/v1/activities/reports/by-category` | — | Reporte agrupado por categoría |

> *Auth temporal deshabilitada hasta que Fabrizio implemente IAM. El `adminId` se pasa por `@RequestParam` hasta integrar JWT.

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
| TC-ACT-06 | POST body vacío `{}` | 400 Bad Request — `details[]` con cada campo inválido |
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

### 11.3 Nota sobre SecurityConfig temporal

`SecurityConfig.java` deshabilita toda autenticación para que los bounded contexts puedan probarse independientemente mientras IAM no está implementado. **Fabrizio (IAM) lo reemplazará** por la configuración real con filtro JWT. No modificar este archivo — coordinar con IAM.

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
| TC-ACT-06 | POST | body vacío `{}` | 400, details[] lista los campos que fallaron validación |
| TC-ACT-07 | POST | `/api/v1/activities?adminId=1` | 201, segunda actividad (BASKETBALL) para poblar el reporte |
| TC-ACT-08 | GET | `/api/v1/activities/reports/by-category` | 200, array con category/totalActivities/totalEnrollment, mínimo 2 categorías |
| TC-ACT-09 | DELETE | `/api/v1/activities/{{activity_id}}` | 204 No Content |
| TC-ACT-10 | GET | `/api/v1/activities/{{activity_id}}` | 200, status es CANCELLED (el registro no se elimina) |

### 12.5 Convención para colecciones de otros BCs

Cada integrante debe agregar su colección en `docs/postman/` con el nombre `mindbody-<bc>.postman_collection.json`:

```
docs/postman/
├── mindbody-activities.postman_collection.json   ← Colfer (listo)
├── mindbody-iam.postman_collection.json          ← Bussalleu (pendiente)
├── mindbody-reservations.postman_collection.json ← Tejada (pendiente)
└── mindbody-attendance.postman_collection.json   ← Ruiz (pendiente)
```

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
