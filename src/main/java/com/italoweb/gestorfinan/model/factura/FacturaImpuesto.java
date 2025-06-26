package com.italoweb.gestorfinan.model.factura;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "factura_impuesto")
public class FacturaImpuesto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "factura_id")
	private Factura factura;

	@Column(name = "porcentaje", precision = 5, scale = 2, nullable = false)
	private BigDecimal porcentaje;

	@Column(name = "valor", precision = 14, scale = 2, nullable = false)
	private BigDecimal valor;

	public FacturaImpuesto() {
	}

	public FacturaImpuesto(Factura factura, BigDecimal porcentaje, BigDecimal valor) {
		this.factura = factura;
		this.porcentaje = porcentaje;
		this.valor = valor;
	}

	// getters y setters
	public Long getId() {
		return id;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	public BigDecimal getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
}