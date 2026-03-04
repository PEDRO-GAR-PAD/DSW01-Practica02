# Tasks: CRUD de Empleados

**Input**: Design documents from `/specs/001-crud-empleados/`  
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/openapi.yaml`, `quickstart.md`

**Tests**: No se generaron tareas de tests porque la especificación no exige enfoque TDD ni pruebas obligatorias en esta fase.

**Organization**: Las tareas están agrupadas por historia de usuario para implementación y validación independiente.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicialización del proyecto y estructura base del backend.

- [X] T001 Inicializar proyecto Spring Boot 3 con Java 17 en pom.xml
- [X] T002 Crear estructura de paquetes base en src/main/java/com/dsw01/practica02/
- [X] T003 [P] Configurar plugin de compilación Java 17 en pom.xml

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura obligatoria antes de implementar cualquier historia.

**⚠️ CRITICAL**: Ninguna historia puede comenzar hasta cerrar esta fase.

- [X] T004 Configurar datasource PostgreSQL y propiedades base en src/main/resources/application.properties
- [X] T005 [P] Crear entidad JPA Empleado en src/main/java/com/dsw01/practica02/domain/Empleado.java
- [X] T006 [P] Crear repositorio de empleados en src/main/java/com/dsw01/practica02/repository/EmpleadoRepository.java
- [X] T007 [P] Implementar estrategia de generación de clave E-0001 en src/main/java/com/dsw01/practica02/service/ClaveEmpleadoGenerator.java
- [X] T008 [P] Configurar seguridad HTTP Basic (admin/admin123) en src/main/java/com/dsw01/practica02/config/SecurityConfig.java
- [X] T009 [P] Configurar OpenAPI/Swagger con esquema basicAuth en src/main/java/com/dsw01/practica02/config/OpenApiConfig.java
- [X] T010 Crear docker-compose para PostgreSQL en docker/docker-compose.yml
- [X] T011 Agregar excepción de dominio y manejador global en src/main/java/com/dsw01/practica02/config/GlobalExceptionHandler.java

**Checkpoint**: Base técnica lista para implementar historias en paralelo.

---

## Phase 3: User Story 1 - Registrar y consultar empleados (Priority: P1) 🎯 MVP

**Goal**: Permitir alta de empleados y consultas por clave/listado paginado (5 registros).

**Independent Test**: Crear un empleado, consultar por clave y listar página 0 verificando clave `E-0001` y tamaño máximo 5.

### Implementation for User Story 1

- [X] T012 [P] [US1] Crear DTOs de creación y respuesta en src/main/java/com/dsw01/practica02/dto/EmpleadoCreateRequest.java
- [X] T013 [P] [US1] Crear DTO de listado paginado en src/main/java/com/dsw01/practica02/dto/EmpleadoPageResponse.java
- [X] T014 [US1] Implementar servicio de creación y consultas en src/main/java/com/dsw01/practica02/service/EmpleadoService.java
- [X] T015 [US1] Implementar endpoint POST /api/empleados en src/main/java/com/dsw01/practica02/controller/EmpleadoController.java
- [X] T016 [US1] Implementar endpoint GET /api/empleados/{clave} en src/main/java/com/dsw01/practica02/controller/EmpleadoController.java
- [X] T017 [US1] Implementar endpoint GET /api/empleados?page=0 con tamaño fijo 5 en src/main/java/com/dsw01/practica02/controller/EmpleadoController.java
- [X] T018 [US1] Aplicar validaciones de campos <=100 y obligatorios en src/main/java/com/dsw01/practica02/dto/EmpleadoCreateRequest.java
- [X] T019 [US1] Sincronizar contrato OpenAPI de alta y consultas en specs/001-crud-empleados/contracts/openapi.yaml

**Checkpoint**: US1 funcional y validable de forma independiente.

---

## Phase 4: User Story 2 - Actualizar datos de empleados (Priority: P2)

**Goal**: Permitir actualización de nombre, dirección y teléfono manteniendo la misma clave.

**Independent Test**: Actualizar un empleado existente y verificar en consulta posterior que conserva clave y refleja cambios.

### Implementation for User Story 2

- [X] T020 [P] [US2] Crear DTO de actualización en src/main/java/com/dsw01/practica02/dto/EmpleadoUpdateRequest.java
- [X] T021 [US2] Implementar lógica de actualización en src/main/java/com/dsw01/practica02/service/EmpleadoService.java
- [X] T022 [US2] Implementar endpoint PUT /api/empleados/{clave} en src/main/java/com/dsw01/practica02/controller/EmpleadoController.java
- [X] T023 [US2] Sincronizar contrato OpenAPI para actualización en specs/001-crud-empleados/contracts/openapi.yaml

**Checkpoint**: US2 funcional sin depender de US3.

---

## Phase 5: User Story 3 - Eliminar empleados (Priority: P3)

**Goal**: Permitir eliminación de empleados por clave con respuesta 204.

**Independent Test**: Eliminar un empleado y comprobar que las consultas posteriores devuelven no encontrado.

### Implementation for User Story 3

- [X] T024 [US3] Implementar lógica de eliminación en src/main/java/com/dsw01/practica02/service/EmpleadoService.java
- [X] T025 [US3] Implementar endpoint DELETE /api/empleados/{clave} en src/main/java/com/dsw01/practica02/controller/EmpleadoController.java
- [X] T026 [US3] Sincronizar contrato OpenAPI para eliminación en specs/001-crud-empleados/contracts/openapi.yaml

**Checkpoint**: Las tres historias CRUD quedan completas e independientes.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre de documentación y endurecimiento transversal.

- [X] T027 [P] Actualizar guía de uso y ejemplos en specs/001-crud-empleados/quickstart.md
- [X] T028 [P] Documentar variables de entorno seguras para credenciales en docs/api/security.md
- [X] T029 Verificar consistencia final de application.properties con Docker y PostgreSQL en src/main/resources/application.properties
- [X] T030 Verificar que Swagger expone todos los endpoints CRUD en specs/001-crud-empleados/contracts/openapi.yaml
- [X] T031 Ejecutar validación manual del quickstart contra implementación en specs/001-crud-empleados/quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: inicia inmediatamente.
- **Phase 2 (Foundational)**: depende de Phase 1 y bloquea todas las historias.
- **Phase 3-5 (User Stories)**: dependen de Phase 2; pueden ejecutarse en paralelo por equipo, aunque se prioriza P1 → P2 → P3.
- **Phase 6 (Polish)**: depende de las historias completadas.

### User Story Dependency Graph

- **US1 (P1)** → habilita MVP.
- **US2 (P2)** → depende funcionalmente de la existencia de empleados creados en US1.
- **US3 (P3)** → depende funcionalmente de la existencia de empleados creados en US1.

Representación:
`US1 -> US2`
`US1 -> US3`

### Within Each User Story

- DTOs/modelos antes de servicio.
- Servicio antes de controlador.
- Controlador antes de sincronización final de contrato OpenAPI.

### Parallel Opportunities

- Setup: `T003` puede correr en paralelo con `T001-T002`.
- Foundational: `T005`, `T006`, `T007`, `T008`, `T009`, `T010` pueden avanzar en paralelo tras `T004`.
- US1: `T012` y `T013` en paralelo.
- US2: `T020` en paralelo con revisiones no bloqueantes de contrato antes de `T023`.
- Polish: `T027` y `T028` en paralelo.

---

## Parallel Example: User Story 1

```bash
Task: "T012 [US1] Crear DTOs de creación y respuesta en src/main/java/com/dsw01/practica02/dto/EmpleadoCreateRequest.java"
Task: "T013 [US1] Crear DTO de listado paginado en src/main/java/com/dsw01/practica02/dto/EmpleadoPageResponse.java"
```

## Parallel Example: User Story 2

```bash
Task: "T020 [US2] Crear DTO de actualización en src/main/java/com/dsw01/practica02/dto/EmpleadoUpdateRequest.java"
Task: "T023 [US2] Sincronizar contrato OpenAPI para actualización en specs/001-crud-empleados/contracts/openapi.yaml"
```

## Parallel Example: User Story 3

```bash
Task: "T024 [US3] Implementar lógica de eliminación en src/main/java/com/dsw01/practica02/service/EmpleadoService.java"
Task: "T026 [US3] Sincronizar contrato OpenAPI para eliminación en specs/001-crud-empleados/contracts/openapi.yaml"
```

---

## Implementation Strategy

### MVP First (US1 only)

1. Completar Phase 1.
2. Completar Phase 2 (bloqueante).
3. Completar Phase 3 (US1).
4. Validar flujo independiente de alta + consulta + listado paginado.

### Incremental Delivery

1. Base técnica (Phase 1 + 2).
2. Entregar US1 (MVP).
3. Extender con US2.
4. Extender con US3.
5. Cerrar con Phase 6.

### Parallel Team Strategy

- Dev A: infraestructura y seguridad (`T004`, `T008`, `T011`).
- Dev B: modelo/repositorio/generador de clave (`T005`, `T006`, `T007`).
- Dev C: Docker y OpenAPI (`T009`, `T010`, `T019`, `T023`, `T026`).
