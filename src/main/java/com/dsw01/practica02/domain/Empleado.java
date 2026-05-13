package com.dsw01.practica02.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @Column(name = "clave", nullable = false, unique = true, length = 20)
    private String clave;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 100)
    private String direccion;

    @Column(name = "telefono", nullable = false, length = 100)
    private String telefono;

    @Column(name = "departamento_clave", nullable = true, length = 20)
    private String departamentoClave;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public Empleado() {
    }

    public Empleado(String clave, String nombre, String direccion, String telefono) {
        this.clave = clave;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.departamentoClave = null;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDepartamentoClave() {
        return departamentoClave;
    }

    public void setDepartamentoClave(String departamentoClave) {
        this.departamentoClave = departamentoClave;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
