package com.dsw01.practica02.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;

@Entity
@Table(name = "credenciales_empleado")
public class CredencialEmpleado {

    @Id
    @Column(name = "empleado_clave", nullable = false, length = 20)
    private String empleadoClave;

    @Column(name = "username", nullable = false, unique = true, length = 60)
    private String username;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "activa", nullable = false)
    private boolean activa;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos;

    @Column(name = "ultimo_intento_fallido_at")
    private LocalDateTime ultimoIntentoFallidoAt;

    @Column(name = "bloqueada_hasta")
    private LocalDateTime bloqueadaHasta;

    @Column(name = "password_updated_at", nullable = false)
    private LocalDateTime passwordUpdatedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public CredencialEmpleado() {
    }

    public String getEmpleadoClave() {
        return empleadoClave;
    }

    public void setEmpleadoClave(String empleadoClave) {
        this.empleadoClave = empleadoClave;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public LocalDateTime getUltimoIntentoFallidoAt() {
        return ultimoIntentoFallidoAt;
    }

    public void setUltimoIntentoFallidoAt(LocalDateTime ultimoIntentoFallidoAt) {
        this.ultimoIntentoFallidoAt = ultimoIntentoFallidoAt;
    }

    public LocalDateTime getBloqueadaHasta() {
        return bloqueadaHasta;
    }

    public void setBloqueadaHasta(LocalDateTime bloqueadaHasta) {
        this.bloqueadaHasta = bloqueadaHasta;
    }

    public LocalDateTime getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    public void setPasswordUpdatedAt(LocalDateTime passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
