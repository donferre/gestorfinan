package com.italoweb.gestorfinan.repository;


import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.Cliente;
import com.italoweb.gestorfinan.model.Estado;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class ClienteDAO extends GenericDAOImpl<Cliente, Long> {
    public ClienteDAO() {
        super(Cliente.class);
    }

    public void actualizarEstado(Estado estado, Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            @SuppressWarnings("deprecation")
			Query<?> query = session.createQuery("UPDATE Cliente SET estado = :estado WHERE id = :id");
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
    
    public List<Cliente> filtroEstadoActivo(Estado estado) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = "FROM Cliente p WHERE p.estado = :estado";
			Query<Cliente> query = session.createQuery(hql, Cliente.class);
			query.setParameter("estado", estado);
			return query.getResultList();
		}
	}

}