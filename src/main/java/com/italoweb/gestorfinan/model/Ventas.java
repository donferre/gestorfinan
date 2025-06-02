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
@Table(name = "ventas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ventas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "numero_factura", length = 50, nullable = false)
	private String numeroFactura;
	/*
	 * Para el numero factura si mi empresa es regimen Simplificado: La factura es
	 * secuencial,no registra en la dian. regimen Comun: Debe solicitar
	 * certificación a la DIAN para númeración de factura. Utilizar una propertie
	 * para identificar.
	 */

	@ManyToOne(optional = false)
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente; // nit

	@ManyToOne(optional = false)
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto;// codigo

	@Column(name = "cantidad_vendida")
	private Integer cantidadVendidad;

	@Column(name = "valor_neto_venta")
	private BigDecimal netoVenta;

	@Column(name = "ivaVenta")
	private BigDecimal ivaVenta;
	/*
	 * Este se calcula por medio del valor introducido en el producto por el
	 * netoVenta
	 */

	@Column(name = "total_venta")
	private BigDecimal totalVenta;

	@ManyToOne(optional = false)
	@JoinColumn(name = "vendedor_id", nullable = false)
	private Empleado vendedor;

	@ManyToOne(optional = false)
	@JoinColumn(name = "precio_venta", nullable = false)
	private Compras precioVenta;

	@Column(name = "fecha_venta", nullable = false)
	private LocalDate fechaVenta;

	@ManyToOne(optional = false)
	@JoinColumn(name = "medio_pago", nullable = false)
	private MedioPago medioPago;

}