package com.italoweb.gestorfinan.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.CategoriaNota;
import com.italoweb.gestorfinan.model.Nota;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class NotaDAO extends GenericDAOImpl<Nota, Long> {
    public NotaDAO() {
        super(Nota.class);
    }

    public List<Nota> filtrarxCategoriaId(CategoriaNota id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Nota n WHERE n.categoriaNota = :id_categoria_nota";
            return session
                    .createQuery(hql, Nota.class)
                    .setParameter("id_categoria_nota", id)
                    .getResultList();
        }
    }

    public void actualizarCategoriaNotaPorIdNota(Long idNota, CategoriaNota nuevaCategoria) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            String hql = "UPDATE Nota n SET n.categoriaNota = :nuevaCategoria WHERE n.id = :idNota";
            @SuppressWarnings("deprecation")
			Query<?> query = session.createQuery(hql);
            query.setParameter("nuevaCategoria", nuevaCategoria);
            query.setParameter("idNota", idNota);

            @SuppressWarnings("unused")
			int filasActualizadas = query.executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

}
