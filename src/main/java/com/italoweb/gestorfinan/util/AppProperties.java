package com.italoweb.gestorfinan.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppProperties {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = AppProperties.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo config.properties");
            }
            props.load(input);

            // Resolver propiedades del sistema, como ${user.home}
            for (String name : props.stringPropertyNames()) {
                String value = props.getProperty(name);
                props.setProperty(name, resolveSystemProperties(value));
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar config.properties", e);
        }
    }

    private static String resolveSystemProperties(String value) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(value);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String propName = matcher.group(1);
            String propValue = System.getProperty(propName, "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(propValue));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
