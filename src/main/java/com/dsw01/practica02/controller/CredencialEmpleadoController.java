package com.dsw01.practica02.controller;

import com.dsw01.practica02.dto.CredencialEmpleadoCreateRequest;
import com.dsw01.practica02.dto.CredencialEmpleadoEstadoRequest;
import com.dsw01.practica02.dto.CredencialEmpleadoResponse;
import com.dsw01.practica02.dto.CredencialEmpleadoUpdateRequest;
import com.dsw01.practica02.service.CredencialEmpleadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/empleados/{clave}/credenciales")
public class CredencialEmpleadoController {

    private final CredencialEmpleadoService credencialEmpleadoService;

    public CredencialEmpleadoController(CredencialEmpleadoService credencialEmpleadoService) {
        this.credencialEmpleadoService = credencialEmpleadoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CredencialEmpleadoResponse crear(@PathVariable("clave") String clave,
                                            @Valid @RequestBody CredencialEmpleadoCreateRequest request) {
        return credencialEmpleadoService.crear(clave, request);
    }

    @GetMapping
    public CredencialEmpleadoResponse obtener(@PathVariable("clave") String clave) {
        return credencialEmpleadoService.obtener(clave);
    }

    @PutMapping
    public CredencialEmpleadoResponse actualizar(@PathVariable("clave") String clave,
                                                 @Valid @RequestBody CredencialEmpleadoUpdateRequest request) {
        return credencialEmpleadoService.actualizar(clave, request);
    }

    @PatchMapping("/estado")
    public CredencialEmpleadoResponse cambiarEstado(@PathVariable("clave") String clave,
                                                    @Valid @RequestBody CredencialEmpleadoEstadoRequest request) {
        return credencialEmpleadoService.cambiarEstado(clave, request);
    }
}
