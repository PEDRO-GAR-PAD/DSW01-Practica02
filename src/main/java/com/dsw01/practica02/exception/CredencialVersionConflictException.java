package com.dsw01.practica02.exception;

public class CredencialVersionConflictException extends RuntimeException {

    public CredencialVersionConflictException(String empleadoClave) {
        super("Conflicto de version en credenciales para empleado " + empleadoClave);
    }
}
