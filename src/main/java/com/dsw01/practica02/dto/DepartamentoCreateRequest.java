package com.dsw01.practica02.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record DepartamentoCreateRequest(
        @NotBlank(message = "nombre es obligatorio")
        @Size(max = 100, message = "nombre debe tener maximo 100 caracteres")
        String nombre,

        @Null(message = "clave no debe enviarse en la creacion")
        String clave
) {
}
