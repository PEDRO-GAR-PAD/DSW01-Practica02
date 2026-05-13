# Tasks: CRUD de Departamentos

**Input**: Design documents from `/specs/002-crud-departamentos/`  
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/openapi.yaml`, `quickstart.md`

**Tests**: No se generan tareas de pruebas automáticas porque la especificación no solicita enfoque TDD ni suite obligatoria en esta fase.

**Organization**: Las tareas están agrupadas por historia de usuario para implementación y validación independiente.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar estructura y componentes base del módulo de departamentos.

- [X] T001 Crear entidad JPA Departamento en src/main/java/com/dsw01/practica02/domain/Departamento.java
- [X] T002 Crear repositorio de departamentos en src/main/java/com/dsw01/practica02/repository/DepartamentoRepository.java
- [X] T003 [P] Crear generador de clave `D-0001` en src/main/java/com/dsw01/practica02/service/ClaveDepartamentoGenerator.java
- [X] T004 [P] Crear excepción de no encontrado en src/main/java/com/dsw01/practica02/exception/DepartamentoNotFoundException.java
- [X] T005 [P] Crear excepción de capacidad agotada en src/main/java/com/dsw01/practica02/exception/DepartamentoKeyCapacityExceededException.java

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura transversal obligatoria para todas las historias.

**⚠️ CRITICAL**: Ninguna historia puede comenzar hasta cerrar esta fase.

- [X] T006 Crear DTO de listado paginado en src/main/java/com/dsw01/practica02/dto/DepartamentoPageResponse.java
- [X] T007 [P] Integrar errores estandarizados de departamentos (400/401/404/409) en src/main/java/com/dsw01/practica02/config/GlobalExceptionHandler.java
- [X] T008 [P] Implementar esqueleto base de servicio de departamentos en src/main/java/com/dsw01/practica02/service/DepartamentoService.java
- [X] T009 [P] Implementar esqueleto base de controlador versionado en src/main/java/com/dsw01/practica02/controller/DepartamentoController.java
- [X] T010 Implementar rutas legacy 404 para departamentos en src/main/java/com/dsw01/practica02/controller/LegacyDepartamentoController.java
- [X] T011 Verificar autenticación HTTP Basic para endpoints de departamentos en src/main/java/com/dsw01/practica02/config/SecurityConfig.java
- [X] T012 Sincronizar contrato base paginado y respuestas de error en specs/002-crud-departamentos/contracts/openapi.yaml

**Checkpoint**: Base técnica lista para implementar historias en paralelo.

---

## Phase 3: User Story 1 - Registrar y consultar departamentos (Priority: P1) 🎯 MVP

**Goal**: Permitir alta y consulta paginada de departamentos bajo `/api/v1`, con validaciones, auth y manejo de rutas no versionadas.

**Independent Test**: Crear un departamento válido, consultarlo por `clave`, listar con `page=0`, validar `400` con `page=-1`, validar `200` con `content` vacío en página fuera de rango, validar `404` en `/api/departamentos`.

### Implementation for User Story 1

- [X] T013 [P] [US1] Crear DTO de creación en src/main/java/com/dsw01/practica02/dto/DepartamentoCreateRequest.java
- [X] T014 [P] [US1] Crear DTO de respuesta en src/main/java/com/dsw01/practica02/dto/DepartamentoResponse.java
- [X] T015 [US1] Implementar creación con clave autogenerada y límite `D-9999` en src/main/java/com/dsw01/practica02/service/DepartamentoService.java
- [X] T016 [US1] Implementar consulta por clave en src/main/java/com/dsw01/practica02/service/DepartamentoService.java
- [X] T017 [US1] Implementar listado paginado con tamaño fijo 5 y reglas FR-016 en src/main/java/com/dsw01/practica02/service/DepartamentoService.java
- [X] T018 [US1] Implementar endpoint POST /api/v1/departamentos en src/main/java/com/dsw01/practica02/controller/DepartamentoController.java
- [X] T019 [US1] Implementar endpoint GET /api/v1/departamentos/{clave} en src/main/java/com/dsw01/practica02/controller/DepartamentoController.java
- [X] T020 [US1] Implementar endpoint GET /api/v1/departamentos?page= en src/main/java/com/dsw01/practica02/controller/DepartamentoController.java
- [X] T021 [US1] Implementar rechazo de `clave` manual y validaciones de `nombre` en src/main/java/com/dsw01/practica02/dto/DepartamentoCreateRequest.java
- [X] T022 [US1] Sincronizar OpenAPI para alta/consultas/paginación y errores estándar en specs/002-crud-departamentos/contracts/openapi.yaml
- [X] T023 [US1] Actualizar quickstart con validaciones de 401/404/400/200 vacío en specs/002-crud-departamentos/quickstart.md

**Checkpoint**: US1 funcional y validable de forma independiente.

---

## Phase 4: User Story 2 - Actualizar nombre de departamento (Priority: P2)

**Goal**: Permitir actualizar nombre preservando `clave`, con validación y errores estándar.

**Independent Test**: Actualizar un departamento existente, confirmar que conserva `clave`, validar `404` para inexistente y `400` para nombre inválido.

### Implementation for User Story 2

- [X] T024 [P] [US2] Crear DTO de actualización en src/main/java/com/dsw01/practica02/dto/DepartamentoUpdateRequest.java
- [X] T025 [US2] Implementar lógica de actualización preservando clave en src/main/java/com/dsw01/practica02/service/DepartamentoService.java
- [X] T026 [US2] Implementar endpoint PUT /api/v1/departamentos/{clave} en src/main/java/com/dsw01/practica02/controller/DepartamentoController.java
- [X] T027 [US2] Aplicar validaciones de nombre en src/main/java/com/dsw01/practica02/dto/DepartamentoUpdateRequest.java
- [X] T028 [P] [US2] Sincronizar OpenAPI para actualización y errores asociados en specs/002-crud-departamentos/contracts/openapi.yaml

**Checkpoint**: US2 funcional sin depender de US3.

---

## Phase 5: User Story 3 - Eliminar departamentos (Priority: P3)

**Goal**: Permitir eliminación por clave con respuesta 204 y no encontrado en consultas posteriores.

**Independent Test**: Eliminar un departamento existente y validar que una consulta posterior por `clave` devuelve `404`.

### Implementation for User Story 3

- [X] T029 [US3] Implementar lógica de eliminación por clave en src/main/java/com/dsw01/practica02/service/DepartamentoService.java
- [X] T030 [US3] Implementar endpoint DELETE /api/v1/departamentos/{clave} en src/main/java/com/dsw01/practica02/controller/DepartamentoController.java
- [X] T031 [P] [US3] Sincronizar OpenAPI para eliminación y respuestas asociadas en specs/002-crud-departamentos/contracts/openapi.yaml
- [X] T032 [US3] Actualizar quickstart con escenario de eliminación y verificación 404 en specs/002-crud-departamentos/quickstart.md

**Checkpoint**: Las tres historias CRUD quedan completas e independientes.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre de consistencia funcional y documental transversal.

- [X] T033 [P] Alinear modelo de datos con listado paginado y ApiError estándar en specs/002-crud-departamentos/data-model.md
- [X] T034 [P] Documentar seguridad y acceso de departamentos en docs/api/security.md
- [X] T035 Verificar que Swagger expone únicamente rutas versionadas de departamentos en specs/002-crud-departamentos/contracts/openapi.yaml
- [X] T036 Ejecutar validación manual de quickstart versionado en specs/002-crud-departamentos/quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: inicia inmediatamente.
- **Phase 2 (Foundational)**: depende de Phase 1 y bloquea todas las historias.
- **Phase 3-5 (User Stories)**: dependen de Phase 2; se prioriza P1 → P2 → P3.
- **Phase 6 (Polish)**: depende de historias completadas.

### User Story Dependency Graph

- **US1 (P1)** → habilita MVP.
- **US2 (P2)** → depende de departamentos existentes (creados en US1).
- **US3 (P3)** → depende de departamentos existentes (creados en US1).

Representación:
`US1 -> US2`  
`US1 -> US3`

### Within Each User Story

- DTOs antes de servicio.
- Servicio antes de controlador.
- Controlador antes de sincronización final del contrato OpenAPI.

### Parallel Opportunities

- Setup: `T003`, `T004`, `T005` en paralelo tras `T001` y `T002`.
- Foundational: `T007`, `T008`, `T009` en paralelo tras `T006`.
- US1: `T013` y `T014` en paralelo.
- US2: `T024` y `T028` en paralelo.
- US3: `T031` en paralelo con implementación de servicio/controlador.
- Polish: `T033` y `T034` en paralelo.

---

## Parallel Example: User Story 1

```bash
Task: "T013 [US1] Crear DTO de creación en src/main/java/com/dsw01/practica02/dto/DepartamentoCreateRequest.java"
Task: "T014 [US1] Crear DTO de respuesta en src/main/java/com/dsw01/practica02/dto/DepartamentoResponse.java"
```

## Parallel Example: User Story 2

```bash
Task: "T024 [US2] Crear DTO de actualización en src/main/java/com/dsw01/practica02/dto/DepartamentoUpdateRequest.java"
Task: "T028 [US2] Sincronizar OpenAPI para actualización y errores asociados en specs/002-crud-departamentos/contracts/openapi.yaml"
```

## Parallel Example: User Story 3

```bash
Task: "T029 [US3] Implementar lógica de eliminación por clave en src/main/java/com/dsw01/practica02/service/DepartamentoService.java"
Task: "T031 [US3] Sincronizar OpenAPI para eliminación y respuestas asociadas en specs/002-crud-departamentos/contracts/openapi.yaml"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1.
2. Completar Phase 2 (bloqueante).
3. Completar Phase 3 (US1).
4. Validar flujo independiente de alta + consulta + listado paginado + reglas 401/404/400/200 vacío.

### Incremental Delivery

1. Base técnica (Phase 1 + 2).
2. Entregar US1 (MVP).
3. Extender con US2.
4. Extender con US3.
5. Cerrar con Phase 6.

### Parallel Team Strategy

- Dev A: dominio y servicios (`T001`, `T003`, `T015`, `T016`, `T017`, `T025`, `T029`).
- Dev B: controladores y validaciones (`T009`, `T010`, `T018`, `T019`, `T020`, `T026`, `T030`).
- Dev C: contratos y documentación (`T012`, `T022`, `T023`, `T028`, `T031`, `T032`, `T034`, `T035`).
