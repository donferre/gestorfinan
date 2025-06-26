package com.italoweb.gestorfinan.controller;

import java.math.BigDecimal;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.Impuesto;
import com.italoweb.gestorfinan.repository.ImpuestoDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class ImpuestoController  extends Window implements AfterCompose {
	private static final long serialVersionUID = -3579867759499821765L;

	private Textbox text_filtrar_impuesto;
	private Listbox listbox_impuesto;
	private Window win_impuesto_form;
	private Textbox text_codigo;
	private Textbox text_nombre;
	private Decimalbox debx_porcentaje;

	private ImpuestoDAO impuestoDAO;

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.impuestoDAO = new ImpuestoDAO();
		this.cargarListaImpuesto();
		this.isMobile();
	}

	public void cargarListaImpuesto() {
		this.text_filtrar_impuesto.setValue("");
		this.listbox_impuesto.getItems().clear();
		impuestoDAO.findAll().forEach(this::crearListitem);
	}

	public void isMobile() {
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Listhead listhead = listbox_impuesto.getListhead();
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

	private void crearListitem(Impuesto impuesto) {
		Listitem listitem = new Listitem();
		this.actualizarListitem(impuesto, listitem);
		this.listbox_impuesto.appendChild(listitem);
	}

	private void actualizarListitem(Impuesto impuesto, Listitem listitem) {
		listitem.getChildren().clear();
		listitem.appendChild(new Listcell(impuesto.getCodigo()));
		listitem.appendChild(new Listcell(impuesto.getNombre()));
		listitem.appendChild(new Listcell(FormatoUtil.formatearMoneda(impuesto.getPorcentaje())));

		/* Acciones */
		Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
		btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
			this.cargarWinImpuestoForm(impuesto);
		});
		Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
		btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
			this.eliminarImpuesto(impuesto);
		});
		Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
		listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

		listitem.setValue(impuesto);
	}

	public void filtrarListImpuesto(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.listbox_impuesto.getItems()) {
			Impuesto attribute = listitem.getValue();
			listitem.setVisible(filter.isEmpty() || attribute.getNombre().toUpperCase().contains(filter));
		}
	}

	private void eliminarImpuesto(Impuesto impuesto) {
		this.impuestoDAO.delete(impuesto);
		this.cargarListaImpuesto();
	}

	public void cargarWinImpuestoForm(Impuesto impuesto) {
		this.text_codigo.setValue("");
		this.text_nombre.setValue("");
		this.debx_porcentaje.setValue(new BigDecimal(0));
		if (impuesto != null) {
			this.text_codigo.setValue(impuesto.getCodigo());
			this.text_nombre.setValue(impuesto.getNombre().toUpperCase());
			this.debx_porcentaje.setValue(impuesto.getPorcentaje());
		}
		this.win_impuesto_form.setAttribute("IMPUESTO", impuesto);
		this.win_impuesto_form.doModal();
	}

	public void guardarWinImpuestoForm() {
		if (!validarDatos()) {
			return;
		}
		String codigo = this.text_codigo.getValue().trim();
		String nombre = this.text_nombre.getValue().trim();
		BigDecimal porcentaje = this.debx_porcentaje.getValue();
		Impuesto impuesto = (Impuesto) this.win_impuesto_form.getAttribute("IMPUESTO");
		String mensaje = "Impuesto Guardado Exitosamente.";
		if (impuesto == null) {
			impuesto = new Impuesto();
			impuesto.setCodigo(codigo);
			impuesto.setNombre(nombre.toUpperCase());
			impuesto.setPorcentaje(porcentaje);
			this.impuestoDAO.save(impuesto);
		} else {
			impuesto.setCodigo(codigo);
			impuesto.setNombre(nombre.toUpperCase());
			impuesto.setPorcentaje(porcentaje);
			this.impuestoDAO.update(impuesto);
			mensaje = "Impuesto Editada Exitosamente";
		}
		this.cargarListaImpuesto();
		this.win_impuesto_form.setVisible(false);
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
