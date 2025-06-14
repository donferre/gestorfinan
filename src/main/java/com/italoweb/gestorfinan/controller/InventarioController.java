package com.italoweb.gestorfinan.controller;

import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.dto.InventarioResumenDTO;
import com.italoweb.gestorfinan.repository.InventarioDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class InventarioController extends Window implements AfterCompose {
	private static final long serialVersionUID = -6732848727890683953L;

	private Textbox text_filtrar_inventarios;
	private Listbox listbox_inventarios;

	private InventarioDAO inventarioDAO = new InventarioDAO();

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.cargarListaInventarios();
		this.isMobile();
	}

	public void cargarListaInventarios() {
		this.text_filtrar_inventarios.setValue("");
		this.listbox_inventarios.getItems().clear();
		inventarioDAO.obtenerResumenInventario().forEach(this::crearListitem);
	}

	public void isMobile() {
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Listhead listhead = listbox_inventarios.getListhead();
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

	private void crearListitem(InventarioResumenDTO inventario) {
		Listitem listitem = new Listitem();
		this.actualizarListitem(inventario, listitem);
		this.listbox_inventarios.appendChild(listitem);
	}

	private void actualizarListitem(InventarioResumenDTO inventario, Listitem listitem) {
		listitem.getChildren().clear();
		listitem.appendChild(new Listcell(inventario.getCodigoProducto()));
		listitem.appendChild(new Listcell(inventario.getNombreProducto()));
		listitem.appendChild(new Listcell(inventario.getStockActual().toString()));
		listitem.appendChild(new Listcell(FormatoUtil.formatearFechaCompleta(inventario.getUltimaActualizacion())));

		listitem.setValue(inventario);
	}

	public void filtrarListInventarios(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.listbox_inventarios.getItems()) {
			InventarioResumenDTO p = listitem.getValue();
			boolean visible = filter.isEmpty()
					|| (p.getCodigoProducto() != null && p.getCodigoProducto().toUpperCase().contains(filter))
					|| (p.getNombreProducto() != null && p.getNombreProducto().toUpperCase().contains(filter))
					|| (p.getStockActual() != null && p.getStockActual().toString().contains(filter));

			listitem.setVisible(visible);
		}
	}
}
