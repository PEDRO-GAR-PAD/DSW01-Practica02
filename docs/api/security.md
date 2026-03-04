# Seguridad de API

## Autenticación básica

El backend usa HTTP Basic Authentication para todos los endpoints de negocio bajo `/api/empleados`.

Credenciales de desarrollo por defecto:

- Usuario: `admin`
- Contraseña: `admin123`

## Variables de entorno recomendadas

Para no exponer credenciales en código o repositorio, configure:

- `APP_BASIC_USER`
- `APP_BASIC_PASSWORD`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

Ejemplo:

```bash
export APP_BASIC_USER=admin
export APP_BASIC_PASSWORD='admin123'
export DB_URL='jdbc:postgresql://localhost:5432/dsw01_practica02'
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
mvn spring-boot:run
```

## Endpoints públicos permitidos

Solo documentación está permitida sin autenticación:

- `/swagger-ui.html`
- `/swagger-ui/**`
- `/v3/api-docs/**`