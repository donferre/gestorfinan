package com.italoweb.gestorfinan.controller;

import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.Roles;
import com.italoweb.gestorfinan.repository.RolesDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;

public class RolesController extends Window implements AfterCompose {
	private static final long serialVersionUID = -3579867759499821765L;

	private Textbox text_filtrar_rol;
	private Listbox listbox_rol;
	private Window win_rol_form;
	private Textbox text_nombre;

	private RolesDAO rolesDAO;

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.rolesDAO = new RolesDAO();
		this.cargarListaRoles();
		this.isMobile();
	}

	public void cargarListaRoles() {
		this.text_filtrar_rol.setValue("");
		this.listbox_rol.getItems().clear();
		rolesDAO.findAll().forEach(this::crearListitem);
	}

	public void isMobile() {
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Listhead listhead = listbox_rol.getListhead();
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

	private void crearListitem(Roles roles) {
		Listitem listitem = new Listitem();
		this.actualizarListitem(roles, listitem);
		this.listbox_rol.appendChild(listitem);
	}

	private void actualizarListitem(Roles roles, Listitem listitem) {
		listitem.getChildren().clear();
		listitem.appendChild(new Listcell(roles.getId().toString()));
		listitem.appendChild(new Listcell(roles.getNombre().toUpperCase()));

		/* Acciones */
		Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
		btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
			this.cargarWinRolesForm(roles);
		});
		Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
		btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
			this.eliminarRoles(roles);
		});
		Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
		listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

		listitem.setValue(roles);
	}

	public void filtrarListRoles(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.listbox_rol.getItems()) {
			Roles attribute = listitem.getValue();
			listitem.setVisible(filter.isEmpty() || attribute.getNombre().toUpperCase().contains(filter));
		}
	}

	private void eliminarRoles(Roles roles) {
		this.rolesDAO.delete(roles);
		this.cargarListaRoles();
	}

	public void cargarWinRolesForm(Roles roles) {
		this.text_nombre.setValue("");
		if (roles != null) {
			this.text_nombre.setValue(roles.getNombre().toUpperCase());
		}
		this.win_rol_form.setAttribute("rol", roles);
		this.win_rol_form.doModal();
	}

	public void guardarWinRolesForm() {
		if (!validarDatos()) {
			return;
		}
		String nombre = this.text_nombre.getValue().trim();
		Roles roles = (Roles) this.win_rol_form.getAttribute("rol");
		String mensaje = "Unidad Guardada Exitosamente.";
		if (roles == null) {
			roles = new Roles();
			roles.setNombre(nombre.toUpperCase());
			this.rolesDAO.save(roles);
		} else {
			roles.setNombre(nombre.toUpperCase());
			this.rolesDAO.update(roles);
			mensaje = "Unidad Editada Exitosamente";
		}
		this.cargarListaRoles();
		this.win_rol_form.setVisible(false);
		DialogUtil.showShortMessage("success", mensaje);
	}

	private boolean validarDatos() {
		if (text_nombre.getValue() == null || text_nombre.getValue().isEmpty()) {
			DialogUtil.showError("Debe ingresar un nombre.");
			return false;
		}
		return true;
	}
}
