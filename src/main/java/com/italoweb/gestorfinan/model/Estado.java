package com.italoweb.gestorfinan.model;

public enum Estado {
    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String label;

    private Estado(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
