package com.italoweb.gestorfinan.model;

public enum TipoMovimiento {
    INGRESOS("Ingresos"),
    EGRESOS("Egresos");

    private final String label;

    private TipoMovimiento(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
