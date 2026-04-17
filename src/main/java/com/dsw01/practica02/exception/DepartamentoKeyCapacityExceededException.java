package com.dsw01.practica02.exception;

public class DepartamentoKeyCapacityExceededException extends RuntimeException {

    public DepartamentoKeyCapacityExceededException() {
        super("Capacidad agotada: no se pueden generar mas claves despues de D-9999");
    }
}
