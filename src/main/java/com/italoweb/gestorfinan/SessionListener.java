package com.italoweb.gestorfinan;

import java.time.LocalDateTime;

import com.italoweb.gestorfinan.repository.SesionUsuarioDAO;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // No hace falta registrar nada aquí aún
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        new SesionUsuarioDAO().actualizarFinSesion(se.getSession().getId(), LocalDateTime.now());
    }
}