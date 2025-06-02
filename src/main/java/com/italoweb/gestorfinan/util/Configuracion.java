package com.italoweb.gestorfinan.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuracion {
    private static final Properties propiedades = new Properties();

    static {
        try (InputStream input = Configuracion.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                propiedades.load(input);
            } else {
                throw new RuntimeException("No se encontró el archivo configuracion.properties");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error cargando configuracion.properties", ex);
        }
    }

    public static String get(String clave) {
        return propiedades.getProperty(clave);
    }
}
