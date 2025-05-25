package com.italoweb.gestorfinan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

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

	@Column(name = "descripcion", nullable = false, length = 255)
	private String descripcion;

	@ManyToOne(optional = false)
	@JoinColumn(name = "unidad_compra_id", nullable = false)
	private UnidadCompra unidadCompra;

	@Column(name = "inventario")
	private Integer inventario;

	@Column(name = "precio_venta", precision = 15, scale = 2, nullable = false)
	private BigDecimal precioVenta;

	@Column(name = "precio_compra", precision = 15, scale = 2, nullable = false)
	private BigDecimal precioCompra;

	@Column(name = "porcentaje_descuento", precision = 5, scale = 2)
	private BigDecimal porcentajeDescuento;

	@Column(name = "porcentaje_impuesto_venta", precision = 5, scale = 2)
	private BigDecimal porcentajeImpuestoVenta;

	@ManyToOne(optional = false)
	@JoinColumn(name = "concepto_impuesto_id", nullable = false)
	private Impuesto conceptoImpuesto;

	@ManyToOne(optional = false)
	@JoinColumn(name = "categoria_id", nullable = false)
	private Categoria categoria;

	@Column(name = "stock_minimo")
	private Integer stockMinimo;

	@Column(name = "marca", length = 100)
	private String marca;

	@Column(name = "ubicacion", length = 100)
	private String ubicacion;
}
