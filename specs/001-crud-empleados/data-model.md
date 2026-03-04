# Data Model - CRUD de Empleados

## Entity: Empleado

### Description
Representa un empleado gestionado por el módulo CRUD.

### Fields
- **clave**
  - Type: `VARCHAR(20)`
  - Constraints: primary key, unique, not null, pattern `^E-[0-9]{4,}$`
  - Source: generado por el sistema usando secuencia autoincremental + formateo
- **nombre**
  - Type: `VARCHAR(100)`
  - Constraints: not null, longitud 1..100
- **direccion**
  - Type: `VARCHAR(100)`
  - Constraints: not null, longitud 1..100
- **telefono**
  - Type: `VARCHAR(100)`
  - Constraints: not null, longitud 1..100

### Validation Rules
- `nombre`, `direccion`, `telefono` son obligatorios en creación y actualización.
- La longitud máxima de `nombre`, `direccion`, `telefono` es 100 caracteres.
- `clave` no se recibe en el payload de creación.
- `clave` se genera como `E-` + correlativo con padding mínimo de 4 dígitos.

## Read Model: EmpleadoPage

### Description
Representa la respuesta de listado paginado de empleados.

### Fields
- **content**: lista de `Empleado`
- **page**: número de página (base 0)
- **size**: tamaño fijo de página (valor 5)
- **totalElements**: total de empleados existentes
- **totalPages**: total de páginas calculadas

### State Transitions
- **CREATE**: registro inexistente -> registro persistido con `clave` generada.
- **UPDATE**: registro existente -> registro actualizado (misma `clave`).
- **DELETE**: registro existente -> registro eliminado.
- **LIST**: consulta paginada -> página de hasta 5 empleados.

### Relationships
- No hay relaciones obligatorias en este feature.
