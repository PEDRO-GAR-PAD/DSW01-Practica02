package com.dsw01.practica02.exception;

public class CredencialEmpleadoNotFoundException extends RuntimeException {

    public CredencialEmpleadoNotFoundException(String empleadoClave) {
        super("Credenciales no encontradas para empleado " + empleadoClave);
    }
}
