# Feature Specification: Frontend CRUD Empleados y Login

**Feature Branch**: `004-frontend-empleados-login`  
**Created**: 2026-03-12  
**Status**: Draft  
**Input**: User description: "Un fron que permita un crud de empleados y un login que tome email y el password para el logueo."

## Impacts *(mandatory for this project)*

- **Authentication impact**: El ingreso de usuarios desde la interfaz se realiza con principal (`username` o `email`) y `password`, con control de sesión y cierre de sesión explícito.
- **PostgreSQL impact**: No agrega nuevas tablas desde frontend; depende de datos existentes de empleados y credenciales con `email` para autenticación.
- **Docker impact**: La ejecución del frontend requiere configuración de entorno para URL base de API en entornos locales/contenedor.
- **Swagger impact**: Debe existir contrato actualizado para login por `email` y operaciones CRUD de empleados consumidas por la interfaz.
- **Frontend impact**: Se incorporan pantallas y flujos para login y administración completa de empleados (crear, listar, actualizar y eliminar).
- **Monorepo impact**: Se agregan artefactos de frontend y modelos compartidos alineados con contratos, manteniendo trazabilidad entre especificación y módulos del repositorio.

## Clarifications

### Session 2026-03-12

- Q: ¿Qué identificador debe usar la autenticación de login en backend cuando el formulario pide `email/password`? → A: El backend acepta principal flexible en HTTP Basic (`username:password` o `email:password`), sin traducción previa en frontend.
- Q: ¿Dónde se debe persistir la sesión del frontend? → A: La sesión se almacena en `sessionStorage` por pestaña y se pierde al cerrar la pestaña o navegador.
- Q: ¿Qué semántica aplica para eliminar empleados desde frontend? → A: `DELETE /api/v1/empleados/{clave}` realiza eliminación definitiva (hard delete).
- Q: ¿Cómo decide el frontend si un usuario puede gestionar empleados? → A: El frontend usa roles/permisos retornados por `GET /api/v1/auth/empleado/me` para habilitar o bloquear capacidades CRUD de administración.
- Q: ¿Cómo debe reaccionar la UI si `DELETE` devuelve `404` porque el empleado ya no existe? → A: Tratar `404` como no-op informativo, mostrar aviso no bloqueante y refrescar el listado para sincronizar estado.

### Session 2026-03-13

- Q: ¿Qué usuario debe poder controlar todo el CRUD de empleados? → A: El usuario bootstrap `master` (rol `ADMIN`) controla CRUD completo de empleados y credenciales.
- Q: ¿Qué alcance funcional tendrá un empleado autenticado no administrador en la UI? → A: Debe ver solo menú simple de sesión iniciada, sin acciones CRUD administrativas.
- Q: ¿Qué mejora de UX debe incluir el login para password? → A: El formulario debe incluir toggle para ver/ocultar contraseña.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Iniciar Sesion Con Email (Priority: P1)

Como usuario del sistema, quiero iniciar sesión con mi usuario o email y contraseña para acceder de forma segura a la aplicación.

**Why this priority**: Sin login funcional no existe acceso al resto de capacidades del sistema.

**Independent Test**: Desde la pantalla de login, autenticar con credenciales válidas y comprobar acceso al área principal; repetir con credenciales inválidas y comprobar rechazo controlado.

**Acceptance Scenarios**:

1. **Given** un usuario activo con `username` o `email` y contraseña válidos, **When** envía el formulario de login, **Then** el sistema inicia sesión y redirige al área principal autorizada.
2. **Given** un usuario con contraseña incorrecta o principal inexistente, **When** envía el formulario de login, **Then** el sistema rechaza el acceso y muestra un mensaje de error genérico.
3. **Given** una sesión activa, **When** el usuario ejecuta cerrar sesión, **Then** el sistema finaliza la sesión y protege nuevamente las vistas internas.

---

### User Story 2 - Gestionar Empleados Desde Frontend (Priority: P2)

Como administrador, quiero crear, consultar, editar y eliminar empleados para mantener actualizado el personal operativo.

**Why this priority**: Es el valor de negocio principal solicitado después del login, permitiendo operación diaria del catálogo de empleados.

**Independent Test**: Iniciar sesión como administrador, crear un empleado, editar sus datos, eliminar un registro y validar que los cambios se reflejan en el listado.

**Acceptance Scenarios**:

1. **Given** un administrador autenticado, **When** crea un empleado con datos válidos, **Then** el nuevo empleado aparece en el listado con confirmación de éxito.
2. **Given** un empleado existente, **When** el administrador actualiza su información, **Then** el sistema guarda cambios y muestra los datos actualizados.
3. **Given** un empleado existente, **When** el administrador confirma su eliminación, **Then** el sistema aplica la operación y el registro deja de estar disponible en el listado.
4. **Given** datos inválidos en un formulario CRUD, **When** el administrador intenta guardar, **Then** el sistema bloquea el envío y muestra validaciones claras por campo.

---

### User Story 3 - Operar Con Seguridad y Recuperación de Errores (Priority: P3)

Como usuario autenticado, quiero una experiencia consistente ante errores de sesión o API para continuar mi trabajo sin pérdida innecesaria de datos.

**Why this priority**: Reduce fricción operativa y evita acciones inconsistentes cuando hay conflictos o errores transitorios.

**Independent Test**: Provocar respuestas de error de autorización, validación y conflicto durante operaciones CRUD y verificar mensajes, preservación de datos de formulario y comportamiento de recuperación.

**Acceptance Scenarios**:

1. **Given** una sesión expirada, **When** el usuario realiza una acción protegida, **Then** el sistema redirige al login y solicita autenticación nuevamente.
2. **Given** un conflicto de actualización sobre un empleado, **When** el usuario intenta guardar cambios desactualizados, **Then** el sistema informa el conflicto y permite reintentar con datos actuales.
3. **Given** un error temporal del servicio, **When** falla una operación CRUD, **Then** el sistema informa el fallo, conserva datos ingresados y permite reintentar.

### Edge Cases

- Intento de login con campos vacíos de principal o password.
- Intento de login con cuenta inactiva o sin permisos para acceder a gestión de empleados.
- Envío repetido del mismo formulario por doble clic durante una operación en curso.
- Conflicto por edición concurrente del mismo empleado desde dos sesiones distintas.
- Pérdida de conectividad o error de servicio durante creación/edición con datos aún no guardados.
- Eliminación de un empleado que ya fue eliminado por otro administrador, tratándolo como no-op informativo (`404`) con refresco de listado.
- Listado de empleados vacío o con volumen alto que requiere paginación y estado de carga claro.
- Comportamiento en múltiples pestañas con sesiones independientes al usar `sessionStorage` por pestaña.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST mostrar una pantalla de login con campos `usuario o email` y `password` obligatorios.
- **FR-002**: El sistema MUST validar obligatoriedad de principal y password antes de enviar autenticación.
- **FR-003**: El sistema MUST permitir acceso a usuarios con credenciales válidas y bloquear acceso con credenciales inválidas.
- **FR-004**: El sistema MUST mostrar mensajes de autenticación fallida sin revelar si el error corresponde a email, password o estado de cuenta.
- **FR-005**: El sistema MUST permitir cierre de sesión explícito y proteger rutas internas tras finalizar sesión.
- **FR-006**: El sistema MUST restringir la gestión CRUD de empleados a usuarios con permisos administrativos obtenidos desde la respuesta de sesión autenticada.
- **FR-007**: El sistema MUST permitir crear empleados con validación de campos obligatorios y confirmación visible de resultado.
- **FR-008**: El sistema MUST permitir consultar/listar empleados con información suficiente para identificación operativa.
- **FR-009**: El sistema MUST permitir editar empleados existentes y reflejar cambios confirmados en la interfaz.
- **FR-010**: El sistema MUST permitir eliminar empleados con confirmación explícita previa.
- **FR-011**: El sistema MUST prevenir envíos duplicados durante operaciones en curso y mostrar estado de carga.
- **FR-012**: El sistema MUST gestionar errores de validación, autorización, no encontrado, conflicto y fallos de servicio con mensajes claros y acciones de recuperación.
- **FR-013**: El sistema MUST conservar datos ingresados en formularios cuando ocurra un error recuperable.
- **FR-014**: El sistema MUST mantener consistencia entre datos mostrados en el listado y el resultado de la última operación CRUD confirmada.
- **FR-015**: El sistema MUST autenticar el login usando principal flexible (`username` o `email`) en backend (HTTP Basic) y MUST evitar flujos de traducción previa de identificador en frontend.
- **FR-016**: El sistema MUST persistir estado de sesión/autenticación en `sessionStorage` por pestaña y MUST limpiar la sesión en logout o al cerrarse la pestaña/navegador.
- **FR-017**: El sistema MUST tratar la operación de `DELETE` de empleados como eliminación definitiva y MUST remover el registro de las vistas de listado posteriores a la operación exitosa.
- **FR-018**: El sistema MUST derivar permisos de UI para acciones administrativas desde los roles/permisos retornados por `GET /api/v1/auth/empleado/me` y MUST ocultar o deshabilitar acciones CRUD cuando el usuario no sea administrador.
- **FR-019**: Ante `DELETE` con `404` por recurso inexistente, el sistema MUST tratar la respuesta como no-op informativo, MUST mostrar aviso no bloqueante y MUST refrescar el listado para reflejar estado actualizado.
- **FR-020**: El sistema MUST incluir en login un control para mostrar/ocultar password sin perder el valor ingresado.
- **FR-021**: El sistema MUST mostrar menu administrativo solo a usuarios con rol `ADMIN`; usuarios `EMPLEADO` MUST ver un menu simple sin CRUD.

### Key Entities *(include if feature involves data)*

- **UsuarioAutenticado**: Persona con sesión activa en la interfaz; atributos clave: identificador, email, rol/permisos, estado de sesión.
- **Empleado**: Registro administrable en el catálogo de personal; atributos clave: clave, nombre, datos de contacto, estado y referencia de área/departamento.
- **SesionUsuario**: Estado temporal de autenticación y autorización en cliente; atributos clave: vigencia, permisos y último estado de acceso.
- **ResultadoOperacion**: Respuesta funcional mostrada al usuario tras acciones CRUD o login; atributos clave: estado, mensaje, detalle de validaciones y posibilidad de reintento.

## Assumptions

- Ya existe un servicio backend con endpoints para login por principal (`username` o `email`) y CRUD de empleados.
- El sistema distingue roles de usuario para habilitar o restringir gestión administrativa.
- La autenticación y autorización se gestionan por sesión válida con expiración controlada.

## Dependencies

- Contratos API actualizados y accesibles para login y CRUD de empleados.
- El servicio de autenticación backend debe aceptar principal flexible (`username` o `email`) para login.
- El endpoint `GET /api/v1/auth/empleado/me` debe retornar roles/permisos suficientes para decidir autorización en frontend.
- Disponibilidad de entorno de ejecución con configuración de URL de API para frontend.
- Datos de empleados y credenciales consistentes en el sistema fuente.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Al menos el 95% de usuarios válidos completa el inicio de sesión en menos de 60 segundos desde la pantalla de login.
- **SC-002**: Al menos el 90% de operaciones CRUD de empleados se completa exitosamente en el primer intento cuando los datos son válidos.
- **SC-003**: El 100% de intentos de acceso con credenciales inválidas es rechazado con un mensaje controlado y sin acceso a vistas protegidas.
- **SC-004**: El 100% de errores recuperables en formularios CRUD conserva los datos ingresados para permitir corrección sin recaptura completa.
- **SC-005**: El tiempo de actualización visible del listado tras una operación CRUD confirmada es menor a 3 segundos en al menos el 95% de los casos.
