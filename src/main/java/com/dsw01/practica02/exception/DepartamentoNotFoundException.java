package com.dsw01.practica02.exception;

public class DepartamentoNotFoundException extends RuntimeException {

    public DepartamentoNotFoundException(String clave) {
        super("Departamento con clave " + clave + " no existe");
    }
}
