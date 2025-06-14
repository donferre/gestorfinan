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

import com.italoweb.gestorfinan.model.UnidadCompra;
import com.italoweb.gestorfinan.repository.UnidadCompraDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;

public class UnidadCompraController extends Window implements AfterCompose {
	private static final long serialVersionUID = -3579867759499821765L;

	private Textbox text_filtrar_unidad_compra;
	private Listbox listbox_unidad_compra;
	private Window win_unidad_compra_form;
	private Textbox text_nombre;

	private UnidadCompraDAO unidadCompraDAO;

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.unidadCompraDAO = new UnidadCompraDAO();
		this.cargarListaUnidadCompra();
		this.isMobile();
	}

	public void cargarListaUnidadCompra() {
		this.text_filtrar_unidad_compra.setValue("");
		this.listbox_unidad_compra.getItems().clear();
		unidadCompraDAO.findAll().forEach(this::crearListitem);
	}

	public void isMobile() {
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Listhead listhead = listbox_unidad_compra.getListhead();
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

	private void crearListitem(UnidadCompra unidadCompra) {
		Listitem listitem = new Listitem();
		this.actualizarListitem(unidadCompra, listitem);
		this.listbox_unidad_compra.appendChild(listitem);
	}

	private void actualizarListitem(UnidadCompra unidadCompra, Listitem listitem) {
		listitem.getChildren().clear();
		listitem.appendChild(new Listcell(unidadCompra.getId().toString()));
		listitem.appendChild(new Listcell(unidadCompra.getNombre()));

		/* Acciones */
		Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
		btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
			this.cargarWinUnidadCompraForm(unidadCompra);
		});
		Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
		btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
			this.eliminarUnidadCompra(unidadCompra);
		});
		Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
		listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

		listitem.setValue(unidadCompra);
	}

	public void filtrarListUnidadCompra(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.listbox_unidad_compra.getItems()) {
			UnidadCompra attribute = listitem.getValue();
			listitem.setVisible(filter.isEmpty() || attribute.getNombre().toUpperCase().contains(filter));
		}
	}

	private void eliminarUnidadCompra(UnidadCompra unidadCompra) {
		this.unidadCompraDAO.delete(unidadCompra);
		this.cargarListaUnidadCompra();
	}

	public void cargarWinUnidadCompraForm(UnidadCompra unidadCompra) {
		this.text_nombre.setValue("");
		if (unidadCompra != null) {
			this.text_nombre.setValue(unidadCompra.getNombre());
		}
		this.win_unidad_compra_form.setAttribute("UNIDAD_COMPRA", unidadCompra);
		this.win_unidad_compra_form.doModal();
	}

	public void guardarWinUnidadCompraForm() {
		if (!validarDatos()) {
			return;
		}
		String nombre = this.text_nombre.getValue().trim();
		UnidadCompra unidadCompra = (UnidadCompra) this.win_unidad_compra_form.getAttribute("UNIDAD_COMPRA");
		String mensaje = "Unidad Guardada Exitosamente.";
		if (unidadCompra == null) {
			unidadCompra = new UnidadCompra();
			unidadCompra.setNombre(nombre);
			this.unidadCompraDAO.save(unidadCompra);
		} else {
			unidadCompra.setNombre(nombre);
			this.unidadCompraDAO.update(unidadCompra);
			mensaje = "Unidad Editada Exitosamente";
		}
		this.cargarListaUnidadCompra();
		this.win_unidad_compra_form.setVisible(false);
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
