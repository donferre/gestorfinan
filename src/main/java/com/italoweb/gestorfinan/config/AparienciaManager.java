package com.italoweb.gestorfinan.config;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

public class AparienciaManager extends JsonManager<List<Apariencia>> {

    public AparienciaManager() {
        super("data/apariencia.json", new TypeReference<List<Apariencia>>() {});
    }

    public List<Apariencia> getApariencia() {
        return this.get();
    }

    public void saveApariencia(List<Apariencia> apariencia) {
        this.save(apariencia);
    }
}