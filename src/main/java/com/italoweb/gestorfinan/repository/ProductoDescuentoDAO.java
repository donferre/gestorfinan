package com.italoweb.gestorfinan.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.ProductoDescuento;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class ProductoDescuentoDAO extends GenericDAOImpl<ProductoDescuento, Long> {
	public ProductoDescuentoDAO() {
		super(ProductoDescuento.class);
	}

	public Optional<ProductoDescuento> obtenerDescuentoPorIdProducto(Long id) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = "FROM ProductoDescuento p WHERE p.producto.id = :id";
			return session.createQuery(hql, ProductoDescuento.class).setParameter("id", id).setMaxResults(1)
					.uniqueResultOptional();
		}
	}

	public List<ProductoDescuento> obtenerDescuentosFinalizadosHastaHoy() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			 LocalDateTime limite = LocalDate.now().atStartOfDay();
			String hql = "SELECT pd FROM ProductoDescuento pd WHERE pd.fechaFinaliza < :limite";

			Query<ProductoDescuento> query = session.createQuery(hql, ProductoDescuento.class);
			query.setParameter("limite", limite);
			return query.getResultList();
		}
	}

}