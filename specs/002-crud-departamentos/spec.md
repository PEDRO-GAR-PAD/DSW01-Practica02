# Feature Specification: CRUD de Departamentos

**Feature Branch**: `002-crud-departamentos`  
**Created**: 2026-03-09  
**Status**: Draft  
**Input**: User description: "Crea un crud de departamentos con los campos clave, y nombre del departamanto, el primary key seria el campo clave, la clave estara en esta estrutura D-0001 de la clave."

## Clarifications

### Session 2026-03-09

- Q: ¿Cómo deben exponerse las rutas del CRUD de departamentos? → A: Solo rutas versionadas `/api/v1/departamentos`; rutas no versionadas deben responder `404 Not Found`.
- Q: ¿Cómo debe aplicarse la autenticación en el CRUD de departamentos? → A: HTTP Basic obligatoria en todos los endpoints de departamentos (`GET`, `POST`, `PUT`, `DELETE`).
- Q: ¿Cómo debe comportarse el listado de departamentos? → A: Listado paginado obligatorio con parámetro `page` y tamaño fijo de 5 registros por página.
- Q: ¿Cómo manejar paginación inválida o fuera de rango? → A: `page` inválido (`<0` o no numérico) devuelve `400`; fuera de rango devuelve `200` con `content` vacío.
- Q: ¿Qué estructura de error debe usar la API de departamentos? → A: Error JSON estándar con `timestamp`, `status`, `error`, `message` y `fieldErrors` (solo en validación).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar y consultar departamentos (Priority: P1)

Como usuario del sistema, quiero registrar departamentos y consultarlos para contar con un catálogo operativo y confiable.

**Why this priority**: La creación y consulta del catálogo es el valor mínimo para empezar a usar el módulo.

**Independent Test**: Se prueba creando un departamento con nombre válido, consultándolo por su clave y listándolo en el catálogo general.

**Acceptance Scenarios**:

1. **Given** un usuario con acceso al módulo, **When** registra un departamento con nombre válido, **Then** el sistema crea el registro y asigna una clave con formato `D-0001`.
2. **Given** departamentos existentes, **When** el usuario consulta por clave, **Then** el sistema devuelve los datos del departamento correspondiente.
3. **Given** departamentos existentes, **When** el usuario solicita el listado con `page`, **Then** el sistema devuelve resultados paginados con tamaño fijo de 5 registros por página.
4. **Given** una solicitud de alta que incluye un valor manual para `clave`, **When** el usuario intenta registrar el departamento, **Then** el sistema rechaza la solicitud por dato no permitido.
5. **Given** que la secuencia de claves alcanzó `D-9999`, **When** el usuario intenta registrar un nuevo departamento, **Then** el sistema responde con conflicto de capacidad.
6. **Given** que el modulo expone solo rutas versionadas, **When** el usuario consume `/api/departamentos`, **Then** el sistema responde `404 Not Found`.
7. **Given** que un cliente no envia credenciales validas, **When** intenta consumir cualquier endpoint de departamentos, **Then** el sistema responde `401 Unauthorized`.
8. **Given** que el cliente solicita `page` inválido o fuera de rango, **When** consume el listado de departamentos, **Then** el sistema devuelve `400` para inválido y `200` con `content` vacío para fuera de rango.

---

### User Story 2 - Actualizar nombre de departamento (Priority: P2)

Como usuario del sistema, quiero actualizar el nombre de un departamento para mantener el catálogo al día.

**Why this priority**: La actualización mantiene la utilidad del catálogo, pero depende de que primero exista información registrada.

**Independent Test**: Se prueba actualizando el nombre de un departamento existente y validando después que conserva su clave original.

**Acceptance Scenarios**:

1. **Given** un departamento existente, **When** el usuario actualiza su nombre, **Then** el sistema guarda el nuevo nombre sin cambiar la clave.
2. **Given** una clave inexistente, **When** el usuario intenta actualizar, **Then** el sistema informa que el departamento no existe.
3. **Given** una solicitud con nombre vacío o inválido, **When** el usuario intenta actualizar, **Then** el sistema rechaza la operación con un mensaje de validación.

---

### User Story 3 - Eliminar departamentos (Priority: P3)

Como usuario del sistema, quiero eliminar departamentos que ya no se usan para mantener el catálogo limpio.

**Why this priority**: Completa el ciclo CRUD, pero se ejecuta después de tener creación, consulta y actualización.

**Independent Test**: Se prueba eliminando un departamento existente y luego intentando consultarlo para confirmar que ya no está disponible.

**Acceptance Scenarios**:

1. **Given** un departamento existente, **When** el usuario solicita eliminarlo, **Then** el sistema elimina el registro.
2. **Given** un departamento eliminado, **When** el usuario intenta consultarlo por clave, **Then** el sistema responde que no existe.

### Edge Cases

- Intento de crear un departamento sin nombre o con nombre vacío.
- Intento de crear un departamento enviando manualmente la `clave`.
- Consulta, actualización o eliminación usando una `clave` inexistente.
- Error al generar una nueva `clave` cuando se alcanza el límite de 4 dígitos (`D-9999`).
- Registro o actualización con `nombre` que supera la longitud máxima permitida.
- Solicitud a rutas no versionadas del modulo (por ejemplo, `/api/departamentos`) cuando solo se soporta `/api/v1/departamentos`, devolviendo `404 Not Found`.
- Intento de acceso sin credenciales o con credenciales inválidas en endpoints de departamentos, devolviendo `401 Unauthorized`.
- Solicitud de paginación con `page` inválido (`<0` o no numérico), devolviendo `400 Bad Request`.
- Solicitud de paginación con `page` fuera de rango, devolviendo `200` con `content` vacío.
- Respuestas de error `400`, `401`, `404` y `409` con estructura JSON estándar consistente.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear departamentos con el campo `nombre`, generando automáticamente el campo `clave`.
- **FR-002**: El campo `clave` MUST actuar como llave primaria única del departamento.
- **FR-003**: La `clave` generada MUST seguir el formato `D-` más 4 dígitos con cero a la izquierda (ejemplo: `D-0001`).
- **FR-004**: El sistema MUST permitir consultar un departamento por `clave`.
- **FR-005**: El sistema MUST permitir consultar el listado de departamentos con paginación obligatoria usando parámetro `page`.
- **FR-006**: El sistema MUST permitir actualizar el `nombre` de un departamento existente manteniendo su `clave`.
- **FR-007**: El sistema MUST permitir eliminar un departamento existente por `clave`.
- **FR-008**: El sistema MUST rechazar operaciones sobre claves inexistentes con un mensaje claro de no encontrado.
- **FR-009**: El sistema MUST rechazar solicitudes de creación o actualización cuando `nombre` sea vacío o inválido.
- **FR-010**: El sistema MUST rechazar solicitudes de creación que intenten definir manualmente el valor de `clave`.
- **FR-011**: El sistema MUST informar conflicto de capacidad cuando ya no sea posible generar una nueva clave en el rango de 4 dígitos.
- **FR-012**: El sistema MUST exponer los endpoints de departamentos únicamente bajo rutas versionadas con prefijo `/api/v1`.
- **FR-013**: El sistema MUST responder `404 Not Found` para solicitudes a rutas no versionadas del CRUD de departamentos (por ejemplo, `/api/departamentos` y `/api/departamentos/{clave}`).
- **FR-014**: El sistema MUST proteger todos los endpoints de departamentos con autenticación HTTP Basic y responder `401 Unauthorized` cuando falten credenciales válidas.
- **FR-015**: El sistema MUST devolver resultados de listado con tamaño fijo de 5 registros por página.
- **FR-016**: El sistema MUST responder `400 Bad Request` cuando `page` sea inválido (`<0` o no numérico), y MUST responder `200` con `content` vacío cuando `page` esté fuera de rango.
- **FR-017**: El sistema MUST retornar errores en formato JSON estándar con `timestamp`, `status`, `error`, `message` y `fieldErrors` (este último solo para errores de validación).

### Key Entities *(include if feature involves data)*

- **Departamento**: Representa una unidad organizacional con `clave` única (formato `D-0001`) y `nombre`.

## Assumptions

- El catálogo de departamentos usa una secuencia correlativa para `clave`, iniciando en `D-0001`.
- El nombre del departamento es obligatorio y tiene límite máximo de 100 caracteres.
- La gestión de departamentos no requiere campos adicionales en esta versión.

## Dependencies

- Disponibilidad de un medio de almacenamiento para persistir el catálogo de departamentos.
- Disponibilidad de un canal de consulta y gestión para crear, listar, actualizar y eliminar departamentos.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de altas válidas de departamentos genera una `clave` con formato `D-0001` y permite recuperación posterior por esa clave.
- **SC-002**: El 100% de consultas, actualizaciones y eliminaciones con clave inexistente devuelve un resultado de no encontrado comprensible para el usuario.
- **SC-003**: Al menos el 95% de operaciones CRUD sobre departamentos finaliza en menos de 2 segundos bajo condiciones normales de uso.
- **SC-004**: Al menos el 95% de usuarios completa el flujo de alta y consulta de departamento sin ayuda externa durante validación funcional.
