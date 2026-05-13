package com.dsw01.practica02.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class EmpleadoAuthUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String empleadoClave;
    private final String nombre;
    private final boolean enabled;
    private final boolean accountNonLocked;
    private final boolean bootstrapAdmin;

    public EmpleadoAuthUserDetails(String username,
                                   String password,
                                   Collection<? extends GrantedAuthority> authorities,
                                   String empleadoClave,
                                   String nombre,
                                   boolean enabled,
                                   boolean accountNonLocked,
                                   boolean bootstrapAdmin) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.empleadoClave = empleadoClave;
        this.nombre = nombre;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.bootstrapAdmin = bootstrapAdmin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getEmpleadoClave() {
        return empleadoClave;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isBootstrapAdmin() {
        return bootstrapAdmin;
    }
}
