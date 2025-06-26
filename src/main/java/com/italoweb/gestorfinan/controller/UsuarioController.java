package com.italoweb.gestorfinan.controller;

import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.components.Switch;
import com.italoweb.gestorfinan.model.Estado;
import com.italoweb.gestorfinan.model.Roles;
import com.italoweb.gestorfinan.model.Usuario;
import com.italoweb.gestorfinan.repository.RolesDAO;
import com.italoweb.gestorfinan.repository.UsuarioDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;
import com.italoweb.gestorfinan.util.MD5Validator;

public class UsuarioController extends Window implements AfterCompose {
	private static final long serialVersionUID = -3579867759499821765L;

	private Textbox text_filtrar_usuario;
	private Listbox listbox_usuario;
	private Window win_usuario_form;
	private Textbox text_username;
	private Textbox text_descripción;
	private Textbox password;
	private Combobox comb_rol;
	private Label togglePassword;

	private UsuarioDAO usuarioDAO;
	private RolesDAO rolesDAO;

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.usuarioDAO = new UsuarioDAO();
		this.rolesDAO = new RolesDAO();
		this.cargarListaUsuario();
		cargarRoles();
		this.isMobile();
	}

	public void cargarRoles() {
		this.comb_rol.setAutocomplete(false);
		List<Roles> listaRoles = this.rolesDAO.findAll();
		for (Roles rol : listaRoles) {
			this.comb_rol.appendChild(ComponentsUtil.getComboitem(rol.getNombre(), null, rol));
		}
	}

	public void cargarListaUsuario() {
		this.text_filtrar_usuario.setValue("");
		this.listbox_usuario.getItems().clear();
		usuarioDAO.findAll().forEach(this::crearListitem);
	}

	public void isMobile() {
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Listhead listhead = listbox_usuario.getListhead();
		if (listhead != null) {
			List<Listheader> headers = listhead.getChildren();
			for (Listheader header : headers) {
				if (esMovil) {
					header.setHflex("min");
				} else {
					header.setHflex("");
				}
			}
		}
	}

	private void crearListitem(Usuario usuario) {
		Listitem listitem = new Listitem();
		this.actualizarListitem(usuario, listitem);
		this.listbox_usuario.appendChild(listitem);
	}

	private void actualizarListitem(Usuario usuario, Listitem listitem) {
		listitem.getChildren().clear();
		listitem.appendChild(new Listcell(usuario.getUsername().toUpperCase()));
		listitem.appendChild(new Listcell(usuario.getDescripcion()));
		listitem.appendChild(new Listcell(usuario.getRoles().getNombre().toUpperCase()));

		/* Estado */
		Switch checkActive = ComponentsUtil.getSwitch(usuario.getEstado().equals(Estado.ACTIVO));
		checkActive.addEventListener(Switch.ON_TOGGLE, new SerializableEventListener<Event>() {
			private static final long serialVersionUID = 1711205549535046996L;

			public void onEvent(Event event) throws Exception {
				boolean check = (Boolean) event.getData();
				Estado estado = check ? Estado.ACTIVO : Estado.INACTIVO;
				usuarioDAO.actualizarEstado(estado, usuario.getId_usuario());
			}
		});
		listitem.appendChild(ComponentsUtil.getListcell(null, checkActive));
		/* Acciones */
		Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
		btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
			this.cargarWinUsuarioForm(usuario);
		});
		Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
		btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
			this.eliminarUsuario(usuario);
		});
		Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
		listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

		listitem.setValue(usuario);
	}

	public void filtrarListUsuario(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.listbox_usuario.getItems()) {
			Usuario attribute = listitem.getValue();
			listitem.setVisible(filter.isEmpty() || attribute.getUsername().toUpperCase().contains(filter)
					|| attribute.getRoles().getNombre().toUpperCase().contains(filter));
		}
	}

	private void eliminarUsuario(Usuario usuario) {
		this.usuarioDAO.delete(usuario);
		this.cargarListaUsuario();
	}

	public void filtrarRol(String filter) {
		filtrarCombo(this.comb_rol, filter);
	}

	public void filtrarCombo(Combobox combo, String filter) {
		filter = filter.trim().toUpperCase();
		for (Comboitem comboItem : combo.getItems()) {
			String label = comboItem.getLabel();
			comboItem.setVisible(filter.isEmpty() || label.toUpperCase().contains(filter));
		}
	}

	public void cargarWinUsuarioForm(Usuario usuario) {
		this.text_username.setValue("");
		this.text_descripción.setValue("");
		this.password.setValue("");
		this.comb_rol.setValue("");
		if (usuario != null) {
			this.text_username.setValue(usuario.getUsername().toUpperCase());
			this.text_descripción.setValue(usuario.getDescripcion());
			this.password.setValue(usuario.getPassword());
			ComponentsUtil.setComboboxValue(this.comb_rol, usuario.getRoles());
		}
		this.win_usuario_form.setAttribute("usuario", usuario);
		this.win_usuario_form.doModal();
	}

	public void guardarWinUsuarioForm() {
		if (!validarDatos()) {
			return;
		}
		String username = this.text_username.getValue().trim();
		String descripción = this.text_descripción.getValue().trim();
		String password = this.password.getValue().trim();
		String hashedPassword = MD5Validator.encriptar(password);

		Roles rol = this.comb_rol.getSelectedItem() != null ? this.comb_rol.getSelectedItem().getValue() : null;

		Usuario usuario = (Usuario) this.win_usuario_form.getAttribute("usuario");
		String mensaje = "Usuario Guardado Exitosamente.";
		if (usuario == null) {
			usuario = new Usuario();
			usuario.setUsername(username.toUpperCase());
			usuario.setDescripcion(descripción);
			usuario.setPassword(hashedPassword);
			usuario.setRoles(rol);
			usuario.setEstado(Estado.ACTIVO);
			this.usuarioDAO.save(usuario);
		} else {
			usuario.setUsername(username.toUpperCase());
			usuario.setDescripcion(descripción);
			usuario.setPassword(hashedPassword);
			usuario.setRoles(rol);
			this.usuarioDAO.update(usuario);
			mensaje = "Usuario Editado Exitosamente";
		}
		this.cargarListaUsuario();
		this.win_usuario_form.setVisible(false);
		DialogUtil.showShortMessage("success", mensaje);
	}

	private boolean validarDatos() {
		if (text_username.getValue() == null || text_username.getValue().isEmpty()) {
			DialogUtil.showError("Debe ingresar un nombre.");
			return false;
		}
		if (text_descripción.getValue() == null || text_descripción.getValue().isEmpty()) {
			DialogUtil.showError("Debe ingresar una descripción.");
			return false;
		}
		if (password.getValue() == null || password.getValue().isEmpty()) {
			DialogUtil.showError("Debe ingresar una contraseña.");
			return false;
		}
		if (comb_rol.getValue() == null) {
			DialogUtil.showError("El rol debe ser asignado.");
			return false;
		}
		return true;
	}
	
	public void togglePasswordVisibility() {
		if ("password".equals(password.getType())) {
			password.setType("text");
			togglePassword.setSclass("fas fa-eye-slash"); // cambia a ícono de ocultar
		} else {
			password.setType("password");
			togglePassword.setSclass("fas fa-eye"); // cambia a ícono de mostrar
		}
	}
}
