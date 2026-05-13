# Tasks: Frontend CRUD Empleados y Login

**Input**: Design documents from `/specs/004-frontend-empleados-login/`
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/`, `quickstart.md`

**Tests**: No se agregan tareas de pruebas automatizadas porque la especificacion no exige enfoque TDD ni suite obligatoria.

**Organization**: Tareas agrupadas por historia de usuario para implementacion y validacion independiente.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicializar workspace frontend y configuracion base para integrar login + CRUD con backend.

- [X] T001 Crear workspace Angular 19.2.22 y estructura inicial en `frontend/package.json`
- [X] T002 Configurar build y serve del frontend en `frontend/angular.json`
- [X] T003 [P] Configurar URL base de API para desarrollo en `frontend/src/environments/environment.ts`
- [X] T004 [P] Configurar URL base de API para produccion en `frontend/src/environments/environment.prod.ts`
- [X] T005 [P] Documentar arranque frontend dentro de monorepo en `docs/frontend/README.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura frontend bloqueante para autenticacion, rutas protegidas, autorizacion y manejo global de errores.

**CRITICAL**: Ninguna historia de usuario inicia antes de cerrar esta fase.

- [X] T006 Definir rutas raiz y composicion base de la app en `frontend/src/app/app.routes.ts`
- [X] T007 [P] Crear tipos compartidos de API (auth/empleados/error) en `frontend/src/app/core/models/api.types.ts`
- [X] T008 [P] Implementar almacenamiento de sesion por pestaña (`sessionStorage`) en `frontend/src/app/core/auth/session-store.service.ts`
- [X] T009 [P] Implementar cliente de autenticacion para `GET /api/v1/auth/empleado/me` en `frontend/src/app/core/auth/auth-api.service.ts`
- [X] T010 [P] Implementar interceptor para adjuntar encabezado Basic a requests protegidos en `frontend/src/app/core/http/auth-header.interceptor.ts`
- [X] T011 [P] Implementar interceptor de falla `401` para limpieza de sesion y redireccion en `frontend/src/app/core/http/auth-failure.interceptor.ts`
- [X] T012 [P] Implementar servicio global de mapeo de errores API a feedback de UI en `frontend/src/app/core/http/api-error.service.ts`
- [X] T013 Configurar providers globales e interceptores en `frontend/src/app/app.config.ts`
- [X] T014 [P] Implementar guardas de autenticacion y permisos admin desde `/auth/empleado/me` en `frontend/src/app/core/auth/auth.guards.ts`
- [X] T015 [P] Crear modelo reusable de feedback de operacion en `frontend/src/app/shared/models/operation-feedback.model.ts`
- [X] T016 [P] Sincronizar baseline del contrato frontend consumido en `specs/004-frontend-empleados-login/contracts/frontend-api.yaml`

**Checkpoint**: Fundacion completa, historias habilitadas para implementacion.

---

## Phase 3: User Story 1 - Iniciar Sesion Con Usuario O Email (Priority: P1) MVP

**Goal**: Permitir login por principal (`username` o `email`) y `password`, mantener sesion por pestaña y habilitar logout con proteccion de rutas privadas.

**Independent Test**: Desde login, autenticar con credenciales validas y entrar a vista privada; recargar misma pestaña manteniendo sesion; cerrar sesion y verificar bloqueo de rutas privadas.

### Implementation for User Story 1

- [X] T017 [P] [US1] Crear modelo de estado del formulario de login en `frontend/src/app/features/auth/models/login-form-state.model.ts`
- [X] T018 [US1] Implementar pagina de login con Reactive Forms (principal + password) en `frontend/src/app/features/auth/pages/login-page/login-page.component.ts`
- [X] T019 [US1] Implementar flujo login/logout con bootstrap de sesion por pestaña en `frontend/src/app/features/auth/services/auth-session.service.ts`
- [X] T020 [US1] Implementar vista privada inicial post-login en `frontend/src/app/features/dashboard/pages/dashboard-home/dashboard-home.component.ts`
- [X] T021 [US1] Definir rutas de autenticacion y redirecciones en `frontend/src/app/features/auth/auth.routes.ts`
- [X] T022 [US1] Implementar layout privado con accion de logout en `frontend/src/app/layouts/private-shell/private-shell.component.ts`
- [X] T023 [P] [US1] Sincronizar contrato de login por principal flexible y perfil autenticado con roles en `specs/004-frontend-empleados-login/contracts/frontend-api.yaml`
- [X] T024 [US1] Actualizar validacion manual de login/logout y persistencia por pestaña en `specs/004-frontend-empleados-login/quickstart.md`

**Checkpoint**: US1 queda funcional e independientemente demostrable.

---

## Phase 4: User Story 2 - Gestionar Empleados Desde Frontend (Priority: P2)

**Goal**: Permitir alta, listado, edicion y eliminacion definitiva de empleados para usuarios admin.

**Independent Test**: Iniciar sesion como admin, crear empleado, editarlo, eliminarlo definitivamente y verificar que deja de aparecer en listado.

### Implementation for User Story 2

- [X] T025 [P] [US2] Definir modelos de vista de empleado y paginacion en `frontend/src/app/features/empleados/models/empleado.models.ts`
- [X] T026 [P] [US2] Implementar cliente API de empleados (GET/POST/PUT/DELETE) en `frontend/src/app/features/empleados/services/empleados-api.service.ts`
- [X] T027 [P] [US2] Implementar formulario reusable de empleado con validaciones en `frontend/src/app/features/empleados/components/empleado-form/empleado-form.component.ts`
- [X] T028 [US2] Implementar pagina de listado con paginacion y acciones CRUD en `frontend/src/app/features/empleados/pages/empleados-list/empleados-list.component.ts`
- [X] T029 [US2] Implementar flujo de creacion de empleado en `frontend/src/app/features/empleados/pages/empleado-create/empleado-create.component.ts`
- [X] T030 [US2] Implementar flujo de edicion de empleado con `version` en `frontend/src/app/features/empleados/pages/empleado-edit/empleado-edit.component.ts`
- [X] T031 [US2] Implementar dialogo de confirmacion para eliminacion definitiva en `frontend/src/app/features/empleados/components/empleado-delete-dialog/empleado-delete-dialog.component.ts`
- [X] T032 [US2] Definir rutas protegidas de empleados solo para admin en `frontend/src/app/features/empleados/empleados.routes.ts`
- [X] T033 [US2] Integrar rutas de empleados al arbol principal en `frontend/src/app/app.routes.ts`
- [X] T034 [P] [US2] Sincronizar contrato CRUD con semantica de hard delete en `specs/004-frontend-empleados-login/contracts/frontend-api.yaml`
- [X] T035 [US2] Actualizar quickstart para escenarios CRUD manuales de admin en `specs/004-frontend-empleados-login/quickstart.md`

**Checkpoint**: US2 queda funcional e independientemente demostrable.

---

## Phase 5: User Story 3 - Operar Con Seguridad y Recuperacion De Errores (Priority: P3)

**Goal**: Garantizar manejo consistente de expiracion de sesion, conflictos de concurrencia y fallos recuperables preservando datos.

**Independent Test**: Forzar errores `401`, conflicto de actualizacion y fallo temporal; verificar redireccion a login, mensajes accionables, preservacion de formulario y tratamiento no bloqueante de `DELETE 404`.

### Implementation for User Story 3

- [X] T036 [P] [US3] Crear utilidad reusable para estado de formulario recuperable en `frontend/src/app/shared/forms/recoverable-form-state.ts`
- [X] T037 [US3] Implementar manejo de expiracion de sesion (`401`) con redireccion a login en `frontend/src/app/core/http/auth-failure.interceptor.ts`
- [X] T038 [US3] Implementar manejo de conflicto de concurrencia (`409`) en `frontend/src/app/core/http/conflict-handler.service.ts`
- [X] T039 [US3] Preservar datos del formulario de alta ante errores recuperables en `frontend/src/app/features/empleados/pages/empleado-create/empleado-create.component.ts`
- [X] T040 [US3] Preservar datos del formulario de edicion ante errores recuperables en `frontend/src/app/features/empleados/pages/empleado-edit/empleado-edit.component.ts`
- [X] T041 [US3] Agregar accion de reintento para fallos temporales en listado en `frontend/src/app/features/empleados/pages/empleados-list/empleados-list.component.ts`
- [X] T042 [US3] Implementar tratamiento de `DELETE 404` como no-op informativo con refresco de listado en `frontend/src/app/features/empleados/pages/empleados-list/empleados-list.component.ts`
- [X] T043 [P] [US3] Sincronizar respuestas de error y comportamiento `404` en `specs/004-frontend-empleados-login/contracts/frontend-api.yaml`
- [X] T044 [US3] Actualizar quickstart para recuperacion de errores, concurrencia y delete no-op en `specs/004-frontend-empleados-login/quickstart.md`

**Checkpoint**: US3 queda funcional e independientemente demostrable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre documental, trazabilidad final y validacion integral del feature.

- [X] T045 [P] Alinear modelo de datos final con estados de UI implementados en `specs/004-frontend-empleados-login/data-model.md`
- [X] T046 [P] Alinear decisiones finales de arquitectura y errores en `specs/004-frontend-empleados-login/research.md`
- [X] T047 [P] Alinear plan final con estructura real de frontend y constraints de auth en `specs/004-frontend-empleados-login/plan.md`
- [X] T048 Ejecutar validacion manual end-to-end y registrar evidencia en `specs/004-frontend-empleados-login/quickstart.md`
- [X] T049 Actualizar matriz FR/SC con evidencia de US1-US3 en `specs/004-frontend-empleados-login/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Sin dependencias.
- **Phase 2 (Foundational)**: Depende de Phase 1 y bloquea todas las historias.
- **Phase 3 (US1)**: Depende de Phase 2.
- **Phase 4 (US2)**: Depende de US1 para operar CRUD sobre sesion/admin reales.
- **Phase 5 (US3)**: Depende de US1 y US2 para validar errores sobre flujos reales de login y CRUD.
- **Phase 6 (Polish)**: Depende del cierre de US1, US2 y US3.

### User Story Dependency Graph

- **US1 (P1)**: Base de autenticacion y sesiones por pestaña.
- **US2 (P2)**: CRUD de empleados con eliminacion definitiva y permisos admin.
- **US3 (P3)**: Resiliencia ante errores y no-op informativo en `DELETE 404`.

Orden sugerido de completitud:
`US1 -> US2 -> US3`

### Parallel Opportunities

- Setup en paralelo: `T003`, `T004`, `T005`.
- Foundational en paralelo: `T007`, `T008`, `T009`, `T010`, `T011`, `T012`, `T014`, `T015`, `T016`.
- US1 en paralelo: `T017`, `T023`.
- US2 en paralelo: `T025`, `T026`, `T027`, `T034`.
- US3 en paralelo: `T036`, `T043`.
- Polish en paralelo: `T045`, `T046`, `T047`.

---

## Parallel Example: User Story 1

```bash
Task: "T017 [US1] Crear modelo de estado del formulario de login en frontend/src/app/features/auth/models/login-form-state.model.ts"
Task: "T023 [US1] Sincronizar contrato de login por email y perfil autenticado con roles en specs/004-frontend-empleados-login/contracts/frontend-api.yaml"
```

## Parallel Example: User Story 2

```bash
Task: "T025 [US2] Definir modelos de vista de empleado y paginacion en frontend/src/app/features/empleados/models/empleado.models.ts"
Task: "T026 [US2] Implementar cliente API de empleados en frontend/src/app/features/empleados/services/empleados-api.service.ts"
Task: "T034 [US2] Sincronizar contrato CRUD con semantica de hard delete en specs/004-frontend-empleados-login/contracts/frontend-api.yaml"
```

## Parallel Example: User Story 3

```bash
Task: "T036 [US3] Crear utilidad reusable para estado de formulario recuperable en frontend/src/app/shared/forms/recoverable-form-state.ts"
Task: "T043 [US3] Sincronizar respuestas de error y comportamiento 404 en specs/004-frontend-empleados-login/contracts/frontend-api.yaml"
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
2. Entregar US1 (login por principal flexible, sessionStorage por pestaña, logout).
3. Entregar US2 (CRUD admin con hard delete).
4. Entregar US3 (manejo de errores y delete no-op).
5. Cerrar con Polish y evidencia final.

### Parallel Team Strategy

1. Equipo completo en Setup + Foundational.
2. Luego distribucion sugerida:
- Dev A: US1 (`T017`-`T024`).
- Dev B: US2 (`T025`-`T035`).
- Dev C: US3 (`T036`-`T044`) y soporte Polish (`T045`-`T049`).
