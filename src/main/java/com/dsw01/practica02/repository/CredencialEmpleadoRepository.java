package com.dsw01.practica02.repository;

import com.dsw01.practica02.domain.CredencialEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredencialEmpleadoRepository extends JpaRepository<CredencialEmpleado, String> {

    Optional<CredencialEmpleado> findByEmpleadoClave(String empleadoClave);

    Optional<CredencialEmpleado> findByUsernameIgnoreCase(String username);

    Optional<CredencialEmpleado> findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCaseAndEmpleadoClaveNot(String username, String empleadoClave);

    boolean existsByEmailIgnoreCaseAndEmpleadoClaveNot(String email, String empleadoClave);
}
