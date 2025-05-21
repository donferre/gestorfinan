package com.italoweb.gestorfinan.controller;

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

import com.italoweb.gestorfinan.model.Usuario;
import com.italoweb.gestorfinan.navigation.MenuModel;
import com.italoweb.gestorfinan.repository.UsuarioDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.MD5Validator;

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
            if (validacion){
                Session session = Sessions.getCurrent();
                session.setAttribute("usuario", usuario);
                String bookmark = MenuModel.bookmarkUrl("/inicio.zul");
                Executions.sendRedirect("index.zul"+bookmark); // Página principal
            }else{
                Messagebox.show("contraseña incorrecta", "Error", Messagebox.OK, Messagebox.ERROR);
            }
        } else {
            Messagebox.show("Usuario incorrecto", "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public static void logout() {
        Session session = Sessions.getCurrent();
        session.removeAttribute("usuario");
        Executions.sendRedirect("/");
    }
}
