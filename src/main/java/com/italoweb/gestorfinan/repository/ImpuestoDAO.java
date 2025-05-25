package com.italoweb.gestorfinan.repository;

import com.italoweb.gestorfinan.model.Impuesto;

public class ImpuestoDAO extends GenericDAOImpl<Impuesto, Long> {
	public ImpuestoDAO() {
		super(Impuesto.class);
	}
}