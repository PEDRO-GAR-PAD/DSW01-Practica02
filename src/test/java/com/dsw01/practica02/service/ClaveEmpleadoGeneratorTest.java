package com.dsw01.practica02.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaveEmpleadoGeneratorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void initializeSequenceShouldExecuteCreateSequenceStatement() {
        ClaveEmpleadoGenerator generator = new ClaveEmpleadoGenerator(jdbcTemplate);

        generator.initializeSequence();

        verify(jdbcTemplate).execute("CREATE SEQUENCE IF NOT EXISTS empleado_clave_seq START WITH 1 INCREMENT BY 1");
    }

    @Test
    void nextClaveShouldReturnFormattedEmployeeKey() {
        ClaveEmpleadoGenerator generator = new ClaveEmpleadoGenerator(jdbcTemplate);
        when(jdbcTemplate.queryForObject("SELECT nextval('empleado_clave_seq')", Long.class)).thenReturn(7L);

        String nextKey = generator.nextClave();

        assertEquals("E-0007", nextKey);
    }

    @Test
    void nextClaveShouldThrowWhenSequenceValueIsNull() {
        ClaveEmpleadoGenerator generator = new ClaveEmpleadoGenerator(jdbcTemplate);
        when(jdbcTemplate.queryForObject("SELECT nextval('empleado_clave_seq')", Long.class)).thenReturn(null);

        assertThrows(IllegalStateException.class, generator::nextClave);
    }
}
