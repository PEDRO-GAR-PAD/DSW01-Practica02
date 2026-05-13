# Quickstart - CRUD de Departamentos (v1)

## Prerequisitos
- Java 17
- Maven 3.9+
- Docker y Docker Compose

## 1) Levantar PostgreSQL con Docker

Desde la raíz del proyecto:

```bash
DB_HOST_PORT=5433 docker compose -f docker/docker-compose.yml up -d postgres
```

## 2) Ejecutar la API

```bash
mvn spring-boot:run
```

Opcional (si usas puerto DB alterno):

```bash
DB_URL='jdbc:postgresql://localhost:5433/dsw01_practica02' mvn spring-boot:run
```

## 3) Probar autenticación

Credenciales de desarrollo:
- usuario: `admin`
- contraseña: `admin123`

## 4) Probar endpoints de departamentos

Base URL: `http://localhost:8080`

- `POST /api/v1/departamentos`
- `GET /api/v1/departamentos?page=0`
- `GET /api/v1/departamentos/{clave}`
- `PUT /api/v1/departamentos/{clave}`
- `DELETE /api/v1/departamentos/{clave}`

Ejemplo rápido de alta:

```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/departamentos \
  -H 'Content-Type: application/json' \
  -d '{"nombre":"Recursos Humanos"}'
```

## 5) Validar reglas de negocio

- Intentar alta con `nombre` vacío debe devolver `400 Bad Request`.
- Consultar una `clave` inexistente debe devolver `404 Not Found`.
- Al superar el rango de claves hasta `D-9999`, la alta debe devolver `409 Conflict`.
- Consumir `/api/departamentos` (sin versión) debe devolver `404 Not Found`.
- Consumir listado con `page=-1` debe devolver `400 Bad Request`.
- Consumir listado con `page=999` debe devolver `200` con `content` vacío.
- Consumir cualquier endpoint sin credenciales debe devolver `401 Unauthorized`.
- Intentar eliminar un departamento referenciado por empleados debe devolver `409 Conflict`.

Escenario de baja:
- Eliminar un departamento existente con `DELETE /api/v1/departamentos/{clave}` debe devolver `204 No Content`.
- Consultar luego el mismo `{clave}` debe devolver `404 Not Found`.

## 6) Validar documentación Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 7) Registro de validación manual (T036)

Fecha: 2026-03-10

Entorno ejecutado:

- `DB_HOST_PORT=5433 docker compose -f docker/docker-compose.yml up -d --build`
- App en `http://localhost:8080`

Resultados observados:

- `POST /api/v1/departamentos` con nombre válido -> `201 Created`.
- `GET /api/v1/departamentos/{clave}` sobre recurso existente -> `200 OK`.
- `GET /api/v1/departamentos?page=999` -> `200 OK` con `content: []`.
- `GET /api/v1/departamentos?page=-1` -> `400 Bad Request`.
- `GET /api/departamentos` (legacy) -> `404 Not Found`.
- `GET /api/v1/departamentos` sin credenciales -> `401 Unauthorized`.
- `POST /api/v1/departamentos` con `nombre` vacío -> `400 Bad Request` con `fieldErrors.nombre`.
- `DELETE /api/v1/departamentos/{clave}` referenciado por empleados -> `409 Conflict`.
- `DELETE /api/v1/departamentos/{clave}` no referenciado -> `204 No Content`.
- `GET /api/v1/departamentos/{clave}` posterior a baja -> `404 Not Found`.