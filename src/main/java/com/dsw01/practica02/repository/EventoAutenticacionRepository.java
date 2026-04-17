package com.dsw01.practica02.repository;

import com.dsw01.practica02.domain.EventoAutenticacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoAutenticacionRepository extends JpaRepository<EventoAutenticacion, Long> {
}
