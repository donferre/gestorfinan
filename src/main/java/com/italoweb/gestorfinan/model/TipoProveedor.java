package com.italoweb.gestorfinan.model;

public enum TipoProveedor  {
    NATURAL("N"),
    JURIDICO("J");

    private final String label;

    private TipoProveedor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}