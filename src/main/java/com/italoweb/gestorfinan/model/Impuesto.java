package com.italoweb.gestorfinan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "impuesto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Impuesto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "codigo", nullable = false, length = 20)
	private String codigo;

	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	@Column(name = "tipo_aplicacion", length = 20)
	private String tipoAplicacion;

	@Column(name = "porcentaje", precision = 5, scale = 2)
	private BigDecimal porcentaje;

	@Column(name = "valor_fijo", precision = 10, scale = 2)
	private BigDecimal valorFijo;

	@Column(name = "aplica_a", length = 100)
	private String aplicaA;
}
