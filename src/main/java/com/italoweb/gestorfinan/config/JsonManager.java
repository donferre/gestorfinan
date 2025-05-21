package com.italoweb.gestorfinan.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonManager<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File file;
    private final TypeReference<T> typeReference;
    private static final String SRC_DIR = System.getProperty("user.home") + "/gestorfinan/config/";

    public JsonManager(String filePath, TypeReference<T> typeReference) {
        this.file = new File(SRC_DIR.concat(filePath));
        this.typeReference = typeReference;

        // Crear archivo si no existe
        if (!file.exists()) {
            crearArchivoVacio();
        }
    }

    private void crearArchivoVacio() {
        try {
            file.getParentFile().mkdirs(); // Crear carpetas si no existen
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, new ArrayList<T>());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el archivo JSON: " + file.getPath(), e);
        }
    }

    public T get() {
        try {
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save(T obj) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
