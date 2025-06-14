package com.italoweb.gestorfinan.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.ParametrosGenerales;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class ParametrosGeneralesDAO extends GenericDAOImpl<ParametrosGenerales, Long> {
	public ParametrosGeneralesDAO() {
		super(ParametrosGenerales.class);
	}
	
	public Long obtenerCantidadRegistro() {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        String sql = "SELECT COUNT(*) FROM Parametros_generales";
	        Query<Long> query = session.createNativeQuery(sql, Long.class);
	        return query.uniqueResult();
	    }
	}
	
	public ParametrosGenerales obtenerUnicoRegistro() {
	    List<ParametrosGenerales> lista = findAll();
	    return lista.isEmpty() ? null : lista.get(0);
	}
	
}
