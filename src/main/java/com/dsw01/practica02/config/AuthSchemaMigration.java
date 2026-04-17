package com.dsw01.practica02.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthSchemaMigration {

    private final JdbcTemplate jdbcTemplate;

    public AuthSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void migrateAuthSchema() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS credenciales_empleado (
                    empleado_clave VARCHAR(20) PRIMARY KEY,
                    username VARCHAR(60) NOT NULL UNIQUE,
                    email VARCHAR(255) NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    activa BOOLEAN NOT NULL DEFAULT TRUE,
                    intentos_fallidos INTEGER NOT NULL DEFAULT 0,
                    ultimo_intento_fallido_at TIMESTAMP NULL,
                    bloqueada_hasta TIMESTAMP NULL,
                    password_updated_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL,
                    version BIGINT NOT NULL DEFAULT 0
                )
                """);

            jdbcTemplate.execute("ALTER TABLE credenciales_empleado ADD COLUMN IF NOT EXISTS email VARCHAR(255)");
            jdbcTemplate.execute("""
                UPDATE credenciales_empleado
                SET email = LOWER(username) || '@local.invalid'
                WHERE email IS NULL OR BTRIM(email) = ''
                """);
            jdbcTemplate.execute("ALTER TABLE credenciales_empleado ALTER COLUMN email SET NOT NULL");
            jdbcTemplate.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS uq_credenciales_empleado_username_ci
                ON credenciales_empleado (LOWER(username))
                """);
            jdbcTemplate.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS uq_credenciales_empleado_email_ci
                ON credenciales_empleado (LOWER(email))
                """);
            jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                    SELECT 1 FROM pg_constraint WHERE conname = 'fk_credenciales_empleado_empleado'
                    ) THEN
                    ALTER TABLE credenciales_empleado
                    ADD CONSTRAINT fk_credenciales_empleado_empleado
                    FOREIGN KEY (empleado_clave) REFERENCES empleados(clave) ON DELETE CASCADE;
                    END IF;
                END
                $$;
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS eventos_autenticacion (
                    id BIGSERIAL PRIMARY KEY,
                    empleado_clave VARCHAR(20) NOT NULL,
                    username_snapshot VARCHAR(60) NOT NULL,
                    tipo_evento VARCHAR(40) NOT NULL,
                    resultado VARCHAR(20) NOT NULL,
                    ip_origen VARCHAR(45) NULL,
                    detalle VARCHAR(255) NULL,
                    created_at TIMESTAMP NOT NULL
                )
                """);
        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1 FROM pg_constraint WHERE conname = 'fk_eventos_autenticacion_empleado'
                    ) THEN
                        ALTER TABLE eventos_autenticacion
                        ADD CONSTRAINT fk_eventos_autenticacion_empleado
                        FOREIGN KEY (empleado_clave) REFERENCES empleados(clave) ON DELETE CASCADE;
                    END IF;
                END
                $$;
                """);
    }
}
