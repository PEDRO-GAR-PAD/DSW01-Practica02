package com.dsw01.practica02.service;

import com.dsw01.practica02.domain.CredencialEmpleado;
import com.dsw01.practica02.domain.Empleado;
import com.dsw01.practica02.repository.CredencialEmpleadoRepository;
import com.dsw01.practica02.repository.EmpleadoRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class EmpleadoAuthenticationProvider implements AuthenticationProvider {

    private final EmpleadoAuthUserDetailsService userDetailsService;
    private final CredencialEmpleadoRepository credencialEmpleadoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final AuthLockoutService authLockoutService;
    private final EventoAutenticacionService eventoAutenticacionService;

    public EmpleadoAuthenticationProvider(
            EmpleadoAuthUserDetailsService userDetailsService,
            CredencialEmpleadoRepository credencialEmpleadoRepository,
            EmpleadoRepository empleadoRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            AuthLockoutService authLockoutService,
            EventoAutenticacionService eventoAutenticacionService) {
        this.userDetailsService = userDetailsService;
        this.credencialEmpleadoRepository = credencialEmpleadoRepository;
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.authLockoutService = authLockoutService;
        this.eventoAutenticacionService = eventoAutenticacionService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials() != null
                ? authentication.getCredentials().toString()
                : "";

        EmpleadoAuthUserDetails details;
        try {
            details = (EmpleadoAuthUserDetails) userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        if (details.isBootstrapAdmin()) {
            if (!details.getPassword().equals(rawPassword)) {
                throw new BadCredentialsException("Credenciales invalidas");
            }
            return UsernamePasswordAuthenticationToken.authenticated(
                    new AuthEmpleadoPrincipal("MASTER", details.getUsername(), "Master", true),
                    null,
                    details.getAuthorities());
        }

        String empleadoClave = details.getEmpleadoClave();
        if (empleadoClave == null) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        CredencialEmpleado credencial = credencialEmpleadoRepository.findByEmpleadoClave(empleadoClave)
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        String ip = currentIp();

        if (!credencial.isActiva()) {
            eventoAutenticacionService.logLoginFailed(credencial.getEmpleadoClave(), credencial.getUsername(), "INACTIVE", ip);
            throw new BadCredentialsException("Credenciales invalidas");
        }

        if (authLockoutService.isLocked(credencial)) {
            eventoAutenticacionService.logLoginFailed(credencial.getEmpleadoClave(), credencial.getUsername(), "LOCKED", ip);
            throw new BadCredentialsException("Credenciales invalidas");
        }

        if (!passwordEncoder.matches(rawPassword, credencial.getPasswordHash())) {
            boolean lockedNow = authLockoutService.registerFailedAttempt(credencial);
            eventoAutenticacionService.logLoginFailed(
                    credencial.getEmpleadoClave(),
                    credencial.getUsername(),
                    lockedNow ? "LOCKED" : "BAD_CREDENTIALS",
                    ip);
            if (lockedNow) {
                eventoAutenticacionService.logAccountLocked(credencial.getEmpleadoClave(), credencial.getUsername(), ip);
            }
            throw new BadCredentialsException("Credenciales invalidas");
        }

        Empleado empleado = empleadoRepository.findById(empleadoClave)
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        authLockoutService.registerSuccessfulLogin(credencial);
        eventoAutenticacionService.logLoginSuccess(credencial.getEmpleadoClave(), credencial.getUsername(), ip);

        AuthEmpleadoPrincipal principal = new AuthEmpleadoPrincipal(
                empleado.getClave(),
                credencial.getUsername(),
                empleado.getNombre(),
                false
        );

        return UsernamePasswordAuthenticationToken.authenticated(principal, null, details.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private String currentIp() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            return attrs.getRequest().getRemoteAddr();
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(auth).map(Authentication::getName).orElse(null);
    }
}
