# Implementation Plan: CRUD de Departamentos

**Branch**: `002-crud-departamentos` | **Date**: 2026-03-09 | **Spec**: `/specs/002-crud-departamentos/spec.md`
**Input**: Feature specification from `/specs/002-crud-departamentos/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar CRUD de departamentos con clave autogenerada `D-0001`, validaciones de negocio,
autenticación HTTP Basic en todos los endpoints, paginación obligatoria (5 por página) y
contrato de error JSON estándar, exponiendo únicamente rutas versionadas `/api/v1`.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3 (Web, Data JPA, Security, Validation), PostgreSQL Driver, Springdoc OpenAPI  
**Storage**: PostgreSQL  
**Testing**: JUnit 5 + Spring Boot Test + MockMvc  
**Target Platform**: Linux server (Docker)  
**Project Type**: web-service backend  
**Performance Goals**: Al menos 95% de operaciones CRUD en menos de 2 segundos (SC-003)  
**Constraints**: Rutas solo `/api/v1/...`; auth básica obligatoria; clave `D-0001` autogenerada y límite `D-9999`; paginación fija 5; `page` inválido -> 400, fuera de rango -> 200 vacío; errores estándar JSON  
**Scale/Scope**: 1 recurso principal (`departamentos`), 5 endpoints CRUD versionados

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Principle I: Baseline confirmado en Spring Boot 3 + Java 17.
- [x] Principle II: CRUD de departamentos protegido con HTTP Basic y credenciales externalizables.
- [x] Principle III: Persistencia en PostgreSQL para datos de negocio y generación de clave.
- [x] Principle IV: Ejecución reproducible vía Docker/Compose mantenida.
- [x] Principle V: OpenAPI/Swagger actualizado para todos los endpoints y errores relevantes.

**Post-Design Re-check**: PASS. Los artefactos de diseño mantienen cumplimiento con seguridad,
persistencia, dockerización y documentación API.

## Project Structure

### Documentation (this feature)

```text
specs/002-crud-departamentos/
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
├── main/
│   ├── java/.../
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── domain/
│   │   ├── dto/
│   │   ├── exception/
│   │   └── config/
│   └── resources/
│       └── application.properties
└── test/
    └── java/.../

docker/
└── docker-compose.yml
```

**Structure Decision**: Reutilizar arquitectura monolítica Spring existente por capas
(domain/repository/service/controller/config), agregando contrato OpenAPI en `specs/.../contracts`.

## Complexity Tracking

No constitution violations identified; complexity exceptions are not required.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |
