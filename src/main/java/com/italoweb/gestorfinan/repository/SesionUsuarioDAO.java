package com.italoweb.gestorfinan.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.model.SesionUsuario;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class SesionUsuarioDAO extends GenericDAOImpl<SesionUsuario, Long> {

    public SesionUsuarioDAO() {
        super(SesionUsuario.class);
    }

    public void registrarInicioSesion(SesionUsuario sesion) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(sesion);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public void actualizarFinSesion(String sessionId, LocalDateTime horaFin) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<SesionUsuario> query = session.createQuery(
                "FROM SesionUsuario WHERE sessionId = :sessionId AND activo = true", SesionUsuario.class);
            query.setParameter("sessionId", sessionId);
            SesionUsuario sesion = query.uniqueResult();

            if (sesion != null) {
                sesion.setHoraFin(horaFin);
                sesion.setActivo(false);
                session.merge(sesion);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public List<SesionUsuario> listarTodas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM SesionUsuario ORDER BY id DESC", SesionUsuario.class).list();
        }
    }
}
