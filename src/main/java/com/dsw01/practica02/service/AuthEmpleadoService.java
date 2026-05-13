package com.dsw01.practica02.service;

import com.dsw01.practica02.dto.EmpleadoAuthMeResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthEmpleadoService {

    public EmpleadoAuthMeResponse getAuthenticatedEmpleado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("No existe sesion autenticada");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthEmpleadoPrincipal authEmpleadoPrincipal) {
            List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return new EmpleadoAuthMeResponse(
                    authEmpleadoPrincipal.empleadoClave(),
                    authEmpleadoPrincipal.username(),
                    authEmpleadoPrincipal.nombre(),
                "AUTHENTICATED",
                roles
            );
        }

        throw new IllegalArgumentException("Sesion de empleado no disponible");
    }
}
