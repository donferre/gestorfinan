package com.italoweb.gestorfinan.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto_descuento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDescuento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto;

	@Column(name = "descuento", nullable = false, precision = 5, scale = 2)
	private BigDecimal descuento;

	@Column(name = "fecha_inicia", nullable = false)
	private LocalDateTime fechaInicia;

	@Column(name = "fecha_finaliza", nullable = false)
	private LocalDateTime fechaFinaliza;
}
