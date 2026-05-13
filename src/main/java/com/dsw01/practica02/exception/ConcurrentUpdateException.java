package com.dsw01.practica02.exception;

public class ConcurrentUpdateException extends RuntimeException {

    public ConcurrentUpdateException(String clave) {
        super("Conflicto de concurrencia al actualizar empleado " + clave);
    }
}
