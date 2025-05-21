package com.italoweb.gestorfinan.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Validator {

    public static String encryptToMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar en MD5", e);
        }
    }

    public static String encriptar(String texto) {
        return encryptToMD5(texto);
    }

    public static boolean validateMD5(String input, String storedHash) {
        return encryptToMD5(input).equals(storedHash);
    }
}
