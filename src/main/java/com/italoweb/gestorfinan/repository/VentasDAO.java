package com.italoweb.gestorfinan.repository;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.venta.Ventas;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class VentasDAO extends GenericDAOImpl<Ventas, Long> {
	public VentasDAO() {
		super(Ventas.class);
	}

	public Ventas findByFacturaNumero(String numeroFactura) {
		  try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Query<Ventas> query = session.createQuery(
					"select v " + "from Ventas v " + "join fetch v.factura f " + "where f.numero = :num", Ventas.class);
			query.setParameter("num", numeroFactura);
			// uniqueResult() retorna la entidad o null si no hay coincidencia
			return query.uniqueResult();
		}
	}
}