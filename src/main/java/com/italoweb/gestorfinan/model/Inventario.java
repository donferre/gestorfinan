package com.italoweb.gestorfinan.model;

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
@Table(name= "inventario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto;// codigo
	
	@Column(name = "cantidad_stok")
	private Integer cantidadStok;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "proveedor_id", nullable = false)
	private Proveedor proveedor;
}
