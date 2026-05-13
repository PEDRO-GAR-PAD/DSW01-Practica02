# Quickstart - Frontend CRUD Empleados y Login

## Prerequisitos

- Java 17
- Maven 3.9+
- Node.js 20+
- npm 10+
- Docker y Docker Compose

## 1) Levantar backend y base de datos

Desde la raiz del repositorio:

```bash
DB_HOST_PORT=5433 APP_HOST_PORT=8080 docker compose -f docker/docker-compose.yml up -d --build
```

Verificar backend activo:

```bash
curl -s http://localhost:8080/v3/api-docs | head
```

## 2) Preparar frontend Angular

```bash
cd frontend
npm install
```

Configurar URL de API en entorno de desarrollo del frontend:

- `apiBaseUrl = http://localhost:8080`

Iniciar frontend:

```bash
npm run start
```

Abrir en navegador:

- `http://localhost:4200`

## 3) Validar login por usuario o email/password (US1)

1. Ir a pantalla de login.
2. Ingresar `usuario o email` y `password` validos.
3. Usar control `Ver/Ocultar` password y confirmar que no se pierde el valor.
3. Confirmar redireccion a vista privada principal.
4. Repetir con credenciales invalidas y verificar mensaje generico sin filtrar causa.

Resultado esperado:

- Login valido: acceso permitido.
- Login invalido: rechazo controlado.

Validacion adicional:

- Confirmar que autenticacion se realiza con principal flexible (`username` o `email`) sin traduccion en cliente.

## 4) Validar cierre de sesion y proteccion de rutas (US1)

1. Con sesion activa, ejecutar logout.
2. Intentar abrir una ruta privada directamente.

Resultado esperado:

- Sesion finalizada.
- Redireccion a login para rutas protegidas.

Validacion adicional de persistencia:

- Recargar la pestaña actual y verificar que la sesion permanece activa.
- Abrir nueva pestaña sin sesion previa y verificar que solicita login.
- Cerrar pestaña con sesion y validar que al reabrir requiere login (scope `sessionStorage`).

## 5) Validar CRUD de empleados (US2)

1. Ingresar como usuario `master` (rol administrativo).
2. Crear un empleado con datos validos.
3. Verificar que aparece en listado.
4. Editar datos del empleado y guardar.
5. Eliminar empleado definitivamente con confirmacion.

Resultado esperado:

- Operaciones CRUD reflejadas correctamente en UI y listado.
- Acciones CRUD visibles solo cuando `/api/v1/auth/empleado/me` retorna rol/permiso administrativo.

## 5.1) Validar menu simple para empleado

1. Ingresar con una cuenta `EMPLEADO` no administrativa.
2. Verificar dashboard y menu.

Resultado esperado:

- La sesion inicia correctamente.
- Se muestra menu basico de sesion iniciada.
- No se muestran acciones/rutas CRUD de empleados.

## 6) Validar errores recuperables y concurrencia (US3)

1. Forzar error de validacion en formulario (campo requerido vacio).
2. Forzar conflicto de concurrencia en actualizacion (version desactualizada).
3. Simular fallo temporal de API durante guardado.
4. Forzar `DELETE` sobre empleado ya eliminado para obtener `404`.

Resultado esperado:

- Mensajes claros por tipo de error.
- Datos del formulario se conservan para reintento en errores recuperables.
- Usuario puede reintentar sin recapturar todo.
- En `DELETE 404`, mostrar aviso informativo no bloqueante y refrescar listado.

## 7) Validar criterios de exito

- SC-001: medir que login exitoso se complete en menos de 60 segundos en >=95% de pruebas.
- SC-002: medir que CRUD valido complete al primer intento en >=90% de casos.
- SC-003: verificar rechazo total de credenciales invalidas sin acceso a vistas privadas.
- SC-004: verificar conservacion de datos en errores recuperables.
- SC-005: medir refresco visible de listado tras CRUD en menos de 3 segundos en >=95% de casos.

## 8) Evidencia de validacion end-to-end

Ejecucion de referencia (2026-03-12):

- Build frontend Angular: `npm run build` en `frontend/`.
- Resultado: compilacion exitosa y bundle generado en `frontend/dist/frontend`.
- Flujos funcionales listos para validacion manual sobre UI implementada:
  - Login con credenciales validas e invalidas.
  - Persistencia de sesion por pestaña (sessionStorage) y logout.
  - CRUD de empleados (crear, editar, eliminar) con feedback visual.
  - Recuperacion ante errores `401`, `409` y `DELETE 404` como no-op informativo.

## 9) Matriz FR/SC con evidencia

| Item | Evidencia de implementacion |
|------|-----------------------------|
| FR-005, FR-006 | Rutas protegidas y guardas en `frontend/src/app/app.routes.ts` y `frontend/src/app/core/auth/auth.guards.ts` |
| FR-011 | Estado de submit y bloqueo de doble envio en `frontend/src/app/features/auth/pages/login-page/login-page.component.ts` |
| FR-012, FR-013 | Preservacion de formularios recuperables en `empleado-create.component.ts` y `empleado-edit.component.ts` |
| FR-017, FR-019 | Hard delete + `404` no-op informativo en `empleados-list.component.ts` |
| SC-001 | Flujo de login instrumentado para validacion manual en pasos 3 y 7 |
| SC-002 | Flujo CRUD completo descrito en pasos 5 y 7 |
| SC-003 | Validacion de rechazo de credenciales invalidas en paso 3 |
| SC-004 | Conservacion de datos en errores recuperables en paso 6 |
| SC-005 | Validacion de refresco de listado en pasos 5, 6 y 7 |
