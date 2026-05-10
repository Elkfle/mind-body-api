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
