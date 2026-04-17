package com.dsw01.practica02.service;

import com.dsw01.practica02.domain.EventoAutenticacion;
import com.dsw01.practica02.domain.ResultadoAutenticacion;
import com.dsw01.practica02.domain.TipoEventoAutenticacion;
import com.dsw01.practica02.repository.EventoAutenticacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EventoAutenticacionService {

    private final EventoAutenticacionRepository eventoAutenticacionRepository;

    public EventoAutenticacionService(EventoAutenticacionRepository eventoAutenticacionRepository) {
        this.eventoAutenticacionRepository = eventoAutenticacionRepository;
    }

    @Transactional
    public void logLoginSuccess(String empleadoClave, String username, String ipOrigen) {
        save(empleadoClave, username, TipoEventoAutenticacion.LOGIN_SUCCESS, ResultadoAutenticacion.SUCCESS, ipOrigen, "AUTH_OK");
    }

    @Transactional
    public void logLoginFailed(String empleadoClave, String username, String detalle, String ipOrigen) {
        save(empleadoClave, username, TipoEventoAutenticacion.LOGIN_FAILED, ResultadoAutenticacion.FAILURE, ipOrigen, detalle);
    }

    @Transactional
    public void logAccountLocked(String empleadoClave, String username, String ipOrigen) {
        save(empleadoClave, username, TipoEventoAutenticacion.ACCOUNT_LOCKED, ResultadoAutenticacion.FAILURE, ipOrigen, "LOCKED");
    }

    @Transactional
    public void logPasswordChanged(String empleadoClave, String username) {
        save(empleadoClave, username, TipoEventoAutenticacion.PASSWORD_CHANGED, ResultadoAutenticacion.SUCCESS, null, "PASSWORD_CHANGED");
    }

    @Transactional
    public void logCredentialStateChanged(String empleadoClave, String username, boolean activa) {
        TipoEventoAutenticacion tipo = activa
                ? TipoEventoAutenticacion.CREDENTIAL_ENABLED
                : TipoEventoAutenticacion.CREDENTIAL_DISABLED;
        save(empleadoClave, username, tipo, ResultadoAutenticacion.SUCCESS, null, activa ? "ENABLED" : "DISABLED");
    }

    private void save(String empleadoClave,
                      String username,
                      TipoEventoAutenticacion tipo,
                      ResultadoAutenticacion resultado,
                      String ipOrigen,
                      String detalle) {
        EventoAutenticacion evento = new EventoAutenticacion();
        evento.setEmpleadoClave(empleadoClave);
        evento.setUsernameSnapshot(username);
        evento.setTipoEvento(tipo);
        evento.setResultado(resultado);
        evento.setIpOrigen(ipOrigen);
        evento.setDetalle(detalle);
        evento.setCreatedAt(LocalDateTime.now());
        eventoAutenticacionRepository.save(evento);
    }
}
