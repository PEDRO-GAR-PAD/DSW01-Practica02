package com.dsw01.practica02.dto;

import java.time.LocalDateTime;

public record CredencialEmpleadoResponse(
        String empleadoClave,
        String username,
        String email,
        boolean activa,
        int intentosFallidos,
        LocalDateTime bloqueadaHasta,
        LocalDateTime passwordUpdatedAt,
        LocalDateTime updatedAt,
        Long version
) {
}
