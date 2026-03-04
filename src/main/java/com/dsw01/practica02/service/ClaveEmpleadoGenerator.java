package com.dsw01.practica02.service;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClaveEmpleadoGenerator {

    private final JdbcTemplate jdbcTemplate;

    public ClaveEmpleadoGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initializeSequence() {
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS empleado_clave_seq START WITH 1 INCREMENT BY 1");
    }

    public String nextClave() {
        Long nextValue = jdbcTemplate.queryForObject("SELECT nextval('empleado_clave_seq')", Long.class);
        if (nextValue == null) {
            throw new IllegalStateException("No se pudo obtener el siguiente valor para la clave de empleado");
        }
        return "E-" + String.format("%04d", nextValue);
    }
}