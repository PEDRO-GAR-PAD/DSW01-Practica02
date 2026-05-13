# Implementation Plan: Autenticacion Por Empleado

**Branch**: `003-add-empleado-auth` | **Date**: 2026-03-11 | **Spec**: `specs/003-add-empleado-auth/spec.md`
**Input**: Feature specification from `/specs/003-add-empleado-auth/spec.md`

## Summary

Se mantiene el modelo de autenticacion por empleado y se consolida el alcance del campo
`email` en credenciales con persistencia `VARCHAR(255)`, validacion de formato y
unicidad global (incluyendo cuentas inactivas), agregando ademas autenticacion por
`username` o `email`, cuenta bootstrap `master` con control administrativo global,
respuesta de roles en `/api/v1/auth/empleado/me` y configuracion CORS para Angular
local.

## Technical Context

**Language/Version**: Java 17  
**Frontend Framework**: N/A para esta iteracion (backend-only); cualquier UI futura debe usar Angular 19.2.22  
**Primary Dependencies**: Spring Boot 3, Spring Security (HTTP Basic), Spring Data JPA, Bean Validation, springdoc-openapi, PostgreSQL driver  
**Storage**: PostgreSQL  
**Testing**: JUnit 5 + Spring Boot Test + validacion manual por quickstart/curl  
**Target Platform**: Linux local y Docker  
**Project Type**: web-service backend  
**Performance Goals**: >=95% de autenticaciones exitosas en <3s (SC-003)  
**Constraints**: `401` uniforme en auth fallida, lockout 5/15m por 30m, `email` en `varchar` con formato valido y unicidad global, concurrencia optimista por `version`, control CRUD por rol `ADMIN` (master) y CORS configurable para entorno local  
**Scale/Scope**: API interna de empleados/credenciales para miles de cuentas activas

## Constitution Check (Pre-Design)

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Principle I: Backend stack preserved (Spring Boot 3 + Java 17)
- [x] Principle II: HTTP Basic contract preserved for protected APIs
- [x] Principle III: PostgreSQL persistence rules satisfied
- [x] Principle IV: Monorepo architecture preserved (no split de repositorios; trazabilidad en un unico repo)
- [x] Principle V: Docker delivery path updated as needed (sin cambios de runtime para esta iteracion)
- [x] Principle VI: OpenAPI/Swagger docs synchronized with API changes
- [x] Principle VII: Frontend scope uses Angular 19.2.22 (N/A en este incremento backend)

## Phase 0: Research Output

`specs/003-add-empleado-auth/research.md` resuelve decisiones de:

- Persistencia y validacion de `email` en credenciales.
- Regla de unicidad global de `email` incluyendo cuentas inactivas.
- Estrategia de lockout, auditoria y administracion de credenciales.

No quedan items `NEEDS CLARIFICATION` abiertos.

## Phase 1: Design & Contracts Output

`specs/003-add-empleado-auth/data-model.md` sincronizado con:

- `email` en `CredencialEmpleado` como `VARCHAR(255)` con unicidad global.
- Reglas de validacion para formato y duplicidad de `email`.

`specs/003-add-empleado-auth/contracts/openapi.yaml` sincronizado con:

- `email` requerido en create y opcional en update.
- Descripcion de conflicto `409` para duplicidad de username/email.
- `roles` en respuesta de `/auth/empleado/me` para decisiones de UI por permisos.

`specs/003-add-empleado-auth/quickstart.md` sincronizado con:

- Flujo de prueba para duplicidad global de `email` (`409 Conflict`).
- Matriz de cobertura extendida con FR-019.

## Project Structure

### Documentation (this feature)

```text
specs/003-add-empleado-auth/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
└── main/
    ├── java/
    └── resources/

docker/
Dockerfile
pom.xml
specs/
└── 003-add-empleado-auth/
```

**Structure Decision**: Se mantiene la estructura actual del repositorio como fuente
unica de verdad para backend y especificaciones; este incremento no altera la topologia,
solo sincroniza diseno/contrato para la regla de unicidad global de `email`.

## Constitution Check (Post-Design)

- [x] Principle I: Sin cambios de stack (Java 17 + Spring Boot 3)
- [x] Principle II: Contrato HTTP Basic intacto
- [x] Principle III: Persistencia PostgreSQL definida (`email` varchar + unicidad global)
- [x] Principle IV: Cambios trazables en el mismo repositorio
- [x] Principle V: Sin cambios adicionales requeridos en Docker/runtime
- [x] Principle VI: Contrato OpenAPI actualizado y alineado
- [x] Principle VII: Sin alcance frontend en esta iteracion (N/A documentado)

## Complexity Tracking

No se identifican violaciones constitucionales que requieran excepcion.
