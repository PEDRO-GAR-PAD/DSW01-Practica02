package com.dsw01.practica02.repository;

import com.dsw01.practica02.domain.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepository extends JpaRepository<Departamento, String> {
}
