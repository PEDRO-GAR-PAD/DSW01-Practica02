package com.dsw01.practica02.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_autenticacion")
public class EventoAutenticacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empleado_clave", nullable = false, length = 20)
    private String empleadoClave;

    @Column(name = "username_snapshot", nullable = false, length = 60)
    private String usernameSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 40)
    private TipoEventoAutenticacion tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 20)
    private ResultadoAutenticacion resultado;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "detalle", length = 255)
    private String detalle;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmpleadoClave() {
        return empleadoClave;
    }

    public void setEmpleadoClave(String empleadoClave) {
        this.empleadoClave = empleadoClave;
    }

    public String getUsernameSnapshot() {
        return usernameSnapshot;
    }

    public void setUsernameSnapshot(String usernameSnapshot) {
        this.usernameSnapshot = usernameSnapshot;
    }

    public TipoEventoAutenticacion getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEventoAutenticacion tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public ResultadoAutenticacion getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAutenticacion resultado) {
        this.resultado = resultado;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
