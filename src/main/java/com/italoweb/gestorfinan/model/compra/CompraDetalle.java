package com.italoweb.gestorfinan.model.compra;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "compra_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDetalle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "compra_id", nullable = false)
	private Compras compra;

	@ManyToOne(optional = false)
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto;

	@Column(name = "cantidad_ingresa", nullable = false)
	private Integer cantidadIngresar;

	@Column(name = "precio_compra", nullable = false)
	private BigDecimal precioCompra;

	@Column(name = "precio_venta", nullable = false)
	private BigDecimal precioVenta;

	@Column(name = "total_compra")
	private BigDecimal totalCompra;

	@Column(name = "fecha_vencimiento")
	private LocalDate fechaVencimiento;

}
