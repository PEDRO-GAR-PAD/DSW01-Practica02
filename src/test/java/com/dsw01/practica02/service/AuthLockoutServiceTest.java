package com.dsw01.practica02.service;

import com.dsw01.practica02.domain.CredencialEmpleado;
import com.dsw01.practica02.repository.CredencialEmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthLockoutServiceTest {

    @Mock
    private CredencialEmpleadoRepository repository;

    @Test
    void isLockedShouldReturnTrueWhenLockedUntilFutureDate() {
        AuthLockoutService service = new AuthLockoutService(repository, 3, 15, 30);
        CredencialEmpleado credencial = new CredencialEmpleado();
        credencial.setBloqueadaHasta(LocalDateTime.now().plusMinutes(5));

        assertTrue(service.isLocked(credencial));
    }

    @Test
    void registerFailedAttemptShouldLockWhenThresholdReached() {
        AuthLockoutService service = new AuthLockoutService(repository, 3, 15, 30);
        CredencialEmpleado credencial = new CredencialEmpleado();
        credencial.setIntentosFallidos(2);
        credencial.setUltimoIntentoFallidoAt(LocalDateTime.now().minusMinutes(1));

        boolean locked = service.registerFailedAttempt(credencial);

        assertTrue(locked);
        assertTrue(credencial.getIntentosFallidos() >= 3);
        assertNotNull(credencial.getBloqueadaHasta());
        assertNotNull(credencial.getUpdatedAt());
        verify(repository).save(credencial);
    }

    @Test
    void registerFailedAttemptShouldResetCounterWhenWindowExpired() {
        AuthLockoutService service = new AuthLockoutService(repository, 3, 15, 30);
        CredencialEmpleado credencial = new CredencialEmpleado();
        credencial.setIntentosFallidos(2);
        credencial.setUltimoIntentoFallidoAt(LocalDateTime.now().minusMinutes(16));

        boolean locked = service.registerFailedAttempt(credencial);

        assertFalse(locked);
        assertTrue(credencial.getIntentosFallidos() == 1);
        verify(repository).save(credencial);
    }

    @Test
    void registerSuccessfulLoginShouldClearSecurityState() {
        AuthLockoutService service = new AuthLockoutService(repository, 3, 15, 30);
        CredencialEmpleado credencial = new CredencialEmpleado();
        credencial.setIntentosFallidos(2);
        credencial.setUltimoIntentoFallidoAt(LocalDateTime.now().minusMinutes(1));
        credencial.setBloqueadaHasta(LocalDateTime.now().plusMinutes(10));

        service.registerSuccessfulLogin(credencial);

        assertTrue(credencial.getIntentosFallidos() == 0);
        assertNull(credencial.getUltimoIntentoFallidoAt());
        assertNull(credencial.getBloqueadaHasta());
        assertNotNull(credencial.getUpdatedAt());
        verify(repository).save(credencial);
    }
}
