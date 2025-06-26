package com.italoweb.gestorfinan.util;

import com.italoweb.gestorfinan.config.Apariencia;
import com.italoweb.gestorfinan.config.AparienciaManager;
import com.italoweb.gestorfinan.model.ParametrosGenerales;
import com.italoweb.gestorfinan.repository.ParametrosGeneralesDAO;

import java.nio.file.Paths;
import java.util.List;

public class FacturaHelper {
	private static final String RUTA_LOGO = Paths
			.get(System.getProperty("user.home"), "gestorfinan", "config", "images", "logo_app.jpg").toString();

	public static String obtenerRutaLogo() {
		System.out.println("Ruta del logo: " + RUTA_LOGO);
		return RUTA_LOGO;
	}

	public static String obtenerNombreEmpresa() {
		AparienciaManager manager = new AparienciaManager();
		List<Apariencia> lista = manager.getApariencia();

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0).getName();
		}
		return "Empresa por defecto";
	}

	public static String obtenerNitEmpresa() {
		AparienciaManager manager = new AparienciaManager();
		List<Apariencia> lista = manager.getApariencia();

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0).getNit();
		}
		return "000000000-0";
	}

	public static String obtenerTelefonoEmpresa() {
		AparienciaManager manager = new AparienciaManager();
		List<Apariencia> lista = manager.getApariencia();

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0).getTelefono();
		}
		return "(999) 999-9999";
	}

	public static String obtenerDireccionEmpresa() {
		AparienciaManager manager = new AparienciaManager();
		List<Apariencia> lista = manager.getApariencia();

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0).getDirección();
		}
		return "Cll x av x";
	}

	public static String obtenerCorreoEmpresa() {
		AparienciaManager manager = new AparienciaManager();
		List<Apariencia> lista = manager.getApariencia();

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0).getEmail();
		}
		return "ventas@tuempresa.com";
	}

	public static String obtenerTipoFactura() {
		ParametrosGeneralesDAO parametroGeneral = new ParametrosGeneralesDAO();
		ParametrosGenerales parametro = parametroGeneral.obtenerUnicoRegistro();

		if (parametro != null) {
			return parametro.getTipoFactura();
		}
		return "N/A";
	}

}
