package com.dsw01.practica02.service;

import com.dsw01.practica02.domain.Empleado;
import com.dsw01.practica02.dto.EmpleadoCreateRequest;
import com.dsw01.practica02.dto.EmpleadoPageResponse;
import com.dsw01.practica02.dto.EmpleadoResponse;
import com.dsw01.practica02.dto.EmpleadoUpdateRequest;
import com.dsw01.practica02.exception.EmpleadoNotFoundException;
import com.dsw01.practica02.repository.EmpleadoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpleadoService {

    private static final int PAGE_SIZE = 5;
    private final EmpleadoRepository empleadoRepository;
    private final ClaveEmpleadoGenerator claveEmpleadoGenerator;

    public EmpleadoService(EmpleadoRepository empleadoRepository,
                           ClaveEmpleadoGenerator claveEmpleadoGenerator) {
        this.empleadoRepository = empleadoRepository;
        this.claveEmpleadoGenerator = claveEmpleadoGenerator;
    }

    @Transactional
    public EmpleadoResponse crearEmpleado(EmpleadoCreateRequest request) {
        Empleado empleado = new Empleado(
                claveEmpleadoGenerator.nextClave(),
                request.nombre(),
                request.direccion(),
                request.telefono()
        );

        Empleado saved = empleadoRepository.save(empleado);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse obtenerPorClave(String clave) {
        Empleado empleado = empleadoRepository.findById(clave)
                .orElseThrow(() -> new EmpleadoNotFoundException(clave));
        return toResponse(empleado);
    }

    @Transactional(readOnly = true)
    public EmpleadoPageResponse listar(int page) {
        if (page < 0) {
            throw new IllegalArgumentException("page debe ser mayor o igual a 0");
        }

        Page<Empleado> pageResult = empleadoRepository.findAll(
                PageRequest.of(page, PAGE_SIZE, Sort.by("clave").ascending())
        );

        List<EmpleadoResponse> content = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new EmpleadoPageResponse(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Transactional
    public EmpleadoResponse actualizar(String clave, EmpleadoUpdateRequest request) {
        Empleado empleado = empleadoRepository.findById(clave)
                .orElseThrow(() -> new EmpleadoNotFoundException(clave));

        empleado.setNombre(request.nombre());
        empleado.setDireccion(request.direccion());
        empleado.setTelefono(request.telefono());

        Empleado saved = empleadoRepository.save(empleado);
        return toResponse(saved);
    }

    @Transactional
    public void eliminar(String clave) {
        Empleado empleado = empleadoRepository.findById(clave)
                .orElseThrow(() -> new EmpleadoNotFoundException(clave));
        empleadoRepository.delete(empleado);
    }

    private EmpleadoResponse toResponse(Empleado empleado) {
        return new EmpleadoResponse(
                empleado.getClave(),
                empleado.getNombre(),
                empleado.getDireccion(),
                empleado.getTelefono()
        );
    }
}