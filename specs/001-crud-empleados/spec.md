# Feature Specification: CRUD de Empleados

**Feature Branch**: `001-crud-empleados`  
**Created**: 2026-02-25  
**Status**: Draft  
**Input**: User description: "Crear un crud de empleados con los campos clave, nombre, direccion y telefono. Donde clave sea el primary key y los demas campos sean de 100 espacios."

## Clarifications

### Session 2026-02-25

- Q: ¿Cómo se define `clave` como PK? → A: Formato `E-` + número autoincremental con padding (ej. `E-0001`).
- Q: ¿Cómo debe funcionar el listado de empleados? → A: Paginación obligatoria de 5 registros por página.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar y consultar empleados (Priority: P1)

Como usuario autenticado, quiero registrar empleados y consultar la lista/detalle para tener
un inventario básico de empleados disponible en el sistema.

**Why this priority**: Sin creación y consulta no existe valor funcional mínimo del módulo.

**Independent Test**: Puede probarse creando un empleado válido y luego consultándolo por clave y
en listado general, verificando persistencia y formato de datos.

**Acceptance Scenarios**:

1. **Given** un usuario autenticado, **When** envía un alta de empleado con `nombre`,
  `direccion` y `telefono` válidos, **Then** el sistema guarda el empleado, genera `clave`
  con formato `E-0001` y devuelve confirmación.
2. **Given** un empleado existente, **When** el usuario autenticado consulta por `clave`,
   **Then** el sistema devuelve sus datos completos.
3. **Given** empleados registrados, **When** el usuario autenticado consulta el listado,
   **Then** el sistema devuelve resultados paginados con 5 empleados por página.

---

### User Story 2 - Actualizar datos de empleados (Priority: P2)

Como usuario autenticado, quiero actualizar nombre, dirección o teléfono de un empleado para
mantener la información vigente.

**Why this priority**: Mantener información actualizada es crítico después de tener el alta y consulta.

**Independent Test**: Puede probarse con un empleado existente, modificando campos válidos y
verificando luego que la consulta refleje los cambios.

**Acceptance Scenarios**:

1. **Given** un empleado existente, **When** el usuario autenticado envía una actualización válida,
   **Then** el sistema reemplaza los datos y conserva la misma `clave`.
2. **Given** una `clave` inexistente, **When** el usuario autenticado intenta actualizar,
   **Then** el sistema informa que el empleado no existe.

---

### User Story 3 - Eliminar empleados (Priority: P3)

Como usuario autenticado, quiero eliminar empleados para retirar registros que ya no son necesarios.

**Why this priority**: Cierra el ciclo CRUD completo, pero depende de que el alta/consulta ya exista.

**Independent Test**: Puede probarse eliminando un empleado existente y validando que ya no aparezca
en consultas por clave ni listados.

**Acceptance Scenarios**:

1. **Given** un empleado existente, **When** el usuario autenticado solicita su eliminación,
   **Then** el sistema borra el registro y confirma la operación.
2. **Given** un empleado eliminado, **When** se consulta nuevamente por su `clave`,
   **Then** el sistema responde que no existe.

### Edge Cases

- Desbordamiento de secuencia o error en el formateo de `clave` (`E-0001`).
- Intento de guardar o actualizar `nombre`, `direccion` o `telefono` con más de 100 caracteres.
- Intento de crear o actualizar con campos obligatorios vacíos.
- Operaciones de consulta, actualización o eliminación sobre una `clave` inexistente.
- Intento de acceso a endpoints CRUD sin autenticación básica válida.
- Solicitud de paginación con página inexistente o parámetro de página inválido.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear empleados con los campos `nombre`, `direccion` y
  `telefono`; `clave` MUST ser generada automáticamente.
- **FR-002**: El campo `clave` MUST ser único, actuar como llave primaria y seguir el formato
  `E-` + número autoincremental con padding a 4 dígitos (ejemplo: `E-0001`).
- **FR-003**: Los campos `nombre`, `direccion` y `telefono` MUST aceptar como máximo 100 caracteres.
- **FR-004**: El sistema MUST rechazar solicitudes de creación o actualización que excedan los 100
  caracteres en cualquiera de esos campos.
- **FR-005**: El sistema MUST permitir consultar todos los empleados y consultar un empleado por `clave`.
- **FR-006**: El sistema MUST permitir actualizar los datos de un empleado existente manteniendo su `clave`.
- **FR-007**: El sistema MUST permitir eliminar un empleado por `clave`.
- **FR-008**: El sistema MUST retornar mensajes de error claros cuando la `clave` no exista.
- **FR-009**: El sistema MUST proteger todas las operaciones CRUD de empleados con autenticación básica.
- **FR-010**: El sistema MUST persistir la información de empleados en PostgreSQL.
- **FR-011**: El sistema MUST listar empleados con paginación obligatoria de 5 registros por página.

### Constitution Alignment *(mandatory for backend features)*

- **CA-001**: La funcionalidad MUST ejecutarse en Spring Boot 3 con Java 17.
- **CA-002**: La autenticación MUST aplicar HTTP Basic en todos los endpoints CRUD de empleados,
  con credenciales de desarrollo `admin`/`admin123`.
- **CA-003**: El modelo de datos de empleados MUST almacenarse en PostgreSQL, incluyendo la llave
  primaria `clave` con formato `E-0001` autogenerado y restricciones de longitud de campos.
- **CA-004**: La funcionalidad MUST incluir impacto Docker documentado para ejecutar API y PostgreSQL
  en entorno reproducible.
- **CA-005**: La funcionalidad MUST exponer documentación Swagger/OpenAPI para altas, bajas,
  cambios y consultas de empleados.

### Key Entities *(include if feature involves data)*

- **Empleado**: Representa un registro de empleado con `clave` (texto con formato `E-0001`),
  `nombre`, `direccion` y `telefono`.

## Assumptions

- `clave` se genera automáticamente con prefijo `E-` y numeración autoincremental con padding.
- Todos los campos del empleado son obligatorios para crear el registro.
- El endpoint de listado usa tamaño de página fijo de 5 registros.

## Dependencies

- Disponibilidad de un servicio PostgreSQL accesible por la aplicación.
- Credenciales de autenticación básica configuradas para acceso al módulo.
- Entorno con documentación Swagger habilitada para validación funcional del contrato API.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de altas válidas de empleados registra datos persistentes y recuperables por `clave`.
- **SC-002**: El 100% de intentos con campos mayores a 100 caracteres son rechazados con mensaje
  de validación comprensible.
- **SC-003**: Al menos el 95% de operaciones CRUD completan su respuesta en menos de 2 segundos
  bajo carga de uso normal del equipo.
- **SC-004**: El 100% de endpoints CRUD del módulo de empleados aparecen documentados y probables desde Swagger.
- **SC-005**: El 100% de respuestas de listado respetan un máximo de 5 registros por página.
