package com.dsw01.practica02.exception;

public class EmpleadoNotFoundException extends RuntimeException {

    public EmpleadoNotFoundException(String clave) {
        super("Empleado con clave " + clave + " no existe");
    }
}