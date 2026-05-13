package com.dsw01.practica02.service;

import com.dsw01.practica02.domain.CredencialEmpleado;
import com.dsw01.practica02.dto.CredencialEmpleadoCreateRequest;
import com.dsw01.practica02.dto.CredencialEmpleadoEstadoRequest;
import com.dsw01.practica02.dto.CredencialEmpleadoResponse;
import com.dsw01.practica02.dto.CredencialEmpleadoUpdateRequest;
import com.dsw01.practica02.exception.CredencialEmailConflictException;
import com.dsw01.practica02.exception.CredencialEmpleadoNotFoundException;
import com.dsw01.practica02.exception.CredencialUsernameConflictException;
import com.dsw01.practica02.exception.CredencialVersionConflictException;
import com.dsw01.practica02.exception.EmpleadoNotFoundException;
import com.dsw01.practica02.mapper.CredencialEmpleadoMapper;
import com.dsw01.practica02.repository.CredencialEmpleadoRepository;
import com.dsw01.practica02.repository.EmpleadoRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CredencialEmpleadoService {

    private final CredencialEmpleadoRepository credencialEmpleadoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordPolicyService passwordPolicyService;
    private final PasswordEncoder passwordEncoder;
    private final EventoAutenticacionService eventoAutenticacionService;
    private final CredencialEmpleadoMapper credencialEmpleadoMapper;

    public CredencialEmpleadoService(CredencialEmpleadoRepository credencialEmpleadoRepository,
                                     EmpleadoRepository empleadoRepository,
                                     PasswordPolicyService passwordPolicyService,
                                     PasswordEncoder passwordEncoder,
                                     EventoAutenticacionService eventoAutenticacionService,
                                     CredencialEmpleadoMapper credencialEmpleadoMapper) {
        this.credencialEmpleadoRepository = credencialEmpleadoRepository;
        this.empleadoRepository = empleadoRepository;
        this.passwordPolicyService = passwordPolicyService;
        this.passwordEncoder = passwordEncoder;
        this.eventoAutenticacionService = eventoAutenticacionService;
        this.credencialEmpleadoMapper = credencialEmpleadoMapper;
    }

    @Transactional
    public CredencialEmpleadoResponse crear(String empleadoClave, CredencialEmpleadoCreateRequest request) {
        assertEmpleadoExiste(empleadoClave);

        if (credencialEmpleadoRepository.findByEmpleadoClave(empleadoClave).isPresent()) {
            throw new CredencialUsernameConflictException("empleado:" + empleadoClave);
        }

        String normalizedUsername = normalizeUsername(request.username());
        if (credencialEmpleadoRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new CredencialUsernameConflictException(normalizedUsername);
        }

        String normalizedEmail = normalizeEmail(request.email());
        if (credencialEmpleadoRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new CredencialEmailConflictException(normalizedEmail);
        }

        passwordPolicyService.validatePassword(request.password());

        LocalDateTime now = LocalDateTime.now();
        CredencialEmpleado credencial = new CredencialEmpleado();
        credencial.setEmpleadoClave(empleadoClave);
        credencial.setUsername(normalizedUsername);
        credencial.setEmail(normalizedEmail);
        credencial.setPasswordHash(passwordEncoder.encode(request.password()));
        credencial.setActiva(true);
        credencial.setIntentosFallidos(0);
        credencial.setUltimoIntentoFallidoAt(null);
        credencial.setBloqueadaHasta(null);
        credencial.setPasswordUpdatedAt(now);
        credencial.setUpdatedAt(now);

        CredencialEmpleado saved = credencialEmpleadoRepository.saveAndFlush(credencial);
        eventoAutenticacionService.logPasswordChanged(saved.getEmpleadoClave(), saved.getUsername());
        return credencialEmpleadoMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CredencialEmpleadoResponse obtener(String empleadoClave) {
        CredencialEmpleado credencial = credencialEmpleadoRepository.findByEmpleadoClave(empleadoClave)
                .orElseThrow(() -> new CredencialEmpleadoNotFoundException(empleadoClave));
        return credencialEmpleadoMapper.toResponse(credencial);
    }

    @Transactional
    public CredencialEmpleadoResponse actualizar(String empleadoClave, CredencialEmpleadoUpdateRequest request) {
        CredencialEmpleado credencial = credencialEmpleadoRepository.findByEmpleadoClave(empleadoClave)
                .orElseThrow(() -> new CredencialEmpleadoNotFoundException(empleadoClave));

        if (!request.version().equals(credencial.getVersion())) {
            throw new CredencialVersionConflictException(empleadoClave);
        }

        if ((request.username() == null || request.username().isBlank())
                && (request.email() == null || request.email().isBlank())
                && (request.password() == null || request.password().isBlank())) {
            throw new IllegalArgumentException("Debe enviar username, email o password para actualizar");
        }

        LocalDateTime now = LocalDateTime.now();

        if (request.username() != null && !request.username().isBlank()) {
            String normalized = normalizeUsername(request.username());
            if (credencialEmpleadoRepository.existsByUsernameIgnoreCaseAndEmpleadoClaveNot(normalized, empleadoClave)) {
                throw new CredencialUsernameConflictException(normalized);
            }
            credencial.setUsername(normalized);
        }

        if (request.email() != null && !request.email().isBlank()) {
            String normalizedEmail = normalizeEmail(request.email());
            if (credencialEmpleadoRepository.existsByEmailIgnoreCaseAndEmpleadoClaveNot(normalizedEmail, empleadoClave)) {
                throw new CredencialEmailConflictException(normalizedEmail);
            }
            credencial.setEmail(normalizedEmail);
        }

        if (request.password() != null && !request.password().isBlank()) {
            passwordPolicyService.validatePassword(request.password());
            credencial.setPasswordHash(passwordEncoder.encode(request.password()));
            credencial.setPasswordUpdatedAt(now);
            credencial.setIntentosFallidos(0);
            credencial.setUltimoIntentoFallidoAt(null);
            credencial.setBloqueadaHasta(null);
        }

        credencial.setUpdatedAt(now);

        try {
            CredencialEmpleado saved = credencialEmpleadoRepository.saveAndFlush(credencial);
            if (request.password() != null && !request.password().isBlank()) {
                eventoAutenticacionService.logPasswordChanged(saved.getEmpleadoClave(), saved.getUsername());
            }
            return credencialEmpleadoMapper.toResponse(saved);
        } catch (OptimisticLockingFailureException ex) {
            throw new CredencialVersionConflictException(empleadoClave);
        }
    }

    @Transactional
    public CredencialEmpleadoResponse cambiarEstado(String empleadoClave, CredencialEmpleadoEstadoRequest request) {
        CredencialEmpleado credencial = credencialEmpleadoRepository.findByEmpleadoClave(empleadoClave)
                .orElseThrow(() -> new CredencialEmpleadoNotFoundException(empleadoClave));

        if (!request.version().equals(credencial.getVersion())) {
            throw new CredencialVersionConflictException(empleadoClave);
        }

        credencial.setActiva(request.activa());
        if (Boolean.TRUE.equals(request.activa())) {
            credencial.setIntentosFallidos(0);
            credencial.setUltimoIntentoFallidoAt(null);
            credencial.setBloqueadaHasta(null);
        }

        credencial.setUpdatedAt(LocalDateTime.now());

        try {
            CredencialEmpleado saved = credencialEmpleadoRepository.saveAndFlush(credencial);
            eventoAutenticacionService.logCredentialStateChanged(saved.getEmpleadoClave(), saved.getUsername(), saved.isActiva());
            return credencialEmpleadoMapper.toResponse(saved);
        } catch (OptimisticLockingFailureException ex) {
            throw new CredencialVersionConflictException(empleadoClave);
        }
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username es obligatorio");
        }
        return username.trim().toLowerCase();
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email es obligatorio");
        }
        return email.trim().toLowerCase();
    }

    private void assertEmpleadoExiste(String empleadoClave) {
        if (empleadoClave == null || empleadoClave.isBlank()) {
            throw new IllegalArgumentException("empleadoClave es obligatoria");
        }
        if (!empleadoRepository.existsById(empleadoClave)) {
            throw new EmpleadoNotFoundException(empleadoClave);
        }
    }
}
