package com.italoweb.gestorfinan.repository;

import com.italoweb.gestorfinan.model.compra.CompraDetalle;

public class CompraDetalleDAO extends GenericDAOImpl<CompraDetalle, Long> {
	public CompraDetalleDAO() {
		super(CompraDetalle.class);
	}
}