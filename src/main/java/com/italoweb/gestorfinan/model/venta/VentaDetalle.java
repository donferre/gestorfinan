package com.italoweb.gestorfinan.model.venta;

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
@Table(name = "venta_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDetalle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "venta_id", nullable = false)
	private Ventas venta;

	@ManyToOne(optional = false)
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto; //precio de venta,producto, 
	
	@Column(name = "cantidad_vendida")
	private Integer cantidadVendidad;

	@Column(name = "total_venta")
	private BigDecimal totalVenta;
	
	@Column(name = "valor_neto_venta")
	private BigDecimal netoVenta;

	@Column(name = "ivaVenta")
	private BigDecimal ivaVenta;
	
	@Column(name = "precio_venta_final")
	private BigDecimal PrecioVentaFinal;

	@Column(name = "descuento_producto", precision = 5, scale = 2)
	private BigDecimal descuentoProducto;
}
