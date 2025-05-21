package com.italoweb.gestorfinan.util;

public class HEXUtil {

    // Convierte un texto en una representación hexadecimal de 3 dígitos por carácter
    public static String convertirATextoHexLargo(String texto) {
        StringBuilder hexTexto = new StringBuilder();
        for (char c : texto.toCharArray()) {
            hexTexto.append(String.format("%03x", (int) c)); // 3 dígitos hexadecimales por carácter
        }
        return hexTexto.toString();
    }

    // Verifica si una cadena es un hexadecimal válido
    public static boolean esHexadecimalValido(String texto) {
        return texto.matches("^[0-9a-fA-F]+$") && texto.length() % 3 == 0;
    }

    // Convierte un texto hexadecimal de vuelta a su texto original
    public static String convertirAOriginalDesdeHexLargo(String hexTexto) {
        if (!esHexadecimalValido(hexTexto)) {
            //throw new IllegalArgumentException("La cadena no es un hexadecimal válido.");
            return "";
        }

        StringBuilder textoOriginal = new StringBuilder();
        for (int i = 0; i < hexTexto.length(); i += 3) {
            String hexChar = hexTexto.substring(i, i + 3); // Tomamos de 3 en 3
            textoOriginal.append((char) Integer.parseInt(hexChar, 16)); // Convertimos de hex a char
        }
        return textoOriginal.toString();
    }
}
