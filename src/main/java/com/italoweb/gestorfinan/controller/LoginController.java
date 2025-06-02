package com.italoweb.gestorfinan.controller;

import java.time.LocalDateTime;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.SesionUsuario;
import com.italoweb.gestorfinan.model.Usuario;
import com.italoweb.gestorfinan.navigation.MenuModel;
import com.italoweb.gestorfinan.repository.SesionUsuarioDAO;
import com.italoweb.gestorfinan.repository.UsuarioDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.MD5Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class LoginController extends Window implements AfterCompose {
	
    private static final long serialVersionUID = 3696052722965637202L;
	private Textbox txtUsuario, txtPassword;
    private Button button;
    private UsuarioDAO usuarioDAO;

    @Override
    public void afterCompose() {
        ComponentsUtil.connectVariablesController(this);
        this.usuarioDAO = new UsuarioDAO();
        this.txtUsuario.setValue("admin");
        this.txtPassword.setValue("12345");
        this.button.addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {
            private static final long serialVersionUID = 4519346807772532271L;
            @Override
            public void onEvent(Event arg0) throws Exception {
                login();
            }
        });
    }


    public void login() {
        String username = txtUsuario.getValue().trim();
        String password = txtPassword.getValue().trim();

        Usuario usuario = this.usuarioDAO.findByUsername(username);
        if (usuario != null) {
            boolean validacion = MD5Validator.validateMD5(password, usuario.getPassword());
            if (validacion) {
                Session zkSession = Sessions.getCurrent();
                HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
                HttpSession httpSession = request.getSession();

                // Guardar usuario en sesión
                zkSession.setAttribute("usuario", usuario);

                // Guardar sesión en base de datos
                SesionUsuario sesion = new SesionUsuario();
                sesion.setUsuario(usuario);
                sesion.setHoraInicio(LocalDateTime.now());
                sesion.setActivo(true);
                sesion.setSessionId(httpSession.getId());

                new SesionUsuarioDAO().registrarInicioSesion(sesion);

                // Redirigir
                String bookmark = MenuModel.bookmarkUrl("/inicio.zul");
                Executions.sendRedirect("index.zul" + bookmark);
            } else {
                Messagebox.show("Contraseña incorrecta", "Error", Messagebox.OK, Messagebox.ERROR);
            }
        } else {
            Messagebox.show("Usuario incorrecto", "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }


    public static void logout() {
        Session zkSession = Sessions.getCurrent();
        HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        HttpSession httpSession = request.getSession();

        try {
            // Actualizar sesión como finalizada en base de datos
            new SesionUsuarioDAO().actualizarFinSesion(httpSession.getId(), LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace(); // O usar logger
        }

        // Limpiar sesión y redirigir
        zkSession.removeAttribute("usuario");
        zkSession.invalidate();
        Executions.sendRedirect("/");
    }
}
