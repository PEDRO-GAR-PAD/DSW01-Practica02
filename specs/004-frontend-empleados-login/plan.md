# Implementation Plan: Frontend CRUD Empleados y Login

**Branch**: `004-frontend-empleados-login` | **Date**: 2026-03-12 | **Spec**: `specs/004-frontend-empleados-login/spec.md`
**Input**: Feature specification from `/specs/004-frontend-empleados-login/spec.md`

## Summary

Se implementa una SPA Angular 19.2.22 con login por principal (`username` o `email`) y `password` usando HTTP Basic
contra backend existente, persistiendo sesion por pestaña en `sessionStorage`, y con
CRUD administrativo de empleados con eliminacion definitiva. El diseno incorpora control
de permisos desde `GET /api/v1/auth/empleado/me`, rutas protegidas, visibilidad de menu
segun rol (`ADMIN` vs `EMPLEADO`), toggle ver/ocultar password en login y manejo robusto
de errores recuperables, incluyendo `DELETE 404` como no-op informativo con refresco.

## Technical Context

**Language/Version**: TypeScript 5.x (frontend) + Java 17 (backend existente)  
**Frontend Framework**: Angular 19.2.22  
**Primary Dependencies**: Angular Router, Angular HttpClient, Reactive Forms, RxJS  
**Storage**: `sessionStorage` por pestaña para estado de sesion; datos de negocio en PostgreSQL via backend  
**Testing**: Angular unit tests + validacion manual guiada en `quickstart.md`  
**Target Platform**: Navegadores modernos desktop/mobile  
**Project Type**: Monorepo backend + frontend (web-service + SPA)  
**Performance Goals**: Cumplir SC-001 y SC-005 (>=95% login <60s, refresco visible <3s)  
**Constraints**: HTTP Basic obligatorio, login por principal flexible (`username` o `email`) sin traduccion previa en frontend, permisos UI desde `/api/v1/auth/empleado/me`, `DELETE` hard delete y `404` no-op informativo  
**Scale/Scope**: UI administrativa interna para autenticacion y gestion de empleados

## Constitution Check (Pre-Design)

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Principle I: Backend stack preserved (Spring Boot 3 + Java 17)
- [x] Principle II: HTTP Basic contract preserved for protected APIs
- [x] Principle III: PostgreSQL persistence rules satisfied
- [x] Principle IV: Monorepo architecture preserved (backend + frontend workspaces)
- [x] Principle V: Docker delivery path updated as needed
- [x] Principle VI: OpenAPI/Swagger docs synchronized with API changes
- [x] Principle VII: Frontend scope uses Angular 19.2.22

## Phase 0: Research Output

`specs/004-frontend-empleados-login/research.md` consolida decisiones de:

- Arquitectura Angular 19.2.22 (standalone + formularios reactivos).
- Login directo por principal (`username` o `email`) y password usando HTTP Basic.
- Persistencia de sesion por pestaña en `sessionStorage`.
- Autorizacion de UI derivada de roles/permisos retornados por `/api/v1/auth/empleado/me`.
- Semantica de eliminacion definitiva y tratamiento `DELETE 404` como no-op informativo.

No quedan items `NEEDS CLARIFICATION` abiertos.

## Phase 1: Design & Contracts Output

`specs/004-frontend-empleados-login/data-model.md` define estados de login/sesion,
modelos de empleado y feedback operacional de errores recuperables.

`specs/004-frontend-empleados-login/contracts/frontend-api.yaml` define contrato de
consumo frontend para auth + CRUD de empleados, incluyendo reglas de autorizacion y
comportamiento esperado ante `404` en delete.

`specs/004-frontend-empleados-login/quickstart.md` documenta validacion manual para
US1-US3 y evidencia de SC-001..SC-005.

## Project Structure

### Documentation (this feature)

```text
specs/004-frontend-empleados-login/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── frontend-api.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
└── main/
    ├── java/
    └── resources/

frontend/
├── angular.json
├── package.json
└── src/
    ├── environments/
    └── app/
        ├── core/
        │   ├── auth/
        │   ├── http/
        │   └── models/
        ├── shared/
        │   ├── forms/
        │   └── models/
        ├── features/
        │   ├── auth/
        │   ├── dashboard/
        │   └── empleados/
        └── layouts/

docker/
Dockerfile
specs/
└── 004-frontend-empleados-login/
```

**Structure Decision**: Se conserva backend en estructura actual y se agrega
workspace `frontend/` Angular 19.2.22 en el mismo repositorio para cumplir
monorepo y trazabilidad de contratos UI/backend.

## Delivery Status (2026-03-12)

- Setup: completado (`frontend/` generado con Angular CLI 19.2.22).
- Foundation: completado (rutas base, guards, interceptores, servicios auth/error).
- US1: completado (login email/password, sesion por pestaña, logout, dashboard privado).
- US2: completado (listado, alta, edicion, eliminacion definitiva de empleados).
- US3: completado (manejo `401`, `409`, reintento y `DELETE 404` no-op informativo).
- Verificacion tecnica: `npm run build` exitoso en workspace frontend.

## Constitution Check (Post-Design)

- [x] Principle I: Sin cambios en stack backend (Spring Boot 3 + Java 17)
- [x] Principle II: Frontend consume APIs protegidas con HTTP Basic
- [x] Principle III: Persistencia de negocio se mantiene en PostgreSQL
- [x] Principle IV: Diseno mantiene monorepo backend + frontend
- [x] Principle V: Quickstart contempla ejecucion Docker del backend
- [x] Principle VI: Contrato frontend sincronizado en `contracts/frontend-api.yaml`
- [x] Principle VII: Frontend fijado a Angular 19.2.22

## Complexity Tracking

No se identifican violaciones constitucionales que requieran excepcion.
