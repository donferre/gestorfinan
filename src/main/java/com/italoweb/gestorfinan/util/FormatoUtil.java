package com.italoweb.gestorfinan.util;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatoUtil {

    // Formatos de fecha
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FORMATO_ISO = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat FORMATO_COMPLETO = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    // Formato numérico con separadores de miles y dos decimales
    private static final NumberFormat FORMATO_MONEDA = new DecimalFormat("#,##0.00");

    // --- FORMATEADORES ---

    public static String formatearFecha(Date fecha) {
        return fecha != null ? FORMATO_FECHA.format(fecha) : "";
    }

    public static String formatearFechaISO(Date fecha) {
        return fecha != null ? FORMATO_ISO.format(fecha) : "";
    }

    public static String formatearFechaCompleta(Date fecha) {
        return fecha != null ? FORMATO_COMPLETO.format(fecha) : "";
    }

    public static String formatearMoneda(BigDecimal valor) {
        return valor != null ? FORMATO_MONEDA.format(valor) : "0.00";
    }

    // --- PARSEADORES ---

    public static Date parsearFecha(String fechaStr) {
        try {
            return fechaStr != null && !fechaStr.trim().isEmpty() ? FORMATO_FECHA.parse(fechaStr.trim()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static BigDecimal parsearMoneda(String valorStr) {
        try {
            if (valorStr == null || valorStr.trim().isEmpty()) return BigDecimal.ZERO;
            Number number = FORMATO_MONEDA.parse(valorStr.trim());
            return new BigDecimal(number.toString());
        } catch (ParseException e) {
            return BigDecimal.ZERO;
        }
    }

    // --- VALIDADORES ---

    public static boolean esNumerico(String valor) {
        if (valor == null || valor.trim().isEmpty()) return false;
        try {
            new BigDecimal(valor.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esFechaValida(String fechaStr) {
        try {
            FORMATO_FECHA.parse(fechaStr.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- FECHAS COMUNES ---

    public static String obtenerFechaActualFormateada() {
        return formatearFecha(new Date());
    }

    public static Date obtenerFechaActual() {
        return new Date();
    }
}
