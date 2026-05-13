package com.dsw01.practica02.controller;

import com.dsw01.practica02.dto.EmpleadoAuthMeResponse;
import com.dsw01.practica02.service.AuthEmpleadoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/empleado")
public class AuthEmpleadoController {

    private final AuthEmpleadoService authEmpleadoService;

    public AuthEmpleadoController(AuthEmpleadoService authEmpleadoService) {
        this.authEmpleadoService = authEmpleadoService;
    }

    @GetMapping("/me")
    public EmpleadoAuthMeResponse me() {
        return authEmpleadoService.getAuthenticatedEmpleado();
    }
}
