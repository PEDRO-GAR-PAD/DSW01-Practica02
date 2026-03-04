# Phase 0 Research - CRUD de Empleados

## Decision 1: Tipo de dato para campos de longitud máxima 100
- **Decision**: Usar `VARCHAR(100)` para `nombre`, `direccion` y `telefono`.
- **Rationale**: Mantiene límite explícito de 100 caracteres sin relleno fijo, optimiza almacenamiento y es compatible con validación en BD y aplicación.
- **Alternatives considered**:
  - `CHAR(100)`: descartado por padding innecesario y menor eficiencia.
  - `TEXT` + validación en aplicación: descartado por no reforzar límite en esquema.

## Decision 2: Definición de llave primaria
- **Decision**: `clave` textual con formato `E-` + correlativo autoincremental con padding mínimo de 4 dígitos (ej. `E-0001`).
- **Rationale**: Cumple la aclaración del dominio solicitada por el usuario y mantiene identificador legible para negocio.
- **Alternatives considered**:
  - `BIGINT` autoincremental como PK expuesta: descartado por no cumplir formato requerido.
  - UUID: descartado por no aportar valor en este dominio.

## Decision 3: Estrategia de autenticación
- **Decision**: Aplicar HTTP Basic Authentication en todos los endpoints `/api/empleados/**`.
- **Rationale**: Cumple constitución y requisito FR-009 con configuración simple para entorno inicial.
- **Alternatives considered**:
  - JWT/OAuth2: descartado por sobrealcance para este feature.
  - Endpoints públicos: descartado por incumplir constitución.

## Decision 4: Contrato API
- **Decision**: Exponer contrato OpenAPI 3.0 para endpoints CRUD de empleados con security scheme `basicAuth`.
- **Rationale**: Permite validación de contrato, pruebas manuales con Swagger UI y trazabilidad para tareas.
- **Alternatives considered**:
  - Documentación solo textual: descartada por menor verificabilidad.

## Decision 5: Persistencia y acceso a datos
- **Decision**: Persistencia en PostgreSQL mediante Spring Data JPA; generación de `clave` con secuencia de BD + formateo de prefijo/padding en capa de servicio.
- **Rationale**: Garantiza unicidad y orden en generación, manteniendo formato de negocio `E-0001`.
- **Alternatives considered**:
  - JDBC plano: descartado por mayor boilerplate sin valor adicional en este alcance.

## Decision 6: Paginación de listados
- **Decision**: El endpoint de listado acepta `page` y retorna tamaño fijo de 5 elementos por página.
- **Rationale**: Cumple restricción de producto con comportamiento consistente y simple de consumir.
- **Alternatives considered**:
  - Tamaño configurable por cliente: descartado por desviarse del requerimiento explícito.
  - Sin paginación: descartado por incumplimiento de aclaración.

## Decision 7: Entorno de ejecución local
- **Decision**: Definir flujo local con Docker Compose para PostgreSQL y ejecución de la API con perfil local.
- **Rationale**: Asegura reproducibilidad y reduce dependencia de instalaciones manuales.
- **Alternatives considered**:
  - PostgreSQL local fuera de contenedor: válido pero no preferente por menor consistencia entre equipos.
