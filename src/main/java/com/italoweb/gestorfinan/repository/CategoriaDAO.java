package com.italoweb.gestorfinan.repository;

import com.italoweb.gestorfinan.model.Categoria;

public class CategoriaDAO extends GenericDAOImpl<Categoria, Long> {
	public CategoriaDAO() {
		super(Categoria.class);
	}
}