package com.dsw01.practica02.dto;

import java.util.List;

public record EmpleadoAuthMeResponse(
        String empleadoClave,
        String username,
        String nombre,
        String authStatus,
        List<String> roles
) {
}
