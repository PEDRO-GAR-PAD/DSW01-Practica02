package com.dsw01.practica02.controller;

import com.dsw01.practica02.dto.DepartamentoCreateRequest;
import com.dsw01.practica02.dto.DepartamentoPageResponse;
import com.dsw01.practica02.dto.DepartamentoResponse;
import com.dsw01.practica02.dto.DepartamentoUpdateRequest;
import com.dsw01.practica02.service.DepartamentoService;
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
@RequestMapping("/api/v1/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartamentoResponse crear(@Valid @RequestBody DepartamentoCreateRequest request) {
        return departamentoService.crear(request);
    }

    @GetMapping("/{clave}")
    public DepartamentoResponse obtener(@PathVariable String clave) {
        return departamentoService.obtenerPorClave(clave);
    }

    @GetMapping
    public DepartamentoPageResponse listar(@RequestParam(defaultValue = "0") int page) {
        return departamentoService.listar(page);
    }

    @PutMapping("/{clave}")
    public DepartamentoResponse actualizar(@PathVariable String clave,
                                           @Valid @RequestBody DepartamentoUpdateRequest request) {
        return departamentoService.actualizar(clave, request);
    }

    @DeleteMapping("/{clave}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String clave) {
        departamentoService.eliminar(clave);
    }
}
