package com.italoweb.gestorfinan.repository;

import java.util.List;

import org.hibernate.Session;

import com.italoweb.gestorfinan.model.poblacion.Ciudad;
import com.italoweb.gestorfinan.model.poblacion.Departamento;
import com.italoweb.gestorfinan.model.poblacion.Pais;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class PoblacionDAO extends GenericDAOImpl<Pais, Long> {
	public PoblacionDAO() {
		super(Pais.class);
	}
	
	public List<Pais> listarPaises() {
		System.out.println("método listarPaises");
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        return session.createQuery("FROM Pais", Pais.class).list();
	    }
	}
	
	public List<Departamento> listarDepartamentosPorPais(Long paisId) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        String hql = """
	            FROM Departamento d
	            WHERE d.pais.id = :paisId
	        """;
	        return session.createQuery(hql, Departamento.class)
	                      .setParameter("paisId", paisId)
	                      .list();
	    }
	}

	public List<Ciudad> listarCiudadesPorDepartamento(Long departamentoId) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        String hql = """
	            FROM Ciudad c
	            WHERE c.departamento.id = :departamentoId
	        """;
	        return session.createQuery(hql, Ciudad.class)
	                      .setParameter("departamentoId", departamentoId)
	                      .list();
	    }
	}
	
}