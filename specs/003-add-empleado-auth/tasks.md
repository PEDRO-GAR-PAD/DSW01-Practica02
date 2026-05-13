# Tasks: Autenticacion Por Empleado

**Input**: Design documents from `/specs/003-add-empleado-auth/`
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/`, `quickstart.md`

**Tests**: No se agregan tareas de pruebas automatizadas porque la especificacion no exige enfoque TDD ni suite obligatoria.

**Organization**: Tareas agrupadas por historia de usuario para habilitar implementacion y validacion independiente.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar configuracion base y documentacion operativa para autenticacion por empleado.

- [X] T001 Alinear dependencias de seguridad/autenticacion del backend en `pom.xml`
- [X] T002 Configurar propiedades bootstrap, password policy y lockout en `src/main/resources/application.properties`
- [X] T003 [P] Publicar metadata de propiedades de auth para tooling en `src/main/resources/META-INF/additional-spring-configuration-metadata.json`
- [X] T004 [P] Ajustar variables de entorno de autenticacion en `docker/docker-compose.yml`
- [X] T005 [P] Documentar modelo de seguridad operativa (master bootstrap + empleado) en `docs/api/security.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura bloqueante que debe estar lista antes de implementar historias.

**CRITICAL**: Ninguna historia de usuario inicia antes de cerrar esta fase.

- [X] T006 Implementar/ajustar migracion de esquema de credenciales con `email` unico global y tabla de eventos en `src/main/java/com/dsw01/practica02/config/AuthSchemaMigration.java`
- [X] T007 [P] Modelar entidad de credenciales con `email`, `version` y timestamps en `src/main/java/com/dsw01/practica02/domain/CredencialEmpleado.java`
- [X] T008 [P] Modelar entidad auditable de eventos de autenticacion en `src/main/java/com/dsw01/practica02/domain/EventoAutenticacion.java`
- [X] T009 [P] Definir tipos de evento de autenticacion en `src/main/java/com/dsw01/practica02/domain/TipoEventoAutenticacion.java`
- [X] T010 [P] Definir resultado de eventos de autenticacion en `src/main/java/com/dsw01/practica02/domain/ResultadoAutenticacion.java`
- [X] T011 [P] Crear acceso a datos de credenciales por `username`, `email` y empleado en `src/main/java/com/dsw01/practica02/repository/CredencialEmpleadoRepository.java`
- [X] T012 [P] Crear acceso a datos para auditoria de autenticacion en `src/main/java/com/dsw01/practica02/repository/EventoAutenticacionRepository.java`
- [X] T013 [P] Estandarizar errores funcionales `400/401/404/409` de credenciales en `src/main/java/com/dsw01/practica02/config/GlobalExceptionHandler.java`
- [X] T014 Implementar politica de password (12..128 + denylist) en `src/main/java/com/dsw01/practica02/service/PasswordPolicyService.java`
- [X] T015 Implementar lockout por intentos fallidos (5/15m -> 30m) en `src/main/java/com/dsw01/practica02/service/AuthLockoutService.java`
- [X] T016 Implementar carga de usuarios (bootstrap admin + empleado) con validacion de estado en `src/main/java/com/dsw01/practica02/service/EmpleadoAuthUserDetailsService.java`
- [X] T017 Implementar proveedor de autenticacion con control de lockout y auditoria en `src/main/java/com/dsw01/practica02/service/EmpleadoAuthenticationProvider.java`
- [X] T018 Configurar pipeline de seguridad HTTP Basic y entrypoint uniforme `401` en `src/main/java/com/dsw01/practica02/config/SecurityConfig.java`
- [X] T019 [P] Crear mapper para respuestas de credenciales sin exponer hash en `src/main/java/com/dsw01/practica02/mapper/CredencialEmpleadoMapper.java`

**Checkpoint**: Fundacion completa, historias habilitadas para implementacion.

---

## Phase 3: User Story 1 - Iniciar Sesion Como Empleado (Priority: P1) MVP

**Goal**: Permitir autenticacion individual de empleado y consulta de identidad autenticada.

**Independent Test**: Crear credenciales activas para un empleado, autenticar `GET /api/v1/auth/empleado/me` con `200`, y validar `401` con credenciales invalidas o cuenta inactiva.

### Implementation for User Story 1

- [X] T020 [P] [US1] Crear DTO de respuesta `me` en `src/main/java/com/dsw01/practica02/dto/EmpleadoAuthMeResponse.java`
- [X] T021 [US1] Implementar resolucion de principal autenticado en `src/main/java/com/dsw01/practica02/service/AuthEmpleadoService.java`
- [X] T022 [US1] Exponer endpoint `GET /api/v1/auth/empleado/me` en `src/main/java/com/dsw01/practica02/controller/AuthEmpleadoController.java`
- [X] T023 [US1] Registrar eventos `LOGIN_SUCCESS` y `LOGIN_FAILED` del flujo `me` en `src/main/java/com/dsw01/practica02/service/EventoAutenticacionService.java`
- [X] T024 [US1] Reforzar rechazo de login para empleado inactivo/eliminado en `src/main/java/com/dsw01/practica02/service/EmpleadoAuthUserDetailsService.java`
- [X] T025 [P] [US1] Sincronizar contrato del endpoint `me` (200/401) en `specs/003-add-empleado-auth/contracts/openapi.yaml`
- [X] T026 [US1] Actualizar validacion manual de login basico en `specs/003-add-empleado-auth/quickstart.md`

**Checkpoint**: US1 queda funcional e independientemente demostrable.

---

## Phase 4: User Story 2 - Administrar Credenciales De Empleados (Priority: P2)

**Goal**: Permitir alta/consulta/actualizacion/desactivacion de credenciales con `email` y concurrencia optimista.

**Independent Test**: Crear credencial con `email`, actualizar `email/password` con `version` valida, validar rechazo por `email` invalido, `409` por `version` desactualizada y `409` por duplicidad global de `email`.

### Implementation for User Story 2

- [X] T027 [P] [US2] Definir request de alta con `username`, `email`, `password` en `src/main/java/com/dsw01/practica02/dto/CredencialEmpleadoCreateRequest.java`
- [X] T028 [P] [US2] Definir request de actualizacion con `email` opcional y `version` requerida en `src/main/java/com/dsw01/practica02/dto/CredencialEmpleadoUpdateRequest.java`
- [X] T029 [P] [US2] Definir request de estado con `version` para concurrencia en `src/main/java/com/dsw01/practica02/dto/CredencialEmpleadoEstadoRequest.java`
- [X] T030 [P] [US2] Exponer `email` en response de credenciales en `src/main/java/com/dsw01/practica02/dto/CredencialEmpleadoResponse.java`
- [X] T031 [US2] Implementar alta administrativa con unicidad global de `username/email` en `src/main/java/com/dsw01/practica02/service/CredencialEmpleadoService.java`
- [X] T032 [US2] Implementar actualizacion administrativa de `username/email/password` con `version` en `src/main/java/com/dsw01/practica02/service/CredencialEmpleadoService.java`
- [X] T033 [US2] Implementar cambio de estado activo/inactivo con `version` en `src/main/java/com/dsw01/practica02/service/CredencialEmpleadoService.java`
- [X] T034 [US2] Exponer endpoints POST/GET/PUT/PATCH de credenciales por empleado en `src/main/java/com/dsw01/practica02/controller/CredencialEmpleadoController.java`
- [X] T035 [US2] Registrar eventos de cambio de password y estado de credenciales en `src/main/java/com/dsw01/practica02/service/EventoAutenticacionService.java`
- [X] T036 [P] [US2] Sincronizar OpenAPI para `email` y conflictos `409` de username/email/version en `specs/003-add-empleado-auth/contracts/openapi.yaml`
- [X] T037 [US2] Actualizar escenarios admin de alta/actualizacion y duplicidad global de email en `specs/003-add-empleado-auth/quickstart.md`

**Checkpoint**: US2 queda funcional e independientemente demostrable.

---

## Phase 5: User Story 3 - Proteger El Acceso Ante Riesgos (Priority: P3)

**Goal**: Aplicar lockout temporal y mantener respuesta externa uniforme `401` con auditoria interna.

**Independent Test**: Forzar 5 fallos consecutivos en <=15 minutos, validar bloqueo de 30 minutos con `401`, y confirmar reingreso exitoso al expirar el lockout.

### Implementation for User Story 3

- [X] T038 [US3] Aplicar politica de lockout en flujo de autenticacion de empleado en `src/main/java/com/dsw01/practica02/service/AuthLockoutService.java`
- [X] T039 [US3] Enforzar motivo interno `LOCKED` y rechazo uniforme `401` en `src/main/java/com/dsw01/practica02/service/EmpleadoAuthenticationProvider.java`
- [X] T040 [US3] Registrar eventos `ACCOUNT_LOCKED` y rechazos asociados en `src/main/java/com/dsw01/practica02/service/EventoAutenticacionService.java`
- [X] T041 [US3] Reiniciar contadores de seguridad tras login exitoso o cambio de password en `src/main/java/com/dsw01/practica02/service/AuthLockoutService.java`
- [X] T042 [P] [US3] Sincronizar contrato de respuestas `401` para lockout en `specs/003-add-empleado-auth/contracts/openapi.yaml`
- [X] T043 [US3] Actualizar escenario de lockout y recuperacion de acceso en `specs/003-add-empleado-auth/quickstart.md`

**Checkpoint**: US3 queda funcional e independientemente demostrable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre documental, trazabilidad y validacion integral del feature.

- [X] T044 [P] Alinear modelo de datos final con validaciones de `email` y FR-019 en `specs/003-add-empleado-auth/data-model.md`
- [X] T045 [P] Alinear decisiones de investigacion con unicidad global de `email` y seguridad final en `specs/003-add-empleado-auth/research.md`
- [X] T046 Ejecutar validacion manual end-to-end y registrar evidencia en `specs/003-add-empleado-auth/quickstart.md`
- [X] T047 Actualizar matriz FR/SC incluyendo FR-017, FR-018, FR-019 y SC-006 en `specs/003-add-empleado-auth/quickstart.md`
- [X] T048 Registrar evidencia de rendimiento de autenticacion para SC-003 en `specs/003-add-empleado-auth/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Sin dependencias.
- **Phase 2 (Foundational)**: Depende de Phase 1 y bloquea todas las historias.
- **Phase 3 (US1)**: Depende de Phase 2.
- **Phase 4 (US2)**: Depende de Phase 2.
- **Phase 5 (US3)**: Depende de Phase 2 y se valida completamente cuando US1 y US2 estan operativas.
- **Phase 6 (Polish)**: Depende del cierre de US1, US2 y US3.

### User Story Dependency Graph

- **US1 (P1)**: Base de autenticacion funcional por empleado.
- **US2 (P2)**: Gestion administrativa de credenciales con `email` y `version`.
- **US3 (P3)**: Endurecimiento de acceso con lockout y auditoria.

Orden sugerido de completitud:
`US1 -> US2 -> US3`

### Parallel Opportunities

- Setup en paralelo: `T003`, `T004`, `T005`.
- Foundational en paralelo: `T007`, `T008`, `T009`, `T010`, `T011`, `T012`, `T013`, `T019`.
- US1 en paralelo: `T020`, `T025`.
- US2 en paralelo: `T027`, `T028`, `T029`, `T030`, `T036`.
- US3 en paralelo: `T042`.
- Polish en paralelo: `T044`, `T045`.

---

## Parallel Example: User Story 1

```bash
Task: "T020 [US1] Crear DTO de respuesta me en src/main/java/com/dsw01/practica02/dto/EmpleadoAuthMeResponse.java"
Task: "T025 [US1] Sincronizar contrato del endpoint me en specs/003-add-empleado-auth/contracts/openapi.yaml"
```

## Parallel Example: User Story 2

```bash
Task: "T027 [US2] Definir request de alta con email en src/main/java/com/dsw01/practica02/dto/CredencialEmpleadoCreateRequest.java"
Task: "T028 [US2] Definir request de actualizacion con email/version en src/main/java/com/dsw01/practica02/dto/CredencialEmpleadoUpdateRequest.java"
Task: "T036 [US2] Sincronizar OpenAPI para conflictos 409 de username/email/version en specs/003-add-empleado-auth/contracts/openapi.yaml"
```

## Parallel Example: User Story 3

```bash
Task: "T040 [US3] Registrar eventos ACCOUNT_LOCKED en src/main/java/com/dsw01/practica02/service/EventoAutenticacionService.java"
Task: "T042 [US3] Sincronizar contrato 401 para lockout en specs/003-add-empleado-auth/contracts/openapi.yaml"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1 (Setup).
2. Completar Phase 2 (Foundational).
3. Completar Phase 3 (US1).
4. Validar prueba independiente de US1 antes de avanzar.

### Incremental Delivery

1. Setup + Foundational.
2. Entregar US1 (autenticacion por empleado).
3. Entregar US2 (gestion de credenciales con `email` y FR-019).
4. Entregar US3 (lockout y endurecimiento de acceso).
5. Cerrar con Polish y evidencias finales.

### Parallel Team Strategy

1. Equipo completo en Setup + Foundational.
2. Luego distribucion sugerida:
- Dev A: US1 (`T020`-`T026`).
- Dev B: US2 (`T027`-`T037`).
- Dev C: US3 (`T038`-`T043`) y soporte Polish (`T044`-`T048`).
