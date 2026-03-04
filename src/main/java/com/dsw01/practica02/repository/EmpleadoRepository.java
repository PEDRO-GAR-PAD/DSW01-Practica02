package com.dsw01.practica02.repository;

import com.dsw01.practica02.domain.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {
}