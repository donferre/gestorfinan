package com.italoweb.gestorfinan.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.config.Apariencia;
import com.italoweb.gestorfinan.config.AparienciaManager;
import com.italoweb.gestorfinan.model.SesionUsuario;
import com.italoweb.gestorfinan.model.Usuario;
import com.italoweb.gestorfinan.navigation.MenuModel;
import com.italoweb.gestorfinan.repository.SesionUsuarioDAO;
import com.italoweb.gestorfinan.repository.UsuarioDAO;
import com.italoweb.gestorfinan.util.AppProperties;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.MD5Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class LoginController extends Window implements AfterCompose {

	private static final long serialVersionUID = 3696052722965637202L;
	private Textbox txtUsuario, txtPassword;
	private Button button;
	private UsuarioDAO usuarioDAO;
	private Image img_login;
	private static final String LOGO_DIR = AppProperties.get("app.src").concat("/config/images");
	private static final String LOGO_FILENAME = "logo_app.jpg";

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
		cargarLogoActual();
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

	private void mostrarPreview(byte[] data) {
		String base64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(data);
		AparienciaManager aparienciaManager = new AparienciaManager();
		List<Apariencia> apariencia = aparienciaManager.getApariencia();
		String widthLogo = "100px";
		if (apariencia.size() > 0) {
			int size = apariencia.get(0).getSizeLogo();
			if (Objects.nonNull(size)) {
				widthLogo = size + "px";
			}
		}
		this.img_login.setSrc(base64);
		this.img_login.setWidth(widthLogo);
	}

	private void cargarLogoActual() {
		File file = new File(LOGO_DIR, LOGO_FILENAME);
		if (file.exists()) {
			try {
				byte[] data = Files.readAllBytes(file.toPath());
				mostrarPreview(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
