package com.italoweb.gestorfinan.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.Concepto;
import com.italoweb.gestorfinan.model.TipoMovimiento;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class ConceptoDAO extends GenericDAOImpl<Concepto, Long> {
	public ConceptoDAO() {
		super(Concepto.class);
	}

	public List<Concepto> findByTipoMovimiento(TipoMovimiento tipo) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = "FROM Concepto c WHERE c.tipoMovimiento = :tipoMovimiento";
			Query<Concepto> query = session.createQuery(hql, Concepto.class);
			query.setParameter("tipoMovimiento", tipo);
			return query.getResultList();
		}
	}
}
