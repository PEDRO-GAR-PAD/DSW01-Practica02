package com.dsw01.practica02.controller;

import com.dsw01.practica02.exception.LegacyRouteNotSupportedException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
@RequestMapping("/api/departamentos")
public class LegacyDepartamentoController {

    @GetMapping
    public void getAllLegacy() {
        throw new LegacyRouteNotSupportedException("/api/v1/departamentos");
    }

    @PostMapping
    public void createLegacy() {
        throw new LegacyRouteNotSupportedException("/api/v1/departamentos");
    }

    @GetMapping("/{clave}")
    public void getByClaveLegacy(@PathVariable String clave) {
        throw new LegacyRouteNotSupportedException("/api/v1/departamentos");
    }

    @PutMapping("/{clave}")
    public void updateLegacy(@PathVariable String clave) {
        throw new LegacyRouteNotSupportedException("/api/v1/departamentos");
    }

    @DeleteMapping("/{clave}")
    public void deleteLegacy(@PathVariable String clave) {
        throw new LegacyRouteNotSupportedException("/api/v1/departamentos");
    }
}
