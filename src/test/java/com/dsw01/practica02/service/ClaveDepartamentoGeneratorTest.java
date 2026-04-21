package com.dsw01.practica02.service;

import com.dsw01.practica02.exception.DepartamentoKeyCapacityExceededException;
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
class ClaveDepartamentoGeneratorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void initializeSequenceShouldExecuteCreateSequenceStatement() {
        ClaveDepartamentoGenerator generator = new ClaveDepartamentoGenerator(jdbcTemplate);

        generator.initializeSequence();

        verify(jdbcTemplate).execute("CREATE SEQUENCE IF NOT EXISTS departamento_clave_seq START WITH 1 INCREMENT BY 1");
    }

    @Test
    void nextClaveShouldReturnFormattedDepartmentKey() {
        ClaveDepartamentoGenerator generator = new ClaveDepartamentoGenerator(jdbcTemplate);
        when(jdbcTemplate.queryForObject("SELECT nextval('departamento_clave_seq')", Long.class)).thenReturn(42L);

        String nextKey = generator.nextClave();

        assertEquals("D-0042", nextKey);
    }

    @Test
    void nextClaveShouldThrowWhenSequenceValueIsNull() {
        ClaveDepartamentoGenerator generator = new ClaveDepartamentoGenerator(jdbcTemplate);
        when(jdbcTemplate.queryForObject("SELECT nextval('departamento_clave_seq')", Long.class)).thenReturn(null);

        assertThrows(IllegalStateException.class, generator::nextClave);
    }

    @Test
    void nextClaveShouldThrowWhenCapacityExceeded() {
        ClaveDepartamentoGenerator generator = new ClaveDepartamentoGenerator(jdbcTemplate);
        when(jdbcTemplate.queryForObject("SELECT nextval('departamento_clave_seq')", Long.class)).thenReturn(10000L);

        assertThrows(DepartamentoKeyCapacityExceededException.class, generator::nextClave);
    }
}
