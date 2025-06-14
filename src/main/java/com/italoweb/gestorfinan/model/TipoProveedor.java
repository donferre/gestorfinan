package com.italoweb.gestorfinan.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_proveedor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoProveedor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, name = "nombre",length = 1)
	private String nombre;
	
	@Column(nullable = false, name = "descripcion")
	private String descripcion;
}