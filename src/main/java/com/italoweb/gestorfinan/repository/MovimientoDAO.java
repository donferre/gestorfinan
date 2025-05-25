package com.italoweb.gestorfinan.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.Movimiento;
import com.italoweb.gestorfinan.model.TipoMovimiento;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class MovimientoDAO  extends GenericDAOImpl<Movimiento, Long> {
    public MovimientoDAO() {
        super(Movimiento.class);
    }
    
    public List<Movimiento> findByTipoMovimiento(TipoMovimiento tipo) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = "FROM Movimiento c WHERE c.tipoMovimiento = :tipoMovimiento";
			Query<Movimiento> query = session.createQuery(hql, Movimiento.class);
			query.setParameter("tipoMovimiento", tipo);
			return query.getResultList();
		}
	}
}