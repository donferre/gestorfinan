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

import com.italoweb.gestorfinan.model.Categoria;
import com.italoweb.gestorfinan.repository.CategoriaDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;

public class CategoriaController extends Window implements AfterCompose {
	private static final long serialVersionUID = -3579867759499821765L;

	private Textbox text_filtrar_categoria;
	private Listbox listbox_categoria;
	private Window win_categoria_form;
	private Textbox text_nombre;

	private CategoriaDAO categoriaDAO;

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.categoriaDAO = new CategoriaDAO();
		this.cargarListaCategoria();
		this.isMobile();
	}

	public void cargarListaCategoria() {
		this.text_filtrar_categoria.setValue("");
		this.listbox_categoria.getItems().clear();
		categoriaDAO.findAll().forEach(this::crearListitem);
	}

	public void isMobile() {
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Listhead listhead = listbox_categoria.getListhead();
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

	private void crearListitem(Categoria categoria) {
		Listitem listitem = new Listitem();
		this.actualizarListitem(categoria, listitem);
		this.listbox_categoria.appendChild(listitem);
	}

	private void actualizarListitem(Categoria categoria, Listitem listitem) {
		listitem.getChildren().clear();
		listitem.appendChild(new Listcell(categoria.getId().toString()));
		listitem.appendChild(new Listcell(categoria.getNombre()));

		/* Acciones */
		Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
		btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
			this.cargarWinCategoriaForm(categoria);
		});
		Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
		btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
			this.eliminarCategoria(categoria);
		});
		Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
		listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

		listitem.setValue(categoria);
	}

	public void filtrarListCategoria(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.listbox_categoria.getItems()) {
			Categoria attribute = listitem.getValue();
			listitem.setVisible(filter.isEmpty() || attribute.getNombre().toUpperCase().contains(filter));
		}
	}

	private void eliminarCategoria(Categoria categoria) {
		this.categoriaDAO.delete(categoria);
		this.cargarListaCategoria();
	}

	public void cargarWinCategoriaForm(Categoria categoria) {
		this.text_nombre.setValue("");
		if (categoria != null) {
			this.text_nombre.setValue(categoria.getNombre());
		}
		this.win_categoria_form.setAttribute("categoria", categoria);
		this.win_categoria_form.doModal();
	}

	public void guardarWinCategoriaForm() {
		if (!validarDatos()) {
			return;
		}
		String nombre = this.text_nombre.getValue().trim();
		Categoria categoria = (Categoria) this.win_categoria_form.getAttribute("categoria");
		String mensaje = "Categoria Guardada Exitosamente.";
		if (categoria == null) {
			categoria = new Categoria();
			categoria.setNombre(nombre);
			this.categoriaDAO.save(categoria);
		} else {
			categoria.setNombre(nombre);
			this.categoriaDAO.update(categoria);
			mensaje = "Categoria Editada Exitosamente";
		}
		this.cargarListaCategoria();
		this.win_categoria_form.setVisible(false);
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
