package com.dsw01.practica02.exception;

public class CredencialUsernameConflictException extends RuntimeException {

    public CredencialUsernameConflictException(String username) {
        super("El username ya existe: " + username);
    }
}
