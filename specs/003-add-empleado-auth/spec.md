# Feature Specification: Autenticacion Por Empleado

**Feature Branch**: `003-add-empleado-auth`  
**Created**: 2026-03-10  
**Status**: Draft  
**Input**: User description: "hazme una autenticacion de usuario y contraseña por cada empleado"

## Impacts

- **Authentication impact**: El acceso funcional de empleados pasa de credencial compartida a credencial individual por empleado, manteniendo cuenta bootstrap `master` (con alias configurables, por ejemplo `admin`) para administracion global.
- **PostgreSQL impact**: Se persisten credenciales por empleado, incluyendo el nuevo campo `email` (`varchar`), estado de bloqueo temporal y eventos de autenticacion auditables.
- **Docker impact**: Se deben mantener variables de entorno para credenciales bootstrap y politicas de autenticacion en flujos de contenedor/compose.
- **Swagger impact**: Los endpoints de autenticacion y administracion de credenciales deben documentarse con respuestas de exito y rechazo, incluyendo `email` en payloads de credenciales y `roles` en `GET /api/v1/auth/empleado/me`.
- **Frontend impact**: Sin impacto funcional en UI para esta iteracion; cualquier pantalla Angular 19.2.22 que consuma credenciales debe incorporar `email` al formulario/DTO cuando se implemente frontend.
- **Monorepo impact**: Se actualizan contratos/documentacion del feature en el mismo repositorio para mantener trazabilidad backend-spec compartida; no se introduce nuevo repositorio.

## Clarifications

### Session 2026-03-10

- Q: ¿Qué modelo de administrador se usará para gestionar credenciales? → A: Mantener un usuario tecnico bootstrap para endpoints administrativos (actualmente `master`, con alias como `admin`); empleados usan credenciales propias para login funcional.
- Q: ¿Qué politica minima de password se aplicará? → A: Minimo 12 y maximo 128 caracteres, sin composicion obligatoria, con denylist de contrasenas comunes/filtradas.
- Q: ¿Cómo se definirá la trazabilidad de actualizaciones de credenciales? → A: Mantener ambos campos: `passwordUpdatedAt` para cambios de password y `updatedAt` para cualquier mutacion de credencial.
- Q: ¿Qué codigo HTTP debe devolverse cuando la cuenta esté bloqueada temporalmente? → A: Responder siempre `401 Unauthorized` en autenticacion fallida (incluyendo bloqueo), registrando internamente el motivo `LOCKED`.
- Q: ¿Qué política de concurrencia se aplicará en actualización de credenciales? → A: Concurrencia optimista con `version`; si la versión está desactualizada se responde `409 Conflict`.
- Q: ¿Cuál es la duracion del bloqueo temporal de cuenta? → A: 30 minutos.

### Session 2026-03-11

- Q: ¿Cuál será la regla de unicidad para `email` en credenciales? → A: `email` MUST ser unico global entre todas las credenciales de empleados, incluyendo cuentas inactivas.
- Q: ¿Qué comportamiento aplica si la denylist no está disponible en alta/cambio de password? → A: Usar snapshot de denylist en cache; si no existe snapshot disponible, rechazar la operacion con `503 Service Unavailable`.

### Session 2026-03-13

- Q: ¿Qué cuenta debe tener control total del CRUD y de la administracion global? → A: La cuenta bootstrap `master` (con alias configurables como `admin`) MUST operar con rol administrativo para controlar CRUD de empleados y credenciales.
- Q: ¿Cómo debe autenticarse un empleado desde login cuando el formulario usa principal flexible? → A: El backend MUST aceptar autenticacion por `username` o `email` en HTTP Basic para cuentas de empleado.
- Q: ¿Qué debe devolver `GET /api/v1/auth/empleado/me` para soportar UI por rol? → A: Debe incluir `roles` y soportar identidad bootstrap (clave `MASTER`) ademas de empleados (`E-xxxx`).
- Q: ¿Qué reglas CORS deben aplicarse para desarrollo local con Angular? → A: Se deben permitir origenes configurables (por defecto `http://localhost:4200` y `http://127.0.0.1:4200`) incluyendo preflight `OPTIONS`.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Iniciar Sesion Como Empleado (Priority: P1)

Como empleado, quiero autenticarme con mi propio usuario y contrasena para acceder al sistema con mi identidad individual.

**Why this priority**: Es el valor principal solicitado: eliminar el acceso compartido y habilitar autenticacion por persona.

**Independent Test**: Crear un empleado con credenciales, iniciar sesion con esas credenciales y confirmar que con credenciales invalidas se rechaza el acceso.

**Acceptance Scenarios**:

1. **Given** un empleado con `username` o `email` y contrasena activos, **When** inicia sesion con credenciales correctas, **Then** el sistema autentica al empleado y concede acceso.
2. **Given** un usuario o contrasena incorrectos, **When** se intenta iniciar sesion, **Then** el sistema rechaza el acceso con respuesta de no autorizado.
3. **Given** un empleado desactivado, **When** intenta iniciar sesion, **Then** el sistema rechaza el acceso.

---

### User Story 2 - Administrar Credenciales De Empleados (Priority: P2)

Como usuario master, quiero asignar y actualizar usuario, email y contrasena por empleado para mantener el control de acceso del personal.

**Why this priority**: Permite operar el alta de credenciales y mantener acceso vigente sin depender de procesos manuales externos.

**Independent Test**: Asignar credenciales unicas (incluyendo `email`) a un empleado, actualizar su contrasena y email, y verificar que la credencial anterior deja de funcionar.

**Acceptance Scenarios**:

1. **Given** un empleado sin credenciales, **When** el usuario master asigna usuario, email y contrasena validos, **Then** el empleado puede autenticarse con esas credenciales.
2. **Given** un empleado con credenciales existentes, **When** el usuario master actualiza la contrasena, **Then** solo la nueva contrasena es valida.
3. **Given** un usuario ya asignado a otro empleado, **When** el usuario master intenta reutilizarlo, **Then** el sistema rechaza la operacion por duplicidad.
4. **Given** dos actualizaciones concurrentes de credenciales del mismo empleado, **When** una solicitud persiste con `version` desactualizada, **Then** el sistema rechaza la operacion con `409 Conflict`.
5. **Given** un email con formato invalido o vacio, **When** el administrador intenta asignarlo en credenciales, **Then** el sistema rechaza la operacion por validacion.
6. **Given** un `email` ya asignado a otro empleado, **When** el administrador intenta reutilizarlo, **Then** el sistema rechaza la operacion por duplicidad global.

---

### User Story 3 - Proteger El Acceso Ante Riesgos (Priority: P3)

Como responsable del sistema, quiero controles basicos ante intentos fallidos para reducir accesos no autorizados.

**Why this priority**: Reduce riesgo operativo y protege cuentas de empleados frente a intentos repetidos de acceso.

**Independent Test**: Realizar intentos fallidos consecutivos sobre una cuenta y validar bloqueo temporal de 30 minutos, luego comprobar que vuelve a permitir autenticacion tras ese periodo.

**Acceptance Scenarios**:

1. **Given** multiples intentos fallidos consecutivos en una cuenta, **When** se alcanza el umbral configurado, **Then** el sistema bloquea temporalmente la autenticacion de esa cuenta.
2. **Given** una cuenta bloqueada temporalmente, **When** se intenta iniciar sesion durante el bloqueo, **Then** el sistema rechaza el acceso.
3. **Given** que el bloqueo temporal finaliza, **When** el empleado inicia sesion con credenciales correctas, **Then** el sistema permite nuevamente el acceso.

### Edge Cases

- Intento de autenticacion con campos vacios de usuario o contrasena.
- Intento de crear o actualizar credenciales con usuario ya existente.
- Intento de crear o actualizar credenciales con `email` vacio o con formato invalido.
- Intento de crear o actualizar credenciales con `email` ya registrado en otra cuenta, incluyendo cuentas inactivas.
- Intento de asignar credenciales a un empleado inexistente.
- Intento de autenticacion de empleado eliminado o inactivo.
- Intentos fallidos en multiples cuentas desde el mismo cliente en corto periodo.
- Solicitudes concurrentes de cambio de contrasena para el mismo empleado.
- Intento de asignar o cambiar password con menos de 12, mas de 128 caracteres o incluida en denylist.
- Intento de alta o cambio de password cuando la denylist no esta disponible y no existe snapshot local, devolviendo `503 Service Unavailable`.
- Intento de autenticacion en cuenta bloqueada temporalmente, devolviendo `401 Unauthorized` sin revelar estado de bloqueo al cliente.
- Actualizacion concurrente de credenciales con `version` desactualizada, devolviendo `409 Conflict`.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir registrar credenciales de acceso (usuario, `email` y contrasena) para cada empleado.
- **FR-002**: El username de acceso MUST ser unico a nivel global en todo el sistema (no puede repetirse entre empleados, independientemente de su estado).
- **FR-003**: El sistema MUST autenticar a un empleado solo cuando el usuario y la contrasena coincidan con una cuenta activa.
- **FR-004**: El sistema MUST rechazar autenticaciones invalidas sin exponer si fallo el usuario, la contrasena o el estado de bloqueo de la cuenta.
- **FR-005**: El sistema MUST impedir autenticacion de empleados inactivos o eliminados.
- **FR-006**: El sistema MUST permitir que la cuenta tecnica bootstrap `master` (no-empleado) asigne, actualice y desactive credenciales por empleado.
- **FR-007**: El sistema MUST invalidar la contrasena anterior inmediatamente despues de un cambio de contrasena.
- **FR-008**: El sistema MUST exigir password de 12 a 128 caracteres, sin reglas de composicion obligatoria, y MUST rechazar contrasenas en denylist de contrasenas comunes/filtradas.
- **FR-009**: El sistema MUST bloquear temporalmente una cuenta por 30 minutos tras 5 intentos fallidos consecutivos en una ventana de 15 minutos.
- **FR-010**: El sistema MUST registrar eventos de autenticacion exitosa, fallida y bloqueo de cuenta para auditoria.
- **FR-011**: El sistema MUST no exponer contrasenas ni equivalentes de contrasena en respuestas de consulta o listado.
- **FR-012**: El sistema MUST mantener trazabilidad con dos marcas de tiempo: `passwordUpdatedAt` para la ultima actualizacion de password y `updatedAt` para la ultima mutacion de credencial (usuario, password o estado).
- **FR-013**: El sistema MUST impedir inicio de sesion de empleados sin credenciales asignadas y MUST habilitarlo inmediatamente cuando la credencial quede asignada y activa.
- **FR-014**: El sistema MUST mantener una cuenta tecnica bootstrap `master` (no asociada a empleado) para operaciones administrativas de credenciales y CRUD global, con credenciales externalizables en produccion y aliases configurables (por ejemplo `admin`).
- **FR-015**: El sistema MUST responder `401 Unauthorized` para toda autenticacion fallida, incluyendo cuentas temporalmente bloqueadas, y MUST registrar internamente el motivo del rechazo para auditoria.
- **FR-016**: El sistema MUST aplicar concurrencia optimista en actualizaciones de credenciales usando `version`, y MUST responder `409 Conflict` cuando la version de escritura esté desactualizada.
- **FR-017**: El sistema MUST almacenar el campo `email` de cada credencial de empleado como `varchar` y mantenerlo disponible en operaciones de consulta y actualizacion de credenciales.
- **FR-018**: El sistema MUST validar que `email` tenga formato de correo valido antes de persistir altas o actualizaciones de credenciales.
- **FR-019**: El sistema MUST garantizar unicidad global de `email` entre todas las credenciales de empleados (activas e inactivas) y MUST rechazar duplicados en alta/actualizacion.
- **FR-020**: En alta/cambio de password, el sistema MUST validar contra denylist usando snapshot local en cache cuando la fuente principal no este disponible, y MUST responder `503 Service Unavailable` si no existe snapshot utilizable.
- **FR-021**: El sistema MUST permitir autenticacion de empleado por `username` o `email` como principal en HTTP Basic.
- **FR-022**: El endpoint `GET /api/v1/auth/empleado/me` MUST devolver `roles` para decisiones de UI y MUST soportar identidad bootstrap con `empleadoClave=MASTER`.
- **FR-023**: El sistema MUST habilitar CORS configurable para desarrollo frontend local y MUST aceptar solicitudes preflight `OPTIONS`.

### Key Entities *(include if feature involves data)*

- **Empleado**: Persona registrada en el sistema que requiere identidad individual para acceso.
- **CredencialEmpleado**: Conjunto de datos de autenticacion asociado a un empleado (`email` en `varchar` con unicidad global, usuario unico, estado, vigencia de contrasena, intentos fallidos, `passwordUpdatedAt` y `updatedAt`).
- **SesionAutenticada**: Resultado de una autenticacion exitosa, con identidad del empleado y vigencia de acceso.
- **EventoAutenticacion**: Registro auditable de intentos de inicio de sesion, bloqueos y cambios de credenciales.

## Assumptions

- La gestion de empleados ya existe y esta operativa en el sistema actual.
- Las credenciales por empleado aplican a todo acceso funcional del empleado en esta version.
- Existe una cuenta tecnica bootstrap `master` no asociada a empleados para administracion global; `admin` puede operar como alias configurable.

## Dependencies

- Disponibilidad de almacenamiento persistente para credenciales y eventos de autenticacion.
- Disponibilidad y mantenimiento de una denylist de contrasenas comunes/filtradas.
- Alineacion con requisitos internos de auditoria y trazabilidad de accesos.
- Configuracion segura de credenciales del usuario bootstrap `admin` por variables de entorno o secretos.
- Configuracion segura de credenciales y aliases del usuario bootstrap `master` por variables de entorno o secretos.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de empleados con credenciales activas puede autenticarse correctamente con su usuario y contrasena individuales.
- **SC-002**: El 100% de intentos con credenciales invalidas es rechazado con respuesta de no autorizado.
- **SC-003**: En el escenario de validacion definido en `specs/003-add-empleado-auth/quickstart.md`, al menos el 95% de inicios de sesion exitosos se completa en menos de 3 segundos.
- **SC-004**: El 100% de cambios de contrasena invalida la contrasena anterior en la siguiente autenticacion.
- **SC-005**: El 100% de bloqueos por intentos fallidos queda registrado en auditoria con identificacion de empleado y marca de tiempo.
- **SC-006**: El 100% de altas y actualizaciones de credenciales persiste y devuelve correctamente el campo `email` cuando el valor es valido.
