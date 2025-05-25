package com.italoweb.gestorfinan.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.Proveedor;
import com.italoweb.gestorfinan.model.poblacion.Ciudad;
import com.italoweb.gestorfinan.repository.PoblacionDAO;
import com.italoweb.gestorfinan.repository.ProveedorDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;

public class ProveedorController extends Window implements AfterCompose {
	private static final long serialVersionUID = 7513499911306546485L;
	private Textbox text_filtro_proveedor;
	private Listbox listbox_proveedor;
	private Map<String, Listitem> listitems = new HashMap<String, Listitem>();

	private Window win_proveedor_form;
	private Textbox text_nit;
	private Textbox text_nombre;
	private Textbox text_email;
	private Textbox text_telefono;
	private Combobox comb_ciudad;
	private Textbox text_direccion;
	private Textbox text_departamento;
	private Textbox text_pais;

	private ProveedorDAO proveedorDAO;
	private PoblacionDAO poblacionDAO;

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.proveedorDAO = new ProveedorDAO();
		this.poblacionDAO = new PoblacionDAO();
		this.cargarComponentes();
		this.cargarListaProveedor();
		this.isMobile();
	}

	public void cargarComponentes() {
		this.text_telefono.setMaxlength(14);
		this.text_nit.setMaxlength(12);
		this.text_nombre.setMaxlength(20);
		this.text_direccion.setMaxlength(25);
		this.text_telefono.setZclass("none");
		this.text_telefono.setWidgetListener("onBind", "jq(this.getInputNode()).mask('(999) 999-9999');");
		cargarCiudad();
	}

	public void cargarListaProveedor() {
		this.text_filtro_proveedor.setValue("");
		this.listbox_proveedor.getItems().clear();
		proveedorDAO.findAll().forEach(this::createListitem);
	}

	public void cargarCiudad() {
		this.comb_ciudad.setAutocomplete(false);
		List<Ciudad> listaCiudades = this.poblacionDAO.listarCiudadesConDepartamentoYPais();
		this.comb_ciudad.getItems().clear();
		for (Ciudad ciudad : listaCiudades) {
			this.comb_ciudad.appendChild(ComponentsUtil.getComboitem(ciudad.getNombre(), null, ciudad));
		}
	}

	public void actualizarDepartamentoYPais() {
		Comboitem selectedItem = comb_ciudad.getSelectedItem();
		if (selectedItem != null) {
			Ciudad ciudad = (Ciudad) selectedItem.getValue();
			if (ciudad != null) {
				text_departamento.setValue(ciudad.getDepartamento().getNombre());
				text_pais.setValue(ciudad.getDepartamento().getPais().getNombre());
			}
		} else {
			text_departamento.setValue("");
			text_pais.setValue("");
		}
	}

	public void isMobile() {
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Listhead listhead = listbox_proveedor.getListhead();
		if (listhead != null) {
			List<Listheader> headers = listhead.getChildren();
			for (Listheader header : headers) {
				if (esMovil) {
					header.setHflex("min");
				} else {
					header.setHflex("true");
				}
			}
		}
	}

	private void createListitem(Proveedor proveedor) {
		Listitem listitem = new Listitem();
		this.updateListitem(proveedor, listitem);
		this.listitems.put(proveedor.getId().toString(), listitem);
		this.listbox_proveedor.appendChild(listitem);
	}

	private void updateListitem(Proveedor proveedor, Listitem listitem) {
		listitem.getChildren().clear();
		listitem.appendChild(new Listcell(proveedor.getNit().toString()));
		listitem.appendChild(new Listcell(proveedor.getNombre()));
		listitem.appendChild(new Listcell(proveedor.getEmail()));
		listitem.appendChild(new Listcell(proveedor.getTelefono()));
		listitem.appendChild(new Listcell(proveedor.getCiudad().getNombre()));
		listitem.appendChild(new Listcell(proveedor.getDireccion()));

		/* Acciones */
		Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
		btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
			this.cargarWinProveedorForm(proveedor);
		});
		Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
		btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
			this.eliminarProveedor(proveedor);
		});
		
		Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
		listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

		listitem.setValue(proveedor);
	}

	public void filtrarListProveedor(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.listbox_proveedor.getItems()) {
			Proveedor attribute = listitem.getValue();
			boolean matchNit = attribute.getNit().toUpperCase().contains(filter);
			boolean matchNombre = attribute.getNombre().toUpperCase().contains(filter);
			boolean matchEmail = attribute.getEmail().toUpperCase().contains(filter);
			boolean matchTelefono = attribute.getTelefono().toUpperCase().contains(filter);
			boolean matchCiudad = attribute.getCiudad().getNombre().toUpperCase().contains(filter);
			boolean matchDireccion = attribute.getDireccion().toUpperCase().contains(filter);
			listitem.setVisible(filter.isEmpty() || matchNit || matchNombre || matchEmail || matchTelefono
					|| matchCiudad || matchDireccion);
		}
	}

	private void eliminarProveedor(Proveedor proveedor) {
		DialogUtil.showConfirmDialog("¿Está seguro de que desea eliminar este registro?", "Confirmación")
				.thenAccept(confirmed -> {
					if (confirmed) {
						this.proveedorDAO.delete(proveedor);
						this.cargarListaProveedor();
						DialogUtil.showShortMessage("success", "Proveedor Eliminado Exitosamente");
					}
				});
	}

	public void filtrarComboCiudad(String filter) {
		filtrarCombo(this.comb_ciudad, filter);
	}

	public void filtrarCombo(Combobox combo, String filter) {
		filter = filter.trim().toUpperCase();
		for (Comboitem comboItem : combo.getItems()) {
			String label = comboItem.getLabel();
			comboItem.setVisible(filter.isEmpty() || label.toUpperCase().contains(filter));
		}
	}

	public void cargarWinProveedorForm(Proveedor proveedor) {
		this.text_nit.setValue("");
		this.text_nombre.setValue("");
		this.text_email.setValue("");
		this.text_telefono.setValue("");
		this.comb_ciudad.setValue("");
		this.text_direccion.setValue("");
		this.text_departamento.setValue("");
		this.text_pais.setValue("");

		if (proveedor != null) {
			this.text_nit.setValue(proveedor.getNit());
			this.text_nombre.setValue(proveedor.getNombre());
			this.text_email.setValue(proveedor.getEmail());
			this.text_telefono.setValue(proveedor.getTelefono());
			this.comb_ciudad.setValue(proveedor.getCiudad().getNombre());
			this.text_departamento.setValue(proveedor.getCiudad().getDepartamento().getNombre());
			this.text_pais.setValue(proveedor.getCiudad().getDepartamento().getPais().getNombre());
			this.text_direccion.setValue(proveedor.getDireccion());
		}
		this.win_proveedor_form.setAttribute("CLIENTE", proveedor);
		this.win_proveedor_form.doModal();
	}

	public void guardarWinProveedorForm() {
		String nit = this.text_nit.getValue().trim();
		String nombre = this.text_nombre.getValue().trim();
		String email = this.text_email.getValue().trim();
		String telefono = this.text_telefono.getValue();
		Ciudad ciudad = comb_ciudad.getSelectedItem() != null ? (Ciudad) comb_ciudad.getSelectedItem().getValue()
				: null;
		String direccion = this.text_direccion.getValue();
		String mensaje = "Proveedor Guardado Exitosamente";

		// Validaciones
		if (StringUtils.isBlank(nit)) {
			DialogUtil.showError("El NIT es Obligatorio.");
			return;
		}
		if (StringUtils.isBlank(nombre)) {
			DialogUtil.showError("El nombre es Obligatorio.");
			return;
		}
		if (StringUtils.isBlank(email)) {
			DialogUtil.showError("El E-mail es Obligatorio.");
			return;
		}
		if (telefono.length() != 14) {
			DialogUtil.showError("El telefono no cumple la longitud.");
			return;
		}
		if (ciudad == null) {
			DialogUtil.showError("La ciudad es obligatoria.");
			return;
		}
		if (StringUtils.isBlank(direccion)) {
			DialogUtil.showError("La direccion es Obligatoria.");
			return;
		}

		Proveedor proveedor = (Proveedor) this.win_proveedor_form.getAttribute("CLIENTE");

		if (proveedor == null) {
			proveedor = new Proveedor();
			proveedor.setNit(nit);
			proveedor.setNombre(nombre);
			proveedor.setEmail(email);
			proveedor.setTelefono(telefono);
			proveedor.setCiudad(ciudad); 
			proveedor.setDireccion(direccion);
			this.proveedorDAO.save(proveedor);
		} else {
			proveedor.setNit(nit);
			proveedor.setNombre(nombre);
			proveedor.setEmail(email);
			proveedor.setTelefono(telefono);
			proveedor.setCiudad(ciudad); 
			proveedor.setDireccion(direccion);
			this.proveedorDAO.update(proveedor);
			mensaje = "Proveedor Editado Exitosamente";
		}
		this.cargarListaProveedor();
		this.win_proveedor_form.setVisible(false);
		DialogUtil.showShortMessage("success", mensaje);
	}
}