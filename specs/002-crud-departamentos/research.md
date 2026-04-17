# Phase 0 Research - CRUD de Departamentos

## Decision 1: Rutas versionadas exclusivas
- **Decision**: Exponer solo `/api/v1/departamentos` y `/api/v1/departamentos/{clave}`; rutas no versionadas responden `404`.
- **Rationale**: Mantiene consistencia con política API-first/versioning y reduce doble mantenimiento.
- **Alternatives considered**:
  - Mantener rutas con y sin versión en paralelo: descartado por ambigüedad contractual.
  - Migrar a rutas sin versión: descartado por romper estándar actual.

## Decision 2: Seguridad uniforme
- **Decision**: Requerir HTTP Basic en todos los endpoints CRUD de departamentos.
- **Rationale**: Cumple constitución y evita diferencias de acceso entre operaciones.
- **Alternatives considered**:
  - Hacer listado público: descartado por inconsistencia de seguridad.

## Decision 3: Clave de negocio
- **Decision**: Generar `clave` automáticamente como `D-0001` con límite en `D-9999`; superar límite devuelve `409`.
- **Rationale**: Respeta formato funcional y evita claves inválidas.
- **Alternatives considered**:
  - Expandir a más de 4 dígitos: descartado por violar especificación.

## Decision 4: Paginación de listado
- **Decision**: Listado paginado con tamaño fijo 5 y parámetro `page`; `page` inválido (`<0`/no numérico) -> `400`, fuera de rango -> `200` con `content` vacío.
- **Rationale**: Separa claramente error de entrada vs resultado válido sin datos.
- **Alternatives considered**:
  - Fuera de rango como `404`: descartado porque la colección existe.

## Decision 5: Contrato de errores
- **Decision**: Estructura estándar JSON para errores: `timestamp`, `status`, `error`, `message`, `fieldErrors` (solo validación).
- **Rationale**: Facilita consumo por clientes y pruebas consistentes.
- **Alternatives considered**:
  - Errores por defecto de framework sin contrato: descartado por inconsistencia.

## Decision 6: Validación de payload
- **Decision**: `nombre` obligatorio y max 100; `clave` no aceptada en alta.
- **Rationale**: Protege calidad de datos y evita manipulaciones de identificador.
- **Alternatives considered**:
  - Permitir `clave` manual: descartado por riesgo de colisión.

## Decision 7: Documentación y persistencia
- **Decision**: Persistir en PostgreSQL y reflejar contrato completo en OpenAPI 3.0.
- **Rationale**: Cumple constitución en durabilidad y trazabilidad de API.
- **Alternatives considered**:
  - Persistencia en memoria: descartada por no durabilidad.