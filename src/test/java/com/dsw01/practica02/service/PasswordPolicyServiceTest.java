package com.dsw01.practica02.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordPolicyServiceTest {

    @Test
    void validatePasswordShouldAcceptStrongPassword() {
        PasswordPolicyService service = new PasswordPolicyService(12, 128);

        assertDoesNotThrow(() -> service.validatePassword("A_secure_pass_2026"));
    }

    @Test
    void validatePasswordShouldRejectBlankPassword() {
        PasswordPolicyService service = new PasswordPolicyService(12, 128);

        assertThrows(IllegalArgumentException.class, () -> service.validatePassword("   "));
    }

    @Test
    void validatePasswordShouldRejectTooShortPassword() {
        PasswordPolicyService service = new PasswordPolicyService(12, 128);

        assertThrows(IllegalArgumentException.class, () -> service.validatePassword("short123"));
    }

    @Test
    void validatePasswordShouldRejectDenylistedPasswordIgnoringCase() {
        PasswordPolicyService service = new PasswordPolicyService(8, 128);

        assertThrows(IllegalArgumentException.class, () -> service.validatePassword("  PassWord123  "));
    }
}
