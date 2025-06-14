package com.italoweb.gestorfinan.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.Estado;
import com.italoweb.gestorfinan.model.Proveedor;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class ProveedorDAO extends GenericDAOImpl<Proveedor, Long> {
	public ProveedorDAO() {
		super(Proveedor.class);
	}
	
	public List<Proveedor> filtroEstadoActivo(Estado estado) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = "FROM Proveedor p WHERE p.estado = :estado";
			Query<Proveedor> query = session.createQuery(hql, Proveedor.class);
			query.setParameter("estado", estado);
			return query.getResultList();
		}
	}

	  public void actualizarEstado(Estado estado, Long id) {
	        Transaction transaction = null;
	        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	            transaction = session.beginTransaction();

	            @SuppressWarnings("deprecation")
				Query<?> query = session.createQuery("UPDATE Proveedor SET estado = :estado WHERE id = :id");
	            query.setParameter("estado", estado);
	            query.setParameter("id", id);

	            @SuppressWarnings("unused")
				int updatedRows = query.executeUpdate();
	            transaction.commit();
	        } catch (Exception e) {
	            if (transaction != null) transaction.rollback();
	            throw e;
	        }
	    }
}
