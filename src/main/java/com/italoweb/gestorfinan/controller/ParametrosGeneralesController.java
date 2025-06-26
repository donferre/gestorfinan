package com.italoweb.gestorfinan.controller;

import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.ParametrosGenerales;
import com.italoweb.gestorfinan.repository.ParametrosGeneralesDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;

public class ParametrosGeneralesController extends Window implements AfterCompose {
	private static final long serialVersionUID = -3579867759499821765L;

	private Textbox text_filtrar_parametros_generales;
	private Listbox listbox_parametros_generales;
	private Window win_parametros_generales_form;
	private Combobox comb_tipo_factura;
	private Combobox comb_responsable_iva;
	private Combobox comb_habilita_precio_venta;
	private Combobox comb_habilita_descuento_total;
	private Textbox text_prefijo_dian;
	private Textbox text_prefijo;
	private Longbox long_consecutivo_dian;
	private Longbox long_consecutivo;
	private Button btnCrear;
	private Listheader lih_predian;
	private Listheader lih_pref;
	private Listheader lih_consdian;
	private Listheader lih_cons;
	private Vlayout grupo_dian;
	private Vlayout grupo_sin_dian;

	private ParametrosGeneralesDAO parametrosGeneralesDAO = new ParametrosGeneralesDAO();

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.cargarListaParametrosGenerales();
		this.isMobile();
		this.inicializarComboboxResponsableIVA();
		inicializarComboboxHabPrecioVenta();
		inicializarComboboxHabDescuentoTotal();
		inicializarComboboxTipoFactura();
		
	}

	public boolean permisoCrear() {
		Long cantidad = parametrosGeneralesDAO.obtenerCantidadRegistro();
		return cantidad != 0;
	}

	private void inicializarComboboxResponsableIVA() {
		comb_responsable_iva.getItems().clear();
		comb_responsable_iva.setReadonly(true);
		agregarItemCombo(comb_responsable_iva, "Sí", "S");
		agregarItemCombo(comb_responsable_iva, "No", "N");
	}
	private void inicializarComboboxTipoFactura() {
		comb_tipo_factura.getItems().clear();
		comb_tipo_factura.setReadonly(true);
		agregarItemCombo(comb_tipo_factura, "80mm", "mm");
		agregarItemCombo(comb_tipo_factura, "Media carta", "mc");
	}

	private void inicializarComboboxHabPrecioVenta() {
		comb_habilita_precio_venta.getItems().clear();
		comb_habilita_precio_venta.setReadonly(true);
		agregarItemCombo(comb_habilita_precio_venta, "Sí", "S");
		agregarItemCombo(comb_habilita_precio_venta, "No", "N");
	}
	
	private void inicializarComboboxHabDescuentoTotal() {
		comb_habilita_descuento_total.getItems().clear();
		comb_habilita_descuento_total.setReadonly(true);
		agregarItemCombo(comb_habilita_descuento_total, "Sí", "S");
		agregarItemCombo(comb_habilita_descuento_total, "No", "N");
	}

	private void agregarItemCombo(Combobox combo, String label, String value) {
		Comboitem item = new Comboitem(label);
		item.setValue(value);
		combo.appendChild(item);
	}

	public void seleccionarEstado(Event event) {
		Comboitem item = ((Combobox) event.getTarget()).getSelectedItem();
		if (item != null) {
			comb_responsable_iva.setSelectedItem(item);
			boolean esResponsableIVA = "S".equals(item.getValue());
			grupo_dian.setVisible(false);
			grupo_sin_dian.setVisible(false);

			if (esResponsableIVA) {
				grupo_dian.setVisible(true);
				text_prefijo_dian.setVisible(esResponsableIVA);
				long_consecutivo_dian.setVisible(esResponsableIVA);
				long_consecutivo_dian.setConstraint("no empty");
			} else {
				grupo_sin_dian.setVisible(true);
				text_prefijo.setVisible(!esResponsableIVA);
				long_consecutivo.setVisible(!esResponsableIVA);
				long_consecutivo.setConstraint("no empty");

			}
			System.out.println("Estado seleccionado: " + ("S".equals(item.getValue()) ? "Sí" : "No"));
		} else {
			System.out.println("No se seleccionó ningún estado.");
		}
	}

	public void seleccionarOpcion(Event event) {
		Comboitem item = ((Combobox) event.getTarget()).getSelectedItem();
		if (item != null) {
			String valorSeleccionado = item.getValue();
			System.out.println("Valor seleccionado: " + valorSeleccionado);
		}
	}

	public void cargarListaParametrosGenerales() {
		btnCrear.setDisabled(permisoCrear());
		this.text_filtrar_parametros_generales.setValue("");
		this.listbox_parametros_generales.getItems().clear();
		parametrosGeneralesDAO.findAll().forEach(this::crearListitem);
		validaResponsableIVA();
	}

	public void validaResponsableIVA() {
		ParametrosGenerales parametro = parametrosGeneralesDAO.obtenerUnicoRegistro();
		if (parametro == null)
			return;

		boolean esResponsableIVA = "S".equalsIgnoreCase(parametro.getResponsableIVA());
		lih_predian.setVisible(esResponsableIVA);
		lih_consdian.setVisible(esResponsableIVA);
		lih_pref.setVisible(!esResponsableIVA);
		lih_cons.setVisible(!esResponsableIVA);
	}

	public void validaResponsableIVAModal() {
		ParametrosGenerales parametro = parametrosGeneralesDAO.obtenerUnicoRegistro();
		if (parametro == null)
			return;

		boolean esResponsableIVA = "S".equalsIgnoreCase(parametro.getResponsableIVA());

		grupo_dian.setVisible(esResponsableIVA);
		text_prefijo_dian.setVisible(esResponsableIVA);
		long_consecutivo_dian.setVisible(esResponsableIVA);

		grupo_sin_dian.setVisible(!esResponsableIVA);
		text_prefijo.setVisible(!esResponsableIVA);
		long_consecutivo.setVisible(!esResponsableIVA);

		if (esResponsableIVA) {
			long_consecutivo_dian.setConstraint("no empty");
			long_consecutivo.setConstraint("");
		} else {
			long_consecutivo.setConstraint("no empty");
			long_consecutivo_dian.setConstraint("");
		}
	}

	public void isMobile() {
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Listhead listhead = listbox_parametros_generales.getListhead();
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

	private void crearListitem(ParametrosGenerales parametrosGenerales) {
		Listitem listitem = new Listitem();
		this.actualizarListitem(parametrosGenerales, listitem);
		this.listbox_parametros_generales.appendChild(listitem);
	}

	private void actualizarListitem(ParametrosGenerales parametrosGenerales, Listitem listitem) {
		listitem.getChildren().clear();
		listitem.appendChild(new Listcell(parametrosGenerales.getResponsableIVA() != null
				&& parametrosGenerales.getResponsableIVA().equalsIgnoreCase("S") ? "Si" : "No"));
		listitem.appendChild(new Listcell(parametrosGenerales.getPrefijoDian().toUpperCase()));
		listitem.appendChild(new Listcell(parametrosGenerales.getPrefijo().toUpperCase()));
		listitem.appendChild(new Listcell(parametrosGenerales.getConsecutivoDian().toString()));
		listitem.appendChild(new Listcell(parametrosGenerales.getConsecutivo().toString()));
		listitem.appendChild(new Listcell(parametrosGenerales.getHabilitaPrecioVenta() != null
				&& parametrosGenerales.getHabilitaPrecioVenta().equalsIgnoreCase("S") ? "Si" : "No"));
		listitem.appendChild(new Listcell(parametrosGenerales.getHabilitaDescuentoTotal() != null
				&& parametrosGenerales.getHabilitaDescuentoTotal().equalsIgnoreCase("S") ? "Si" : "No"));
		listitem.appendChild(new Listcell(parametrosGenerales.getTipoFactura() != null
				&& parametrosGenerales.getTipoFactura().equalsIgnoreCase("mm") ? "80mm" :  parametrosGenerales.getTipoFactura().equalsIgnoreCase("mc") ? "Media carta":"No"));

		/* Acciones */
		Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
		btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
			this.cargarWinParametrosGeneralesForm(parametrosGenerales);
		});
		Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
		btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
			this.eliminarParametrosGenerales(parametrosGenerales);
		});
		Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
		listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

		listitem.setValue(parametrosGenerales);
	}

	public void filtrarListParametrosGenerales(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.listbox_parametros_generales.getItems()) {
			ParametrosGenerales attribute = listitem.getValue();
			listitem.setVisible(filter.isEmpty() || attribute.getResponsableIVA().toUpperCase().contains(filter));
		}
	}

	private void eliminarParametrosGenerales(ParametrosGenerales parametrosGenerales) {
		this.parametrosGeneralesDAO.delete(parametrosGenerales);
		this.cargarListaParametrosGenerales();
	}

	public void cargarWinParametrosGeneralesForm(ParametrosGenerales parametrosGenerales) {
		System.out.println("cargarWinParametrosGeneralesForm");
		validaResponsableIVAModal();
		this.comb_responsable_iva.setSelectedItem(null);
		this.comb_habilita_precio_venta.setSelectedItem(null);
		this.comb_habilita_descuento_total.setSelectedItem(null);
		this.comb_tipo_factura.setSelectedItem(null);
		this.text_prefijo_dian.setValue("");
		this.text_prefijo.setValue("");
		this.long_consecutivo_dian.setValue(0L);
		this.long_consecutivo.setValue(0L);
		if (parametrosGenerales != null) {
			for (Comboitem item : comb_responsable_iva.getItems()) {
				if (item.getValue().equals(parametrosGenerales.getResponsableIVA())) {
					comb_responsable_iva.setSelectedItem(item);
					break;
				}
			}
			for (Comboitem item : comb_habilita_precio_venta.getItems()) {
				if (item.getValue().equals(parametrosGenerales.getHabilitaPrecioVenta())) {
					comb_habilita_precio_venta.setSelectedItem(item);
					break;
				}
			}
			for (Comboitem item : comb_habilita_descuento_total.getItems()) {
				if (item.getValue().equals(parametrosGenerales.getHabilitaDescuentoTotal())) {
					comb_habilita_descuento_total.setSelectedItem(item);
					break;
				}
			}
			for (Comboitem item : comb_tipo_factura.getItems()) {
				if (item.getValue().equals(parametrosGenerales.getTipoFactura())) {
					comb_tipo_factura.setSelectedItem(item);
					break;
				}
			}
			this.text_prefijo_dian.setValue(parametrosGenerales.getPrefijoDian());
			this.text_prefijo.setValue(parametrosGenerales.getPrefijo());
			this.long_consecutivo_dian.setValue(parametrosGenerales.getConsecutivoDian());
			this.long_consecutivo.setValue(parametrosGenerales.getConsecutivo());
		}
		this.win_parametros_generales_form.setAttribute("PARAMETROSGENERALES", parametrosGenerales);
		this.win_parametros_generales_form.doModal();
	}

	public void guardarWinParametrosGeneralesForm() {
		System.out.println("guardarWinParametrosGeneralesForm ");
		if (!validarDatos()) {
			return;
		}
		String tipoFactura = this.comb_tipo_factura.getSelectedItem().getValue();
		String responsableIVA = this.comb_responsable_iva.getSelectedItem().getValue();
		String habilitaPrecioVenta = this.comb_habilita_precio_venta.getSelectedItem().getValue();
		String habilitaDescuentoTotal = this.comb_habilita_descuento_total.getSelectedItem().getValue();
		String prefijoDian = this.text_prefijo_dian.getValue();
		String prefijo = this.text_prefijo.getValue();
		Long consecutivoDian = this.long_consecutivo_dian.getValue();
		Long consecutivo = this.long_consecutivo.getValue();
		ParametrosGenerales parametrosGenerales = (ParametrosGenerales) this.win_parametros_generales_form
				.getAttribute("PARAMETROSGENERALES");

		String mensaje = "Parametro General Guardado Exitosamente.";
		if (parametrosGenerales == null) {
			parametrosGenerales = new ParametrosGenerales();
			parametrosGenerales.setTipoFactura(tipoFactura);
			parametrosGenerales.setResponsableIVA(responsableIVA);
			parametrosGenerales.setPrefijoDian(prefijoDian.toUpperCase());
			parametrosGenerales.setPrefijo(prefijo.toUpperCase());
			parametrosGenerales.setConsecutivoDian(consecutivoDian);
			parametrosGenerales.setConsecutivo(consecutivo);
			parametrosGenerales.setHabilitaPrecioVenta(habilitaPrecioVenta);
			parametrosGenerales.setHabilitaDescuentoTotal(habilitaDescuentoTotal);
			this.parametrosGeneralesDAO.save(parametrosGenerales);
		} else {
			parametrosGenerales.setTipoFactura(tipoFactura);
			parametrosGenerales.setResponsableIVA(responsableIVA);
			parametrosGenerales.setPrefijoDian(prefijoDian.toUpperCase());
			parametrosGenerales.setPrefijo(prefijo.toUpperCase());
			parametrosGenerales.setConsecutivoDian(consecutivoDian);
			parametrosGenerales.setConsecutivo(consecutivo);
			parametrosGenerales.setHabilitaPrecioVenta(habilitaPrecioVenta);
			parametrosGenerales.setHabilitaDescuentoTotal(habilitaDescuentoTotal);
			this.parametrosGeneralesDAO.update(parametrosGenerales);
			mensaje = "Parametro General Editada Exitosamente";
		}

		this.cargarListaParametrosGenerales();
		this.win_parametros_generales_form.setVisible(false);
		DialogUtil.showShortMessage("success", mensaje);
	}

	private boolean validarDatos() {
		if (comb_responsable_iva.getSelectedItem() == null) {
			DialogUtil.showError("Debe ingresar SI ó NO.");
			return false;
		}
		return true;
	}

}
