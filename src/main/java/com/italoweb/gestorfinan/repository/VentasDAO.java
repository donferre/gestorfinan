package com.italoweb.gestorfinan.repository;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.venta.Ventas;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class VentasDAO extends GenericDAOImpl<Ventas, Long> {
	public VentasDAO() {
		super(Ventas.class);
	}

	public String obtenerUltimaFactura() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String sql = "SELECT factura FROM Ventas "
					+ "ORDER BY LPAD(REGEXP_REPLACE(factura, '^[a-zA-Z. ]+', ''), 10, '0') DESC " + "LIMIT 1";
			Query<String> query = session.createNativeQuery(sql, String.class);
			return query.uniqueResult();
		}
	}

}
