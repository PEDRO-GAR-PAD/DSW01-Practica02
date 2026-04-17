package com.dsw01.practica02.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PasswordPolicyService {

    private final int minLength;
    private final int maxLength;

    private static final Set<String> DENYLIST = Set.of(
            "password",
            "password123",
            "123456",
            "123456789",
            "qwerty",
            "admin123",
            "letmein",
            "welcome",
            "iloveyou"
    );

    public PasswordPolicyService(
            @Value("${app.auth.password.min-length:12}") int minLength,
            @Value("${app.auth.password.max-length:128}") int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password es obligatoria");
        }

        if (password.length() < minLength || password.length() > maxLength) {
            throw new IllegalArgumentException(
                    "password debe tener entre " + minLength + " y " + maxLength + " caracteres");
        }

        if (DENYLIST.contains(password.trim().toLowerCase())) {
            throw new IllegalArgumentException("password no permitida por politica de seguridad");
        }
    }
}
