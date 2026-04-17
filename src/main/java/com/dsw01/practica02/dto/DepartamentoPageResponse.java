package com.dsw01.practica02.dto;

import java.util.List;

public record DepartamentoPageResponse(
        List<DepartamentoResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
