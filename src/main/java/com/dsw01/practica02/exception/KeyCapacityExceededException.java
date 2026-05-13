package com.dsw01.practica02.exception;

public class KeyCapacityExceededException extends RuntimeException {

    public KeyCapacityExceededException() {
        super("Capacidad agotada: no se pueden generar más claves después de E-9999");
    }
}