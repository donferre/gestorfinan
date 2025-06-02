package com.italoweb.gestorfinan.repository;

import org.hibernate.Session;

import com.italoweb.gestorfinan.model.Producto;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class ProductoDAO extends GenericDAOImpl<Producto, Long> {
	public ProductoDAO() {
		super(Producto.class);
	}

	public boolean existeProductoConCodigo(String codigo) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = "SELECT 1 FROM Producto p WHERE p.codigo = :codigo";
			return session.createQuery(hql, Integer.class).setParameter("codigo", codigo).setMaxResults(1)
					.uniqueResultOptional().isPresent();
		}
	}	
}