# Data Model - Frontend CRUD Empleados y Login

## Entity: LoginFormState

### Description
Estado del formulario de login por principal (`username` o `email`) y password en la UI.

### Fields
- **principal**
  - Type: `string`
  - Constraints: requerido, no vacio
- **password**
  - Type: `string`
  - Constraints: requerido, no vacio
- **showPassword**
  - Type: `boolean`
  - Constraints: requerido, default `false`
- **submitStatus**
  - Type: `enum(idle, loading, success, error)`
  - Constraints: requerido
- **errorMessage**
  - Type: `string | null`
  - Constraints: nullable

### Validation Rules
- No enviar solicitud de login con campos vacios.
- Bloquear doble envio cuando `submitStatus=loading`.
- Mostrar mensaje generico en auth fallida sin filtrar causa exacta.
- El toggle `showPassword` no altera valor del campo password.

### State Transitions
- **Idle -> Loading**: usuario envia credenciales validas.
- **Loading -> Success**: backend valida credenciales.
- **Loading -> Error**: backend rechaza o falla la solicitud.
- **Error -> Loading**: usuario corrige datos y reintenta.

## Entity: AuthSessionView

### Description
Representa sesion autenticada en cliente para acceso a rutas privadas.

### Fields
- **principalEmail**
  - Type: `string`
  - Constraints: requerido
  - Notes: puede contener `email` o `username` segun el principal usado al autenticar.
- **displayName**
  - Type: `string`
  - Constraints: requerido
- **roles**
  - Type: `string[]`
  - Constraints: opcional en respuesta backend; frontend aplica fallback seguro
- **authHeader**
  - Type: `string`
  - Constraints: requerido, formato `Basic <base64>`
- **storageScope**
  - Type: `enum(sessionStoragePerTab)`
  - Constraints: requerido
- **isAuthenticated**
  - Type: `boolean`
  - Constraints: requerido

### Validation Rules
- Rutas privadas requieren `isAuthenticated=true`.
- CRUD de empleados requiere rol administrativo derivado de `/api/v1/auth/empleado/me`.
- Si `roles` no esta presente por compatibilidad, la UI aplica fallback por principal autenticado.
- En expiracion o rechazo de sesion, limpiar `authHeader` y volver a login.
- La sesion debe persistir por pestaña en `sessionStorage`.

### State Transitions
- **Anonymous -> Authenticated**: login exitoso.
- **Authenticated -> Anonymous**: logout, cierre de pestaña o `401` de sesion expirada.

## Entity: EmpleadoView

### Description
Representacion de empleado mostrada en tablas y formularios del frontend.

### Fields
- **clave**
  - Type: `string`
  - Constraints: requerido, patron `E-####...`
- **nombre**
  - Type: `string`
  - Constraints: requerido, maximo 100
- **direccion**
  - Type: `string`
  - Constraints: requerido, maximo 100
- **telefono**
  - Type: `string`
  - Constraints: requerido, maximo 100
- **departamentoClave**
  - Type: `string`
  - Constraints: requerido, patron `D-####`
- **version**
  - Type: `number`
  - Constraints: requerido, minimo 0

### Validation Rules
- Alta requiere `nombre`, `direccion`, `telefono`, `departamentoClave`.
- Edicion requiere `version` para concurrencia optimista.
- Operacion de eliminacion definitiva requiere confirmacion explicita de usuario.

### State Transitions
- **Draft -> Persisted**: creacion exitosa.
- **Persisted -> Updated**: edicion exitosa con `version` vigente.
- **Persisted -> Removed**: eliminacion definitiva confirmada.
- **Persisted -> Conflict**: `version` desactualizada en actualizacion.
- **Persisted -> Removed (No-op sync)**: `DELETE` responde `404`; la UI refresca listado y marca aviso informativo.

## Entity: EmpleadoPageView

### Description
Contenedor de paginacion para la vista de listado de empleados.

### Fields
- **content**
  - Type: `EmpleadoView[]`
  - Constraints: requerido
- **page**
  - Type: `number`
  - Constraints: requerido, minimo 0
- **size**
  - Type: `number`
  - Constraints: requerido
- **totalElements**
  - Type: `number`
  - Constraints: requerido, minimo 0
- **totalPages**
  - Type: `number`
  - Constraints: requerido, minimo 0

## Entity: OperationFeedback

### Description
Resultado visible para el usuario tras acciones login/CRUD.

### Fields
- **type**
  - Type: `enum(success, info, warning, error)`
  - Constraints: requerido
- **message**
  - Type: `string`
  - Constraints: requerido
- **fieldErrors**
  - Type: `Record<string,string> | null`
  - Constraints: nullable
- **retryable**
  - Type: `boolean`
  - Constraints: requerido

### Validation Rules
- Para errores de validacion/conflicto se deben mapear mensajes por campo.
- Para errores recuperables se preservan datos del formulario.
- Para `DELETE 404`, mostrar aviso `info` no bloqueante y actualizar listado.

## Relationships

- `AuthSessionView` controla acceso a vistas que usan `EmpleadoPageView` y `EmpleadoView`.
- `LoginFormState` crea/actualiza `AuthSessionView` tras auth exitosa.
- `OperationFeedback` se asocia a login y a cada operacion CRUD para comunicar estado al usuario.
- `RecoverableFormState` conserva `draft` y `feedback` para reintentos sin recaptura completa.
