package com.dsw01.practica02.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LegacySchemaMigration {

    private final JdbcTemplate jdbcTemplate;

    public LegacySchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void migrateLegacyEmpleadosSchema() {
        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM information_schema.tables
                        WHERE table_schema = 'public'
                          AND table_name = 'empleados'
                    ) THEN
                        IF EXISTS (
                            SELECT 1
                            FROM information_schema.columns
                            WHERE table_schema = 'public'
                              AND table_name = 'empleados'
                              AND column_name = 'departamentoclave'
                        )
                        AND NOT EXISTS (
                            SELECT 1
                            FROM information_schema.columns
                            WHERE table_schema = 'public'
                              AND table_name = 'empleados'
                              AND column_name = 'departamento_clave'
                        ) THEN
                            ALTER TABLE empleados RENAME COLUMN departamentoclave TO departamento_clave;
                        END IF;

                        ALTER TABLE empleados ADD COLUMN IF NOT EXISTS departamento_clave VARCHAR(20);
                        ALTER TABLE empleados ADD COLUMN IF NOT EXISTS version BIGINT;
                        UPDATE empleados SET version = 0 WHERE version IS NULL;
                        ALTER TABLE empleados ALTER COLUMN version SET NOT NULL;
                    END IF;
                END
                $$;
                """);
    }
}
