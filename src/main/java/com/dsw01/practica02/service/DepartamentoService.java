package com.dsw01.practica02.service;

import com.dsw01.practica02.domain.Departamento;
import com.dsw01.practica02.dto.DepartamentoCreateRequest;
import com.dsw01.practica02.dto.DepartamentoPageResponse;
import com.dsw01.practica02.dto.DepartamentoResponse;
import com.dsw01.practica02.dto.DepartamentoUpdateRequest;
import com.dsw01.practica02.exception.DepartamentoNotFoundException;
import com.dsw01.practica02.repository.DepartamentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    private static final int PAGE_SIZE = 5;

    private final DepartamentoRepository departamentoRepository;
    private final ClaveDepartamentoGenerator claveDepartamentoGenerator;

    public DepartamentoService(DepartamentoRepository departamentoRepository,
                               ClaveDepartamentoGenerator claveDepartamentoGenerator) {
        this.departamentoRepository = departamentoRepository;
        this.claveDepartamentoGenerator = claveDepartamentoGenerator;
    }

    @Transactional
    public DepartamentoResponse crear(DepartamentoCreateRequest request) {
        Departamento departamento = new Departamento(claveDepartamentoGenerator.nextClave(), request.nombre());
        Departamento saved = departamentoRepository.save(departamento);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DepartamentoResponse obtenerPorClave(String clave) {
        Departamento departamento = departamentoRepository.findById(clave)
                .orElseThrow(() -> new DepartamentoNotFoundException(clave));
        return toResponse(departamento);
    }

    @Transactional(readOnly = true)
    public DepartamentoPageResponse listar(int page) {
        if (page < 0) {
            throw new IllegalArgumentException("page debe ser mayor o igual a 0");
        }

        Page<Departamento> result = departamentoRepository.findAll(
                PageRequest.of(page, PAGE_SIZE, Sort.by("clave").ascending())
        );

        List<DepartamentoResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new DepartamentoPageResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional
    public DepartamentoResponse actualizar(String clave, DepartamentoUpdateRequest request) {
        Departamento departamento = departamentoRepository.findById(clave)
                .orElseThrow(() -> new DepartamentoNotFoundException(clave));

        departamento.setNombre(request.nombre());
        Departamento saved = departamentoRepository.save(departamento);
        return toResponse(saved);
    }

    @Transactional
    public void eliminar(String clave) {
        Departamento departamento = departamentoRepository.findById(clave)
                .orElseThrow(() -> new DepartamentoNotFoundException(clave));

        departamentoRepository.delete(departamento);
    }

    private DepartamentoResponse toResponse(Departamento departamento) {
        return new DepartamentoResponse(departamento.getClave(), departamento.getNombre());
    }
}
