package com.dsw01.practica02.mapper;

import com.dsw01.practica02.domain.CredencialEmpleado;
import com.dsw01.practica02.dto.CredencialEmpleadoResponse;
import org.springframework.stereotype.Component;

@Component
public class CredencialEmpleadoMapper {

    public CredencialEmpleadoResponse toResponse(CredencialEmpleado credencial) {
        return new CredencialEmpleadoResponse(
                credencial.getEmpleadoClave(),
                credencial.getUsername(),
                credencial.getEmail(),
                credencial.isActiva(),
                credencial.getIntentosFallidos(),
                credencial.getBloqueadaHasta(),
                credencial.getPasswordUpdatedAt(),
                credencial.getUpdatedAt(),
                credencial.getVersion()
        );
    }
}
