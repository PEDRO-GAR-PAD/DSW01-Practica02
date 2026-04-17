package com.dsw01.practica02.exception;

public class DepartamentoReferencedException extends RuntimeException {

    public DepartamentoReferencedException(String clave) {
        super("No se puede eliminar el departamento " + clave + " porque tiene empleados asociados");
    }
}
