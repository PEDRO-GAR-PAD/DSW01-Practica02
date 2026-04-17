# Phase 0 Research - Frontend CRUD Empleados y Login

## Decision 1: Stack frontend
- **Decision**: Implementar la UI con Angular 19.2.22 usando componentes standalone y Reactive Forms.
- **Rationale**: Cumple la constitucion (Principio VII) y facilita formularios robustos para login y CRUD.
- **Alternatives considered**:
  - Angular en otra version: descartado por incumplir el estandar obligatorio.
  - Framework distinto (React/Vue): descartado por violar la constitucion.

## Decision 2: Estrategia de autenticacion en UI
- **Decision**: El login por principal (`username` o `email`) y password se implementa enviando credenciales por HTTP Basic y validando sesion con `GET /api/v1/auth/empleado/me`.
- **Rationale**: Mantiene el contrato de seguridad vigente (Principio II) y evita introducir otro mecanismo de auth fuera de alcance.
- **Alternatives considered**:
  - JWT/Bearer nuevo: descartado por cambiar el contrato base de APIs protegidas.
  - Sesion server-side nueva solo para frontend: descartado por ampliar alcance backend.

## Decision 3: Identificador de login
- **Decision**: El backend autentica por principal flexible (`username` o `email`) sin traduccion previa en frontend.
- **Rationale**: Reduce friccion de login, mantiene compatibilidad con usuarios existentes y evita capas de mapeo innecesarias (FR-015).
- **Alternatives considered**:
  - Forzar solo `email`: descartado por romper flujos existentes basados en `username`.
  - Traducir identificador antes de autenticar: descartado por complejidad extra y mayor superficie de fallo.

## Decision 4: Proteccion de rutas
- **Decision**: Definir guardas de ruta para separar vistas publicas (login) y privadas (dashboard + empleados), con control de rol para CRUD.
- **Rationale**: Alinea FR-005 y FR-006 con una navegacion predecible y segura.
- **Alternatives considered**:
  - Validar permisos solo en componentes: descartado por duplicacion y riesgo de inconsistencias.

## Decision 5: Persistencia de sesion
- **Decision**: Persistir estado de sesion en `sessionStorage` por pestaña.
- **Rationale**: Balancea seguridad y usabilidad para una SPA con HTTP Basic (FR-016).
- **Alternatives considered**:
  - Solo memoria: descartado por perder sesion en recarga.
  - `localStorage` persistente: descartado por mayor exposicion de credenciales.

## Decision 6: Fuente de autorizacion para UI admin
- **Decision**: Derivar permisos de CRUD desde roles/permisos retornados por `GET /api/v1/auth/empleado/me`.
- **Rationale**: Centraliza la decision de autorizacion y evita configuraciones estaticas frágiles (FR-018).
- **Alternatives considered**:
  - Lista estatica de admins en frontend: descartada por deriva operativa.
  - Habilitar CRUD a cualquier autenticado y confiar solo en `403`: descartado por mala UX y rutas inconsistentes.

## Decision 14: Menu por rol en dashboard
- **Decision**: Mostrar menu administrativo solo para `ROLE_ADMIN` y mostrar menu simple para `ROLE_EMPLEADO`.
- **Rationale**: Refuerza separacion de capacidades por rol y evita acciones no autorizadas visibles en UI.
- **Alternatives considered**:
  - Mostrar siempre opciones CRUD y depender de `403`: descartado por UX confusa.

## Decision 15: Toggle de visibilidad de password en login
- **Decision**: Incorporar control de ver/ocultar password en formulario de login.
- **Rationale**: Mejora usabilidad para captura de credenciales y reduce errores de tipeo.
- **Alternatives considered**:
  - Mantener campo oculto sin toggle: descartado por menor ergonomia en ingreso manual.

## Decision 7: Patron de estado para formularios
- **Decision**: Modelar estado de formulario con `idle/loading/success/error` y bloqueo de doble envio.
- **Rationale**: Cumple FR-011, reduce errores por clicks repetidos y mejora feedback de usuario.
- **Alternatives considered**:
  - Sin estado explicito de operacion: descartado por UX inconsistente y mayor riesgo de race conditions.

## Decision 8: Manejo de errores recuperables
- **Decision**: Conservar datos de formularios en errores recuperables (validacion/conflicto/fallo temporal) y mostrar mensajes accionables.
- **Rationale**: Cumple FR-012 y FR-013, minimiza recaptura y friccion operativa.
- **Alternatives considered**:
  - Limpiar formulario ante cualquier error: descartado por mala experiencia de uso.

## Decision 9: Semantica de eliminacion
- **Decision**: `DELETE /api/v1/empleados/{clave}` representa eliminacion definitiva (hard delete).
- **Rationale**: Alinea el frontend con contrato CRUD y reduce ambiguedad funcional (FR-017).
- **Alternatives considered**:
  - Soft delete con desactivacion: descartado por no corresponder a la decision de alcance actual.

## Decision 10: Manejo de `DELETE 404`
- **Decision**: Tratar `404` en eliminacion como no-op informativo, mostrar aviso no bloqueante y refrescar listado.
- **Rationale**: Mejora resiliencia en concurrencia y sincroniza estado visual sin bloquear operacion (FR-019).
- **Alternatives considered**:
  - Error bloqueante: descartado por friccion operativa.
  - Reintentos automaticos ciegos: descartado por baja utilidad en recurso ya inexistente.

## Decision 11: Contrato de integracion frontend-backend
- **Decision**: Consolidar en `contracts/frontend-api.yaml` los endpoints consumidos por login y CRUD de empleados.
- **Rationale**: Mejora trazabilidad en monorepo y reduce drift entre UI y API (Principio VI).
- **Alternatives considered**:
  - Documentacion informal en texto libre: descartada por ambiguedad y baja mantenibilidad.

## Decision 12: Estructura monorepo
- **Decision**: Mantener backend existente en raiz y agregar workspace `frontend/` para Angular.
- **Rationale**: Cumple Principio IV sin romper la estructura actual del proyecto.
- **Alternatives considered**:
  - Repositorio separado para frontend: descartado por violar monorepo obligatorio.
  - Mover backend completo a `apps/backend` en esta iteracion: descartado por riesgo y alcance excesivo.

## Decision 13: Validacion de rendimiento de UX
- **Decision**: Medir tiempo de login y refresco visible de listado con validacion manual guiada en quickstart.
- **Rationale**: Da evidencia directa para SC-001 y SC-005 en el contexto actual del proyecto.
- **Alternatives considered**:
  - Exigir suite E2E completa en esta fase: descartado por no estar requerido en la especificacion.

## Clarification Status

No quedan items `NEEDS CLARIFICATION` para continuar a Fase 1.

## Implementation Notes (2026-03-12)

- Se implemento workspace Angular 19.2.22 en `frontend/` con arquitectura standalone.
- Se incorporaron interceptores HTTP para:
  - Adjuntar `Authorization: Basic ...` a requests protegidos.
  - Limpiar sesion y redirigir a login ante `401`.
- Se implemento estado de formularios recuperables para altas/ediciones con reintento.
- Se trato `DELETE 404` como no-op informativo con refresco de listado.
- Para compatibilidad con backend actual, `roles` en `/auth/empleado/me` se maneja como opcional en frontend y contrato.
