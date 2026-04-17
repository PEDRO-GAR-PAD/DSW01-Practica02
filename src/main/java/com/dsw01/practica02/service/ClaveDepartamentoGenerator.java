package com.dsw01.practica02.service;

import com.dsw01.practica02.exception.DepartamentoKeyCapacityExceededException;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClaveDepartamentoGenerator {

    private static final long MAX_SEQUENCE = 9999L;
    private final JdbcTemplate jdbcTemplate;

    public ClaveDepartamentoGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initializeSequence() {
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS departamento_clave_seq START WITH 1 INCREMENT BY 1");
    }

    public String nextClave() {
        Long nextValue = jdbcTemplate.queryForObject("SELECT nextval('departamento_clave_seq')", Long.class);
        if (nextValue == null) {
            throw new IllegalStateException("No se pudo obtener el siguiente valor para la clave de departamento");
        }
        if (nextValue > MAX_SEQUENCE) {
            throw new DepartamentoKeyCapacityExceededException();
        }
        return "D-" + String.format("%04d", nextValue);
    }
}
