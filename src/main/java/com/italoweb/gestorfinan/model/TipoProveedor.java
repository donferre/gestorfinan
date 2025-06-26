package com.italoweb.gestorfinan.model;

public enum TipoProveedor {
    NATURAL("Natural"),
    JURIDICO("Juridico");

    private final String label;

    private TipoProveedor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
