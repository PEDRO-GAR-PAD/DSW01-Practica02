# Frontend Angular

Workspace frontend para login y CRUD de empleados.

## Requisitos

- Node.js 20+
- npm 10+
- Backend Spring Boot ejecutandose en `http://localhost:8080`

## Arranque rapido

```bash
cd frontend
npm install
npm start
```

Aplicacion disponible en `http://localhost:4200`.

## Build de produccion

```bash
cd frontend
npm run build
```

Artefactos generados en `frontend/dist/frontend`.

## Configuracion de API

La URL base del backend se define en:

- `frontend/src/environments/environment.ts`
- `frontend/src/environments/environment.prod.ts`

Variable actual: `apiBaseUrl = http://localhost:8080`.
