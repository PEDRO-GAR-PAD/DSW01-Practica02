package com.dsw01.practica02.service;

import com.dsw01.practica02.domain.CredencialEmpleado;
import com.dsw01.practica02.domain.Empleado;
import com.dsw01.practica02.repository.CredencialEmpleadoRepository;
import com.dsw01.practica02.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmpleadoAuthUserDetailsService implements UserDetailsService {

    private final CredencialEmpleadoRepository credencialEmpleadoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final String bootstrapUsername;
    private final String bootstrapPassword;
    private final String bootstrapRoles;
    private final Set<String> bootstrapAliases;

    public EmpleadoAuthUserDetailsService(
            CredencialEmpleadoRepository credencialEmpleadoRepository,
            EmpleadoRepository empleadoRepository,
            @Value("${app.auth.bootstrap.username:master}") String bootstrapUsername,
            @Value("${app.auth.bootstrap.password:admin123}") String bootstrapPassword,
            @Value("${app.auth.bootstrap.roles:USER}") String bootstrapRoles,
            @Value("${app.auth.bootstrap.aliases:master,admin}") String bootstrapAliases) {
        this.credencialEmpleadoRepository = credencialEmpleadoRepository;
        this.empleadoRepository = empleadoRepository;
        this.bootstrapUsername = bootstrapUsername;
        this.bootstrapPassword = bootstrapPassword;
        this.bootstrapRoles = bootstrapRoles;
        this.bootstrapAliases = Arrays.stream(bootstrapAliases.split(","))
                .map(String::trim)
                .filter(alias -> !alias.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Usuario vacio");
        }

        String normalizedPrincipal = username.trim();
        if (isBootstrapPrincipal(normalizedPrincipal)) {
            return buildBootstrapAdmin(normalizedPrincipal);
        }

        Optional<CredencialEmpleado> byUsername = credencialEmpleadoRepository
            .findByUsernameIgnoreCase(normalizedPrincipal);
        CredencialEmpleado cred = byUsername.orElseGet(() -> credencialEmpleadoRepository
            .findByEmailIgnoreCase(normalizedPrincipal)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado")));

        if (!cred.isActiva()) {
            throw new UsernameNotFoundException("Cuenta inactiva");
        }

        String empleadoClave = cred.getEmpleadoClave();
        if (empleadoClave == null || empleadoClave.isBlank()) {
            throw new UsernameNotFoundException("Credencial sin empleado asociado");
        }

        Empleado empleado = empleadoRepository.findById(empleadoClave)
                .orElseThrow(() -> new UsernameNotFoundException("Empleado no encontrado"));

        boolean locked = cred.getBloqueadaHasta() != null && cred.getBloqueadaHasta().isAfter(LocalDateTime.now());

        return new EmpleadoAuthUserDetails(
                cred.getUsername(),
                cred.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_EMPLEADO")),
                empleadoClave,
                empleado.getNombre(),
                cred.isActiva(),
                !locked,
                false
        );
    }

    private boolean isBootstrapPrincipal(String principal) {
        String normalized = principal.toLowerCase();
        return bootstrapUsername.equalsIgnoreCase(principal) || bootstrapAliases.contains(normalized);
    }

    private EmpleadoAuthUserDetails buildBootstrapAdmin(String principal) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String role : bootstrapRoles.split(",")) {
            String trimmed = role.trim();
            if (!trimmed.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + trimmed));
            }
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return new EmpleadoAuthUserDetails(
            principal,
                bootstrapPassword,
                authorities,
            "MASTER",
            "Master",
                true,
                true,
                true
        );
    }
}
