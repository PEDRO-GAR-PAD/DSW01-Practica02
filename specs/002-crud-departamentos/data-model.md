# Data Model - CRUD de Departamentos

## Entity: Departamento

### Description
Representa una unidad organizacional dentro del catálogo del sistema.

### Fields
- **clave**
  - Type: `VARCHAR(20)`
  - Constraints: primary key, unique, not null, pattern `^D-[0-9]{4}$`
  - Source: generado automáticamente por secuencia + padding
- **nombre**
  - Type: `VARCHAR(100)`
  - Constraints: not null, longitud 1..100

### Validation Rules
- `nombre` es obligatorio en creación y actualización.
- `nombre` no puede estar vacío y su longitud máxima es 100 caracteres.
- `clave` no se acepta en payload de creación.
- `clave` permanece inmutable en actualización.
- Si la secuencia supera `D-9999`, se devuelve conflicto de capacidad.

## Read Model: DepartamentoPageResponse

### Description
Respuesta de listado paginado para `/api/v1/departamentos`.

### Fields
- **content**: lista de `DepartamentoResponse`
- **page**: número de página (base 0)
- **size**: tamaño fijo de página (`5`)
- **totalElements**: total de registros
- **totalPages**: total de páginas

### Validation/State Rules
- `page` inválido (`<0` o no numérico) devuelve `400 Bad Request`.
- `page` fuera de rango devuelve `200` con `content` vacío.

## Read Model: DepartamentoResponse

### Description
Representa la salida estándar de detalle/listado de departamentos.

### Fields
- **clave**: identificador con formato `D-0001`
- **nombre**: nombre vigente del departamento

## Read Model: ApiError

### Description
Estructura estándar para errores de validación, no encontrado y conflicto.

### Fields
- **timestamp**: fecha/hora ISO-8601
- **status**: código HTTP
- **error**: etiqueta HTTP
- **message**: descripción funcional del error
- **fieldErrors**: mapa de errores por campo (solo validación)

### Relationships
- `Departamento.clave` es referenciada logicamente por `Empleado.departamentoClave`.
- Si un departamento tiene empleados asociados, su eliminacion responde `409 Conflict`.