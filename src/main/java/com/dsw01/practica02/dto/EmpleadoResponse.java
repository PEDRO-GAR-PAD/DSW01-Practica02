package com.dsw01.practica02.dto;

public record EmpleadoResponse(
        String clave,
        String nombre,
        String direccion,
        String telefono
) {
}