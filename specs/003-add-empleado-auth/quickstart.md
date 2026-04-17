# Quickstart - Autenticacion Por Empleado

## Prerequisitos
- Java 17
- Maven 3.9+
- Docker y Docker Compose

## 1) Levantar infraestructura

Desde raiz del proyecto:

```bash
DB_HOST_PORT=5433 docker compose -f docker/docker-compose.yml up -d --build
```

## 2) Verificar API disponible

```bash
curl -s http://localhost:8080/v3/api-docs | head
```

## 3) Crear datos base de negocio

Crear departamento:

```bash
curl -u master:admin123 -X POST http://localhost:8080/api/v1/departamentos \
  -H 'Content-Type: application/json' \
  -d '{"nombre":"Recursos Humanos"}'
```

Crear empleado:

```bash
curl -u master:admin123 -X POST http://localhost:8080/api/v1/empleados \
  -H 'Content-Type: application/json' \
  -d '{"nombre":"Ana Lopez","direccion":"Calle 1","telefono":"555111111","departamentoClave":"D-0001"}'
```

## 4) Asignar credenciales por empleado (master)

```bash
curl -u master:admin123 -X POST http://localhost:8080/api/v1/empleados/E-0001/credenciales \
  -H 'Content-Type: application/json' \
  -d '{"username":"ana.lopez","email":"ana.lopez@empresa.com","password":"AnaSegura2026!"}'
```

Resultado esperado:
- `201 Created`.
- Respuesta sin `password` ni `passwordHash`.

## 5) Validar autenticacion por empleado

```bash
curl -u ana.lopez:AnaSegura2026! http://localhost:8080/api/v1/auth/empleado/me
curl -u ana.lopez@empresa.com:AnaSegura2026! http://localhost:8080/api/v1/auth/empleado/me
```

Resultado esperado:
- `200 OK` con `empleadoClave`, `username`, `nombre`, `authStatus`.

## 6) Validar rechazo por credenciales invalidas

```bash
curl -u ana.lopez:Incorrecta123! http://localhost:8080/api/v1/auth/empleado/me
```

Resultado esperado:
- `401 Unauthorized`.

## 7) Validar bloqueo temporal por intentos fallidos

Ejecutar 5 intentos fallidos en <= 15 minutos:

```bash
for i in 1 2 3 4 5; do
  curl -s -o /dev/null -w "Intento $i -> HTTP:%{http_code}\n" \
    -u ana.lopez:BadPass${i}! \
    http://localhost:8080/api/v1/auth/empleado/me
done
```

Intento adicional durante bloqueo:

```bash
curl -u ana.lopez:AnaSegura2026! http://localhost:8080/api/v1/auth/empleado/me
```

Resultado esperado:
- Cuenta bloqueada temporalmente con respuesta externa `401 Unauthorized`.

## 8) Cambiar contrasena y validar invalidacion de la anterior

Primero consultar version actual:

```bash
curl -u admin:admin123 http://localhost:8080/api/v1/empleados/E-0001/credenciales
```

Luego actualizar (ejemplo con `version: 0`):

```bash
curl -u master:admin123 -X PUT http://localhost:8080/api/v1/empleados/E-0001/credenciales \
  -H 'Content-Type: application/json' \
  -d '{"email":"ana.lopez+rrhh@empresa.com","password":"NuevaSegura2026!","version":0}'
```

Probar antigua contrasena:

```bash
curl -u ana.lopez:AnaSegura2026! http://localhost:8080/api/v1/auth/empleado/me
```

Probar nueva contrasena:

```bash
curl -u ana.lopez:NuevaSegura2026! http://localhost:8080/api/v1/auth/empleado/me
```

Resultado esperado:
- Antigua: rechazada.
- Nueva: aceptada.

## 9) Desactivar credenciales y validar rechazo

Consultar version mas reciente antes de cambiar estado:

```bash
curl -u master:admin123 http://localhost:8080/api/v1/empleados/E-0001/credenciales
```

```bash
curl -u master:admin123 -X PATCH http://localhost:8080/api/v1/empleados/E-0001/credenciales/estado \
  -H 'Content-Type: application/json' \
  -d '{"activa":false,"version":<version-actual>}'
```

Reintentar acceso del empleado:

```bash
curl -u ana.lopez:NuevaSegura2026! http://localhost:8080/api/v1/auth/empleado/me
```

Resultado esperado:
- Rechazo de autenticacion para cuenta inactiva.

## 10) Validar contrato Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 11) Validar unicidad global de email

Intentar crear credenciales para otro empleado reutilizando un `email` existente:

```bash
curl -u master:admin123 -X POST http://localhost:8080/api/v1/empleados/E-0002/credenciales \
  -H 'Content-Type: application/json' \
  -d '{"username":"ana.duplicada","email":"ana.lopez@empresa.com","password":"DuplicadaSegura2026!"}'
```

Resultado esperado:
- `409 Conflict` por duplicidad global de `email`.

## 12) Validar cuenta master y restricciones por rol

Validar sesion master:

```bash
curl -u master:admin123 http://localhost:8080/api/v1/auth/empleado/me
```

Validar que empleado no puede operar CRUD administrativo:

```bash
curl -u ana.lopez@empresa.com:NuevaSegura2026! "http://localhost:8080/api/v1/empleados?page=0"
```

Resultado esperado:
- Master: `200` con roles administrativos.
- Empleado en CRUD: `403 Forbidden`.

## 13) Validar CORS para frontend local

```bash
curl -i -X OPTIONS "http://localhost:8080/api/v1/auth/empleado/me" \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: GET"
```

Resultado esperado:
- Presencia de `Access-Control-Allow-Origin: http://localhost:4200`.
- Métodos permitidos incluyen `OPTIONS`.

## 14) Registro de validacion manual end-to-end (T046)

Fecha: 2026-03-10

Entorno ejecutado:

- `DB_HOST_PORT=5433 APP_HOST_PORT=8081 docker compose -f docker/docker-compose.yml up -d --build`
- App en `http://localhost:8081`

Resultados observados:

- `POST /api/v1/departamentos` -> `201 Created` (ejemplo: `D-0007`).
- `POST /api/v1/empleados` -> `201 Created` (ejemplo: `E-0009`).
- `POST /api/v1/empleados/{clave}/credenciales` -> `201 Created`.
- `GET /api/v1/auth/empleado/me` con credenciales correctas -> `200 OK`.
- `GET /api/v1/auth/empleado/me` con password incorrecta -> `401 Unauthorized`.
- 5 intentos fallidos consecutivos -> `401` en cada intento.
- Intento con password correcta durante lockout -> `401 Unauthorized`.
- `PUT /api/v1/empleados/{clave}/credenciales` con version vigente -> `200 OK`.
- Login con password anterior tras cambio -> `401 Unauthorized`.
- Login con password nueva tras cambio -> `200 OK`.
- `PATCH /api/v1/empleados/{clave}/credenciales/estado` con version desactualizada -> `409 Conflict`.
- `PATCH /api/v1/empleados/{clave}/credenciales/estado` con version actual -> `200 OK`.
- Login de cuenta inactiva -> `401 Unauthorized`.

## 15) Matriz de cobertura FR y SC (T047)

| Item | Cobertura | Evidencia |
|------|-----------|-----------|
| FR-001 | Creacion de credenciales por empleado | `POST /api/v1/empleados/{clave}/credenciales` -> `201` |
| FR-002 | Username unico global | Validacion de unicidad en `CredencialEmpleadoService` + respuesta `409` |
| FR-003 | Login solo con usuario/password validos y cuenta activa | `GET /api/v1/auth/empleado/me` -> `200` con credenciales correctas |
| FR-004 | Rechazo sin exponer causa exacta | Fallos de auth -> `401` uniforme |
| FR-005 | Rechazo para empleado inactivo/eliminado | Cuenta inactiva -> `401` |
| FR-006 | Master bootstrap gestiona credenciales | Endpoints de credenciales y CRUD restringidos a rol `ADMIN` |
| FR-007 | Password anterior invalidada tras cambio | Login con password anterior -> `401` |
| FR-008 | Politica password 12..128 + denylist | `PasswordPolicyService` aplicado en alta/cambio |
| FR-009 | Lockout 5/15 con bloqueo 30 min | 5 fallos + intento valido durante bloqueo -> `401` |
| FR-010 | Auditoria de exitos/fallos/bloqueo | `EventoAutenticacionService` registra `LOGIN_SUCCESS`, `LOGIN_FAILED`, `ACCOUNT_LOCKED` |
| FR-011 | No exponer password/hash | `CredencialEmpleadoResponse` no incluye hash/password |
| FR-012 | Trazabilidad `passwordUpdatedAt` y `updatedAt` | Campos presentes en entidad y respuesta |
| FR-013 | Empleado sin credencial no inicia sesion | Resolucion en `EmpleadoAuthUserDetailsService`/provider |
| FR-014 | Cuenta bootstrap `master` externalizable | Props `app.auth.bootstrap.*` en `application.properties` |
| FR-015 | Auth fallida siempre `401` incluyendo lockout | `AuthenticationEntryPoint` uniforme + evidencia lockout |
| FR-016 | Concurrencia optimista por `version` y `409` | `PUT/PATCH` con version desactualizada -> `409` |
| FR-017 | Persistencia de `email` (`varchar`) en credenciales | `POST/PUT/GET /api/v1/empleados/{clave}/credenciales` incluye `email` |
| FR-018 | Validacion de formato de `email` | Alta/actualizacion con email invalido -> `400 Bad Request` |
| FR-019 | Unicidad global de `email` (incluye inactivas) | Alta/actualizacion con `email` duplicado -> `409 Conflict` |
| FR-021 | Login por `username` o `email` | `/auth/empleado/me` con ambos principales -> `200` |
| FR-022 | `/me` retorna roles e identidad bootstrap | `/auth/empleado/me` de master incluye `roles` y `empleadoClave=MASTER` |
| FR-023 | CORS local habilitado con preflight | `OPTIONS /auth/empleado/me` retorna headers CORS esperados |
| SC-001 | Empleados con credenciales activas autentican | Login exitoso `200` en validacion manual |
| SC-002 | Intentos invalidos rechazados | Login invalido `401` consistente |
| SC-003 | >=95% logins exitosos <3s | Ver seccion 16 |
| SC-004 | Cambio de password invalida la anterior | Evidencia `401` para password vieja |
| SC-005 | Bloqueos registrados en auditoria | Servicio de eventos + lockout integrado |
| SC-006 | Altas/actualizaciones persisten y devuelven `email` valido | Respuestas de credenciales muestran `email` actualizado |

## 16) Evidencia de rendimiento de autenticacion (T048)

Fecha: 2026-03-10

Ejecucion:

- Usuario de prueba: `perf.1773198005`
- Muestra: `40` autenticaciones exitosas a `GET /api/v1/auth/empleado/me`
- Resultado: `40/40` bajo `3s` (`100.00%`)

Conclusion:

- SC-003 cumplido (`>=95%` de logins exitosos en menos de `3s`).
