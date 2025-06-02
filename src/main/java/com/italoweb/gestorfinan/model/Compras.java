package com.italoweb.gestorfinan.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "compra")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Compras {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto; // codigo del producto

	@Column(name = "precio_compra", nullable = false)
	private BigDecimal precioCompra;

	@Column(name = "precio_venta", nullable = false)
	private BigDecimal precioVenta;

	@Column(name = "cantidad_ingresa", nullable = false)
	private Integer cantidadIngresar;

	@Column(name = "fecha_ingreso", nullable = false)
	private LocalDate fechaIngreso;

	@Column(name = "fecha_vencimiento", nullable = false)
	private LocalDate fechaVencimiento;

	@Column(name = "fecha_pago", nullable = false)
	private LocalDate fechaPago;

	@ManyToOne(optional = false)
	@JoinColumn(name = "medio_pago", nullable = false)
	private MedioPago medioPago;

	@Column(name = "numero_factura", length = 25)
	private String numeroFactura;

	@ManyToOne(optional = false)
	@JoinColumn(name = "proveedor_id", nullable = false)
	private Proveedor proveedor; // nit

}
