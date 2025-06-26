package com.italoweb.gestorfinan.config;

public class Apariencia {

	private String colorPrimary;
	private String gradientStartNavbar;
	private String gradientEndNavbar;
	private String name;
	private String nit;
	private String dirección;
	private String telefono;
	private String description;
	private String email;
	private String logo;
	private int sizeLogo;

	public Apariencia() {
	}

	public Apariencia(String colorPrimary, String gradientStartNavbar, String gradientEndNavbar) {
		this.colorPrimary = colorPrimary;
		this.gradientStartNavbar = gradientStartNavbar;
		this.gradientEndNavbar = gradientEndNavbar;
	}

	public String getColorPrimary() {
		return colorPrimary;
	}

	public void setColorPrimary(String colorPrimary) {
		this.colorPrimary = colorPrimary;
	}

	public String getGradientStartNavbar() {
		return gradientStartNavbar;
	}

	public void setGradientStartNavbar(String gradientStartNavbar) {
		this.gradientStartNavbar = gradientStartNavbar;
	}

	public String getGradientEndNavbar() {
		return gradientEndNavbar;
	}

	public void setGradientEndNavbar(String gradientEndNavbar) {
		this.gradientEndNavbar = gradientEndNavbar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getSizeLogo() {
		return sizeLogo;
	}

	public void setSizeLogo(int sizeLogo) {
		this.sizeLogo = sizeLogo;
	}

	public String getNit() {
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}

	public String getDirección() {
		return dirección;
	}

	public void setDirección(String dirección) {
		this.dirección = dirección;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
