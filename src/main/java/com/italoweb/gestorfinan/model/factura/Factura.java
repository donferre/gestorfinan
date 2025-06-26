package com.italoweb.gestorfinan.model.factura;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.italoweb.gestorfinan.model.Cliente;
import com.italoweb.gestorfinan.model.venta.Ventas;

@Entity
@Table(name = "facturas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Factura {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "numero", nullable = false, unique = true, length = 20)
	private String numero;

	@Column(name = "fecha", nullable = false)
	private LocalDate fecha;

	@ManyToOne(optional = false)
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;

	@Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
	private BigDecimal subtotal;

	@Column(name = "iva", nullable = false, precision = 15, scale = 2)
	private BigDecimal iva;

	@Column(name = "total", nullable = false, precision = 15, scale = 2)
	private BigDecimal total;

	@Column(name = "descuento", nullable = false, precision = 15, scale = 2)
	private BigDecimal descuento;

	@OneToOne(mappedBy = "factura")
	private Ventas venta;

	@Column(name = "vendedor", nullable = false)
	private String vendedor;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "nombre_empresa", nullable = false)
	private String nombreEmpresa;

	@Column(name = "nit_empresa", nullable = false)
	private String nitEmpresa;

	@Column(name = "telefono_empresa", nullable = false)
	private String telefonoEmpresa;

	@Column(name = "numero_factura_impresa", nullable = false)
	private String numeroFacturaImpresa;

	@Column(name = "direccion_empresa", nullable = false)
	private String direccionEmpresa;

	@OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FacturaDetalle> detalles = new ArrayList<>();

	@OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FacturaImpuesto> impuestos = new ArrayList<>();

	/**
	 * Asocia un detalle de factura garantizando la referencia bidireccional
	 */
	public void addDetalle(FacturaDetalle detalle) {
		detalle.setFactura(this);
		this.detalles.add(detalle);
	}

	/**
	 * Reemplaza toda la lista de detalles, manteniendo referencias
	 */
	public void setDetalles(List<FacturaDetalle> detalles) {
		this.detalles.clear();
		if (detalles != null) {
			for (FacturaDetalle fd : detalles) {
				addDetalle(fd);
			}
		}
	}

	/**
	 * Asocia un desglose de impuesto garantizando la referencia bidireccional
	 */
	public void addImpuesto(FacturaImpuesto impuesto) {
		impuesto.setFactura(this);
		this.impuestos.add(impuesto);
	}

	/**
	 * Reemplaza toda la lista de impuestos, manteniendo referencias
	 */
	public void setImpuestos(List<FacturaImpuesto> impuestos) {
		this.impuestos.clear();
		if (impuestos != null) {
			for (FacturaImpuesto fi : impuestos) {
				addImpuesto(fi);
			}
		}
	}
}