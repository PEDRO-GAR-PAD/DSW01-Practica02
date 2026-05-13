package com.dsw01.practica02.dto;

import jakarta.validation.constraints.NotNull;

public record CredencialEmpleadoEstadoRequest(
        @NotNull(message = "activa es obligatoria")
        Boolean activa,

        @NotNull(message = "version es obligatoria")
        Long version
) {
}
