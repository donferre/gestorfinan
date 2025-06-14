package com.italoweb.gestorfinan.model.venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.italoweb.gestorfinan.model.Cliente;
import com.italoweb.gestorfinan.model.Empleado;
import com.italoweb.gestorfinan.model.MedioPago;
import com.italoweb.gestorfinan.model.compra.Compras;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@Column(name = "factura", length = 50, nullable = false)
	private String factura;

	@ManyToOne(optional = false)
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;

	@Column(name = "total_venta")
	private BigDecimal totalVenta;

	@ManyToOne(optional = false)
	@JoinColumn(name = "vendedor_id")
	private Empleado vendedor;

	@ManyToOne(optional = false)
	@JoinColumn(name = "compra_asociada")
	private Compras compraAsociada;

	@Column(name = "fecha_venta", nullable = false)
	private LocalDate fechaVenta;

	@ManyToOne(optional = false)
	@JoinColumn(name = "medio_pago", nullable = false)
	private MedioPago medioPago;

	@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<VentaDetalle> detalles;

}