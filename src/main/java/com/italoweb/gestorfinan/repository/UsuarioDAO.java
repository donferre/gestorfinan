package com.italoweb.gestorfinan.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.Estado;
import com.italoweb.gestorfinan.model.Usuario;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class UsuarioDAO extends GenericDAOImpl<Usuario, Long> {
    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Usuario findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE username = :username", Usuario.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        }
    }
    
    public void actualizarEstado(Estado estado, Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            @SuppressWarnings("deprecation")
			Query<?> query = session.createQuery("UPDATE Usuario SET estado = :estado WHERE id = :id");
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