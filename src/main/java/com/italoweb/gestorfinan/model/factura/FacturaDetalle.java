package com.italoweb.gestorfinan.model.factura;

import java.math.BigDecimal;

import com.italoweb.gestorfinan.model.Producto;

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
@Table(name = "factura_detalle")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacturaDetalle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "factura_id", nullable = false)
	private Factura factura;

	@ManyToOne
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto;

	@Column(name = "descripcion", nullable = false)
	private String descripcion;

	@Column(name = "cantidad", nullable = false)
	private int cantidad;

	@Column(name = "precio_unitario", precision = 15, scale = 2)
	private BigDecimal precioUnitario;

	@Column(name = "total", precision = 15, scale = 2)
	private BigDecimal total;
	
	@Column(name = "descuento_unitario")
	private BigDecimal descuentoUnitario;
}
