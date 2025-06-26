package com.italoweb.gestorfinan.repository;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.factura.Factura;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class FacturaDAO extends GenericDAOImpl<Factura, Long> {
	public FacturaDAO() {
		super(Factura.class);
	}

	// Busca por número de factura.
	public Factura buscarPorNumeroFactura(String numero) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Factura f = session.createQuery("select f " + "from Factura f " + "  left join fetch f.detalles det " + // solo
																													// detalles
					"where f.numero = :numero", Factura.class).setParameter("numero", numero).uniqueResult();
			if (f != null) {
				// esto disparará un SELECT separado para cargar la colección impuestos
				Hibernate.initialize(f.getImpuestos());
			}
			return f;
		} finally {
			session.close();
		}
	}

	public String obtenerUltimaFactura() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String sql = """
					    SELECT f.numero
					    FROM facturas f
					    ORDER BY LPAD(REGEXP_REPLACE(f.numero, '^[a-zA-Z. ]+', ''), 10, '0') DESC
					    LIMIT 1
					""";
			Query<String> query = session.createNativeQuery(sql, String.class);
			return query.uniqueResult();
		}
	}

}
