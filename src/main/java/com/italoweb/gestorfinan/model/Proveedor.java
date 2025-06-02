package com.italoweb.gestorfinan.model;

import com.italoweb.gestorfinan.model.poblacion.Ciudad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proveedor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proveedor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, name = "nombre")
	private String nombre;

	@Column(name = "nit", nullable = false, unique = true)
	private String nit;
	
	@Column(nullable = false, length = 1)
	@Pattern(regexp = "^[NJ]$", message = "El tipo de proveedor debe ser 'N' o 'J'")
	private String tipoProveedor;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ciudad_id", nullable = false) 
	private Ciudad ciudad;
	
	@Column(nullable = false, name = "direccion")
	private String direccion;

	@Column(nullable = false, name = "email")
	private String email;
	
	@Column(nullable = false,name = "telefono")
	private String telefono;

	@Column(nullable = false, name = "persona_contacto")
	private String personaContacto;
	
	@Column(nullable = false)
	private Estado estado;


}
