package com.dsw01.practica02.exception;

public class CredencialEmailConflictException extends RuntimeException {

    public CredencialEmailConflictException(String email) {
        super("El email ya existe: " + email);
    }
}
