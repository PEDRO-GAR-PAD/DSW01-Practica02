package com.dsw01.practica02.exception;

public class LegacyRouteNotSupportedException extends RuntimeException {

    public LegacyRouteNotSupportedException() {
        this("/api/v1/empleados");
    }

    public LegacyRouteNotSupportedException(String versionedPath) {
        super("Ruta no versionada no soportada. Use " + versionedPath);
    }
}