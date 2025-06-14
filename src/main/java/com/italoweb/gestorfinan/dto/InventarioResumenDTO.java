package com.italoweb.gestorfinan.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioResumenDTO {
	private Long productoId;
	private String codigoProducto;
	private String nombreProducto;
	private Integer stockActual;
	private LocalDateTime ultimaActualizacion;
}
