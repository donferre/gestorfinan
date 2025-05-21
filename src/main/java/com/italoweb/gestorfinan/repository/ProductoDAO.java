package com.italoweb.gestorfinan.repository;

import com.italoweb.gestorfinan.model.Producto;

public class ProductoDAO extends GenericDAOImpl<Producto, Long> {
    public ProductoDAO() {
        super(Producto.class);
    }
}