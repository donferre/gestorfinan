package com.italoweb.gestorfinan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medios_pagos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedioPago {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, name = "nombre_medio_pago")
	private String nombre;

}