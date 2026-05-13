# Phase 0 Research - Autenticacion Por Empleado

## Decision 1: Mecanismo de autenticacion para empleados
- **Decision**: Usar HTTP Basic con credenciales por empleado cargadas desde base de datos en lugar de un login token separado.
- **Rationale**: Cumple la constitucion (Basic obligatorio en endpoints expuestos) y evita introducir otro protocolo de sesion.
- **Alternatives considered**:
  - Endpoint dedicado de login con bearer token: descartado por romper el contrato de seguridad vigente del proyecto.
  - Mantener un unico usuario tecnico compartido: descartado porque no cumple el requerimiento de credenciales por empleado.

## Decision 2: Modelo de persistencia de credenciales
- **Decision**: Crear entidad `CredencialEmpleado` separada y relacionada 1:1 con `Empleado` mediante `empleadoClave`.
- **Rationale**: Separa datos de negocio del empleado respecto de datos sensibles de acceso y simplifica la gestion de ciclo de vida de credenciales.
- **Alternatives considered**:
  - Incrustar usuario/hash en tabla `empleados`: descartado por acoplar seguridad y dominio de negocio en la misma entidad.

## Decision 3: Campo email en credenciales
- **Decision**: Agregar `email` a `CredencialEmpleado` con tipo `VARCHAR(255)` y validacion de formato en operaciones de alta/actualizacion.
- **Rationale**: Alinea el diseño con FR-017/FR-018 y permite exponer un identificador de contacto no sensible en respuestas de administracion.
- **Alternatives considered**:
  - Mantener email fuera del agregado de credenciales: descartado por duplicar validaciones y dificultar consistencia de contratos.
  - Almacenar email sin validacion de formato: descartado por riesgo de datos inconsistentes.

## Decision 4: Hash de contrasena
- **Decision**: Almacenar solo hash de contrasena usando `Argon2id` como estrategia principal.
- **Rationale**: Es una opcion moderna resistente a ataques de fuerza bruta en GPU y no requiere almacenar secretos reversibles.
- **Alternatives considered**:
  - BCrypt: viable y compatible, pero se prioriza Argon2id por robustez actual.
  - Cifrado reversible: descartado por ser anti-patron para almacenamiento de contrasenas.

## Decision 5: Politica minima de contrasena
- **Decision**: Definir longitud minima de 12 caracteres y bloqueo de contrasenas triviales conocidas por lista de denegacion interna.
- **Rationale**: Eleva entropia de credenciales sin imponer reglas excesivas de composicion que afectan usabilidad.
- **Alternatives considered**:
  - Minimo de 8 con reglas de simbolos obligatorios: descartado por menor seguridad global y peor experiencia de usuario.

## Decision 6: Bloqueo temporal por intentos fallidos
- **Decision**: Aplicar bloqueo por 30 minutos al alcanzar 5 intentos fallidos en ventana de 15 minutos.
- **Rationale**: Resuelve el requisito funcional de bloqueo temporal y establece un valor operativo concreto para implementar y probar.
- **Alternatives considered**:
  - Bloqueo permanente hasta accion de admin: descartado por impacto operativo y soporte.
  - Solo rate limit por IP: descartado porque el requerimiento esta centrado en cuenta de empleado.

## Decision 7: Reseteo de contadores de seguridad
- **Decision**: Reiniciar intentos fallidos y estado de bloqueo tras autenticacion exitosa o cambio administrativo de contrasena.
- **Rationale**: Reduce falsos bloqueos persistentes y alinea el estado de la cuenta con acciones legitimas del usuario/admin.
- **Alternatives considered**:
  - Mantener contador historico tras exito: descartado por penalizar acceso legitimo futuro.

## Decision 8: Auditoria de autenticacion
- **Decision**: Registrar eventos en `EventoAutenticacion` para exitos, fallos, bloqueo, desbloqueo y cambio de credenciales.
- **Rationale**: Cumple trazabilidad requerida y facilita soporte/incidentes con evidencia persistente.
- **Alternatives considered**:
  - Logging solo en consola: descartado por no garantizar persistencia ni consulta confiable.

## Decision 9: Operacion administrativa de credenciales
- **Decision**: Exponer endpoints administrativos para crear/actualizar/desactivar credenciales por empleado, sin devolver hash ni contrasena.
- **Rationale**: Permite gestionar ciclo de vida de acceso y mantiene secreto de credenciales fuera de respuestas API.
- **Alternatives considered**:
  - Gestion manual directa en base de datos: descartada por alto riesgo operativo y falta de trazabilidad funcional.

## Decision 10: Unicidad global de email
- **Decision**: En credenciales de empleado, `email` debe ser unico global incluso para cuentas inactivas.
- **Rationale**: Evita ambiguedades operativas y conflictos de identificacion al reactivar cuentas o auditar eventos historicos.
- **Alternatives considered**:
  - Unicidad solo entre cuentas activas: descartada por permitir reutilizacion riesgosa del mismo email.
  - Sin unicidad de email: descartada por inconsistencia de datos y mayor probabilidad de errores administrativos.

  ## Decision 11: Cuenta bootstrap master y aliases
  - **Decision**: Definir `master` como cuenta bootstrap administrativa por defecto, con aliases configurables (por ejemplo `admin`) para compatibilidad operativa.
  - **Rationale**: Permite control administrativo total y transicion segura desde credenciales historicas de administracion sin romper entornos existentes.
  - **Alternatives considered**:
    - Mantener solo `admin` fijo: descartado por menor claridad semantica de rol global.
    - Crear entidad de admin separada en base de datos: descartado por alcance adicional innecesario.

  ## Decision 12: Principal flexible en login de empleado
  - **Decision**: Aceptar autenticacion de empleado por `username` o `email` como principal en HTTP Basic.
  - **Rationale**: Reduce friccion de login en frontend y evita errores por divergencia entre identificadores de usuario.
  - **Alternatives considered**:
    - Solo `username`: descartado por peor UX cuando el formulario usa email.
    - Solo `email`: descartado por romper compatibilidad con credenciales existentes basadas en username.

  ## Decision 13: CORS para frontend local
  - **Decision**: Habilitar CORS configurable y aceptar preflight `OPTIONS` para origenes locales de Angular (`localhost:4200`, `127.0.0.1:4200` por defecto).
  - **Rationale**: Evita bloqueos de navegador en desarrollo y mantiene gobernanza por configuracion de entorno.
  - **Alternatives considered**:
    - CORS wildcard global: descartado por riesgo de seguridad.
    - Deshabilitar CORS y usar proxy obligatorio: descartado por complejidad operativa adicional.

## Clarification Status

No quedan items `NEEDS CLARIFICATION` para continuar a Fase 1 de diseno.
