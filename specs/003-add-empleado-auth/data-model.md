# Data Model - Autenticacion Por Empleado

## Entity: CredencialEmpleado

### Description
Representa las credenciales de acceso asociadas de forma unica a un empleado.

### Fields
- **empleadoClave**
  - Type: `VARCHAR(20)`
  - Constraints: primary key, foreign key a `empleados.clave`, not null
- **username**
  - Type: `VARCHAR(60)`
  - Constraints: unique, not null
- **email**
  - Type: `VARCHAR(255)`
  - Constraints: not null, unique global
  - Notes: formato email valido, visible en respuestas administrativas
- **passwordHash**
  - Type: `VARCHAR(255)`
  - Constraints: not null
  - Notes: hash Argon2id, nunca se expone en respuestas
- **activa**
  - Type: `BOOLEAN`
  - Constraints: not null, default `true`
- **intentosFallidos**
  - Type: `INTEGER`
  - Constraints: not null, default `0`, min `0`
- **ultimoIntentoFallidoAt**
  - Type: `TIMESTAMP`
  - Constraints: nullable
- **bloqueadaHasta**
  - Type: `TIMESTAMP`
  - Constraints: nullable
- **passwordUpdatedAt**
  - Type: `TIMESTAMP`
  - Constraints: not null
- **updatedAt**
  - Type: `TIMESTAMP`
  - Constraints: not null
- **version**
  - Type: `BIGINT`
  - Constraints: not null, optimistic locking

### Validation Rules
- `username` es obligatorio, unico y se valida en formato permitido por la organizacion.
- `email` es obligatorio en alta, se valida formato de correo y longitud maxima 255.
- `email` debe ser unico global en todas las credenciales (activas e inactivas).
- En autenticacion se acepta principal por `username` o `email`.
- `password` de entrada es obligatoria en alta/cambio, minima 12 caracteres.
- No se persiste ni retorna contrasena en texto plano.
- Si `activa=false`, la autenticacion debe rechazarse.
- Si `bloqueadaHasta > now()`, la autenticacion debe rechazarse por bloqueo temporal.

### State Transitions
- **Alta credencial**: crea registro activo con contador de fallos en `0`.
- **Fallo de autenticacion**: incrementa `intentosFallidos` y actualiza `ultimoIntentoFallidoAt`.
- **Bloqueo**: al alcanzar 5 fallos dentro de 15 minutos, define `bloqueadaHasta = now()+30min`.
- **Autenticacion exitosa**: reinicia `intentosFallidos`, limpia `bloqueadaHasta` y registra evento.
- **Cambio de contrasena**: actualiza `passwordHash`, `passwordUpdatedAt`, limpia bloqueo/fallos.
- **Desactivacion**: cambia `activa=false` y rechaza autenticaciones futuras.

## Entity: EventoAutenticacion

### Description
Bitacora auditable de eventos relacionados con autenticacion y gestion de credenciales.

### Fields
- **id**
  - Type: `BIGSERIAL`
  - Constraints: primary key
- **empleadoClave**
  - Type: `VARCHAR(20)`
  - Constraints: foreign key a `empleados.clave`, not null
- **usernameSnapshot**
  - Type: `VARCHAR(60)`
  - Constraints: not null
- **tipoEvento**
  - Type: `VARCHAR(40)`
  - Constraints: not null
  - Allowed values: `LOGIN_SUCCESS`, `LOGIN_FAILED`, `ACCOUNT_LOCKED`, `PASSWORD_CHANGED`, `CREDENTIAL_DISABLED`, `CREDENTIAL_ENABLED`
- **resultado**
  - Type: `VARCHAR(20)`
  - Constraints: not null
  - Allowed values: `SUCCESS`, `FAILURE`
- **ipOrigen**
  - Type: `VARCHAR(45)`
  - Constraints: nullable
- **detalle**
  - Type: `VARCHAR(255)`
  - Constraints: nullable
- **createdAt**
  - Type: `TIMESTAMP`
  - Constraints: not null

### Validation Rules
- Todo intento de autenticacion debe producir un evento.
- Todo cambio administrativo de credenciales debe producir un evento.
- El evento no debe almacenar contrasena ni hash.

## Read Model: CredencialEmpleadoResponse

### Fields
- `empleadoClave`
- `username`
- `email`
- `activa`
- `intentosFallidos`
- `bloqueadaHasta`
- `passwordUpdatedAt`
- `updatedAt`
- `version`

### Rules
- Nunca incluir `passwordHash` ni contrasena.

## Write Model: CredencialEmpleadoCreateRequest

### Fields
- `username` (string, requerido, unico)
- `email` (string, requerido, formato email, maximo 255)
- `password` (string, requerido, minimo 12)

## Write Model: CredencialEmpleadoUpdateRequest

### Fields
- `username` (string, opcional)
- `email` (string, opcional, formato email, maximo 255)
- `password` (string, opcional, minimo 12)
- `version` (integer, requerido, minimo 0)

### Rules
- Debe enviarse al menos un campo para actualizar.

## Write Model: CredencialEmpleadoEstadoRequest

### Fields
- `activa` (boolean, requerido)
- `version` (integer, requerido, minimo 0)

## Relationships

- `Empleado (1) <-> (0..1) CredencialEmpleado`.
- `Empleado (1) <-> (N) EventoAutenticacion`.

## Read Model: EmpleadoAuthMeResponse

### Fields
- `empleadoClave` (string, `E-xxxx` para empleado o `MASTER` para bootstrap)
- `username` (string)
- `nombre` (string)
- `authStatus` (enum: `AUTHENTICATED`)
- `roles` (string[], por ejemplo `ROLE_EMPLEADO`, `ROLE_ADMIN`)

### Rules
- Debe devolver roles para que frontend determine capacidades de UI.
- La cuenta bootstrap devuelve identidad `MASTER` y rol administrativo.
