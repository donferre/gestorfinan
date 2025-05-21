package com.italoweb.gestorfinan.model;

public enum PerfilUsuario {
    ADMIN("Administrador"),
    OPERADOR("Operador"),
    ESTETICISTA("Esteticista"),
    RECEPCIONISTA("Recepcionista");

    private final String label;

    private PerfilUsuario(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
