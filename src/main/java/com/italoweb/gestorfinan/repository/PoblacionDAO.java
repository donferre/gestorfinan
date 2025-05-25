package com.italoweb.gestorfinan.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.poblacion.Ciudad;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class PoblacionDAO extends GenericDAOImpl<Ciudad, Long> {
	public PoblacionDAO() {
		super(Ciudad.class);
	}

	public List<Ciudad> listarCiudadesConDepartamentoYPais() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
						SELECT c FROM Ciudad c
						JOIN FETCH c.departamento d
						JOIN FETCH d.pais
					""";
			Query<Ciudad> query = session.createQuery(hql, Ciudad.class);
			return query.getResultList();
		}
	}
}