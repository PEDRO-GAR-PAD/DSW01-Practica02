package com.dsw01.practica02.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CredencialEmpleadoCreateRequest(
        @NotBlank(message = "username es obligatorio")
        @Size(min = 3, max = 60, message = "username debe tener entre 3 y 60 caracteres")
        String username,

        @NotBlank(message = "email es obligatorio")
        @Email(message = "email debe tener formato valido")
        @Size(max = 255, message = "email no puede superar 255 caracteres")
        String email,

        @NotBlank(message = "password es obligatoria")
        @Size(min = 12, max = 128, message = "password debe tener entre 12 y 128 caracteres")
        String password
) {
}
