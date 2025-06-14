package com.italoweb.gestorfinan.repository;

import com.italoweb.gestorfinan.model.compra.Compras;

public class ComprasDAO extends GenericDAOImpl<Compras, Long> {
	public ComprasDAO() {
		super(Compras.class);
	}
}
