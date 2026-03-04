package com.dsw01.practica02.controller;

import com.dsw01.practica02.dto.EmpleadoCreateRequest;
import com.dsw01.practica02.dto.EmpleadoPageResponse;
import com.dsw01.practica02.dto.EmpleadoResponse;
import com.dsw01.practica02.dto.EmpleadoUpdateRequest;
import com.dsw01.practica02.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpleadoResponse crear(@Valid @RequestBody EmpleadoCreateRequest request) {
        return empleadoService.crearEmpleado(request);
    }

    @GetMapping("/{clave}")
    public EmpleadoResponse obtener(@PathVariable String clave) {
        return empleadoService.obtenerPorClave(clave);
    }

    @GetMapping
    public EmpleadoPageResponse listar(@RequestParam(defaultValue = "0") int page) {
        return empleadoService.listar(page);
    }

    @PutMapping("/{clave}")
    public EmpleadoResponse actualizar(@PathVariable String clave,
                                       @Valid @RequestBody EmpleadoUpdateRequest request) {
        return empleadoService.actualizar(clave, request);
    }

    @DeleteMapping("/{clave}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String clave) {
        empleadoService.eliminar(clave);
    }
}