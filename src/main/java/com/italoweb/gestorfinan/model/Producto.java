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
@Table(name = "producto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "codigo", nullable = false, length = 50)
	private String codigo;

	@Column(name = "nombre", nullable = false, length = 255)
	private String nombre;

	@Column(name = "descripcion", length = 255)
	private String descripcion;

	@ManyToOne(optional = false)
	@JoinColumn(name = "unidad_compra_id", nullable = false)
	private UnidadCompra unidadCompra;

	@ManyToOne(optional = false)
	@JoinColumn(name = "categoria_id", nullable = false)
	private Categoria categoria;

	@Column(name = "marca", length = 100)
	private String marca;

	@Column(name = "stock_minimo")
	private Integer stockMinimo;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "impuesto_id", nullable = false)
	private Impuesto impuesto;
	
	@Column(name = "fecha_inicia_desc")
	private LocalDateTime  fechaIniciaDescuento;
	
	@Column(name = "fecha_final_desc")
	private LocalDateTime  fechaFinalDescuento;
	
//	@Column(name = "fecha_vencimiento")
//	private LocalDateTime  fechaVencimiento;
	
	@Column(name = "decuento")
	private BigDecimal descuento;

	@Column(name = "precio_compra")
	private BigDecimal precioCompra;

	@Column(name = "precio_venta")
	private BigDecimal precioVenta;
}
