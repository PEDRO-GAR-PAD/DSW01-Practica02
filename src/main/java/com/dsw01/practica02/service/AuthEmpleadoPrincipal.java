package com.dsw01.practica02.service;

public record AuthEmpleadoPrincipal(
        String empleadoClave,
        String username,
        String nombre,
        boolean bootstrapAdmin
) {
}
