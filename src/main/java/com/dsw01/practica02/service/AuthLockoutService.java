package com.dsw01.practica02.service;

import com.dsw01.practica02.domain.CredencialEmpleado;
import com.dsw01.practica02.repository.CredencialEmpleadoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthLockoutService {

    private final CredencialEmpleadoRepository credencialEmpleadoRepository;
    private final int maxFailedAttempts;
    private final int windowMinutes;
    private final int lockoutMinutes;

    public AuthLockoutService(
            CredencialEmpleadoRepository credencialEmpleadoRepository,
            @Value("${app.auth.lockout.max-failed-attempts:5}") int maxFailedAttempts,
            @Value("${app.auth.lockout.window-minutes:15}") int windowMinutes,
            @Value("${app.auth.lockout.duration-minutes:30}") int lockoutMinutes) {
        this.credencialEmpleadoRepository = credencialEmpleadoRepository;
        this.maxFailedAttempts = maxFailedAttempts;
        this.windowMinutes = windowMinutes;
        this.lockoutMinutes = lockoutMinutes;
    }

    public boolean isLocked(CredencialEmpleado credencial) {
        return credencial.getBloqueadaHasta() != null
                && credencial.getBloqueadaHasta().isAfter(LocalDateTime.now());
    }

    @Transactional
    public boolean registerFailedAttempt(CredencialEmpleado credencial) {
        LocalDateTime now = LocalDateTime.now();

        if (credencial.getUltimoIntentoFallidoAt() == null
                || credencial.getUltimoIntentoFallidoAt().isBefore(now.minusMinutes(windowMinutes))) {
            credencial.setIntentosFallidos(0);
        }

        credencial.setIntentosFallidos(credencial.getIntentosFallidos() + 1);
        credencial.setUltimoIntentoFallidoAt(now);
        credencial.setUpdatedAt(now);

        boolean locked = false;
        if (credencial.getIntentosFallidos() >= maxFailedAttempts) {
            credencial.setBloqueadaHasta(now.plusMinutes(lockoutMinutes));
            locked = true;
        }

        credencialEmpleadoRepository.save(credencial);
        return locked;
    }

    @Transactional
    public void registerSuccessfulLogin(CredencialEmpleado credencial) {
        LocalDateTime now = LocalDateTime.now();
        credencial.setIntentosFallidos(0);
        credencial.setUltimoIntentoFallidoAt(null);
        credencial.setBloqueadaHasta(null);
        credencial.setUpdatedAt(now);
        credencialEmpleadoRepository.save(credencial);
    }

    @Transactional
    public void clearSecurityState(CredencialEmpleado credencial) {
        LocalDateTime now = LocalDateTime.now();
        credencial.setIntentosFallidos(0);
        credencial.setUltimoIntentoFallidoAt(null);
        credencial.setBloqueadaHasta(null);
        credencial.setUpdatedAt(now);
        credencialEmpleadoRepository.save(credencial);
    }
}
