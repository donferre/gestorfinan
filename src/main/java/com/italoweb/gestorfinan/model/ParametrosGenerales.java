package com.italoweb.gestorfinan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parametros_generales")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParametrosGenerales {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, name = "responsable_iva", length = 1)
	private String responsableIVA;

	@Column(name = "prefijo_dian")
	private String prefijoDian;
	
	@Column(name = "prefijo")
	private String prefijo;

	@Column(name = "consecutivo_dian")
	private Long consecutivoDian;
	
	@Column(name = "consecutivo")
	private Long consecutivo;

	@Column(name = "habilita_precio_venta",nullable = false, length = 1)
	private String habilitaPrecioVenta;
}
