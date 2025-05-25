package com.italoweb.gestorfinan.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.Concepto;
import com.italoweb.gestorfinan.model.MedioPago;
import com.italoweb.gestorfinan.model.Movimiento;
import com.italoweb.gestorfinan.model.TipoMovimiento;
import com.italoweb.gestorfinan.repository.ConceptoDAO;
import com.italoweb.gestorfinan.repository.MedioPagoDAO;
import com.italoweb.gestorfinan.repository.MovimientoDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class IngresosController extends Window implements AfterCompose {
	private static final long serialVersionUID = -6732848727890683953L;

	private Textbox text_filtrar_ingresos;
	private Listbox listbox_ingresos;
	private Window win_ingresos_form;
	private Combobox comb_concepto;
	private Combobox comb_medio_pago;
	private Decimalbox debx_valor;
	private Textbox text_descripcion;
	private Datebox dbx_fecha;
	private MovimientoDAO movimientoDAO;
	private ConceptoDAO conceptoDAO;
	private MedioPagoDAO medioPagoDAO;

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.movimientoDAO = new MovimientoDAO();
		this.conceptoDAO = new ConceptoDAO();
		this.medioPagoDAO = new MedioPagoDAO();
		this.cargarComponentes();
		this.cargarListaIngresos();
		this.isMobile();
	}

	public void cargarConceptos() {
		this.comb_concepto.setAutocomplete(false);
		List<Concepto> listaConceptosIngreso = this.conceptoDAO.findByTipoMovimiento(TipoMovimiento.INGRESOS);
		for (Concepto concepto : listaConceptosIngreso) {
			this.comb_concepto.appendChild(ComponentsUtil.getComboitem(concepto.getNombre(), null, concepto));
		}
	}

	public void cargarMediosPago() {
		this.comb_concepto.setAutocomplete(false);
		List<MedioPago> listaMedioPago = this.medioPagoDAO.findAll();
		for (MedioPago medioPago : listaMedioPago) {
			this.comb_medio_pago.appendChild(ComponentsUtil.getComboitem(medioPago.getNombre(), null, medioPago));
		}
	}

	public void cargarComponentes() {
		cargarConceptos();
		cargarMediosPago();
	}

	public void cargarListaIngresos() {
		this.text_filtrar_ingresos.setValue("");
		this.listbox_ingresos.getItems().clear();
		for (Movimiento ingresos : this.movimientoDAO.findByTipoMovimiento(TipoMovimiento.INGRESOS)) {
			this.crearListitem(ingresos);
		}
	}

	public void isMobile() {
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Listhead listhead = listbox_ingresos.getListhead();
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

	private void crearListitem(Movimiento ingresos) {
		Listitem listitem = new Listitem();
		this.actualizarListitem(ingresos, listitem);
		this.listbox_ingresos.appendChild(listitem);
	}

	private void actualizarListitem(Movimiento ingresos, Listitem listitem) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		listitem.getChildren().clear();
		listitem.appendChild(new Listcell(ingresos.getId().toString()));
		listitem.appendChild(new Listcell(ingresos.getConcepto().getNombre().toString()));
		listitem.appendChild(new Listcell(ingresos.getMedioPago().getNombre().toString()));
		listitem.appendChild(new Listcell(FormatoUtil.formatearMoneda(ingresos.getValor())));
		listitem.appendChild(new Listcell(FormatoUtil.formatearFecha(ingresos.getFecha())));
		listitem.appendChild(new Listcell(ingresos.getDescripcion()));

		/* Acciones */
		Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
		btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
			this.cargarWinIngresosForm(ingresos);
		});
		Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
		btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
			this.eliminarProducto(ingresos);
		});
		Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
		listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

		listitem.setValue(ingresos);
	}

	public void filtrarListIngresos(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.listbox_ingresos.getItems()) {
			Movimiento attribute = listitem.getValue();
			listitem.setVisible(filter.isEmpty() || attribute.getDescripcion().toUpperCase().contains(filter));
		}
	}

	private void eliminarProducto(Movimiento ingresos) {
		this.movimientoDAO.delete(ingresos);
		this.cargarListaIngresos();
	}

	public void cargarWinIngresosForm(Movimiento ingresos) {
		this.debx_valor.setValue(new BigDecimal(0));
		this.comb_concepto.setSelectedIndex(0);
		this.comb_medio_pago.setSelectedIndex(0);
		this.dbx_fecha.setValue(new Date());
		this.text_descripcion.setValue("");
		if (ingresos != null) {
			ComponentsUtil.setComboboxValue(this.comb_concepto, ingresos.getConcepto().getId());
			ComponentsUtil.setComboboxValue(this.comb_medio_pago, ingresos.getMedioPago().getId());
			this.debx_valor.setValue(ingresos.getValor());
			this.dbx_fecha.setValue(ingresos.getFecha());
			this.text_descripcion.setValue(ingresos.getDescripcion());
		}
		this.win_ingresos_form.setAttribute("INGRESOS", ingresos);
		this.win_ingresos_form.doModal();
	}

	public void filtrarComboConcepto(String filter) {
		filtrarCombo(this.comb_concepto, filter);
	}

	public void filtrarComboMedioPago(String filter) {
		filtrarCombo(this.comb_medio_pago, filter);
	}

	public void filtrarCombo(Combobox combo, String filter) {
		filter = filter.trim().toUpperCase();
		for (Comboitem comboItem : combo.getItems()) {
			String label = comboItem.getLabel();
			comboItem.setVisible(filter.isEmpty() || label.toUpperCase().contains(filter));
		}
	}

	public void guardarWinProductoForm() {
		Concepto concepto = this.comb_concepto.getSelectedItem().getValue();
		MedioPago medioPago = this.comb_medio_pago.getSelectedItem().getValue();
		BigDecimal valor = this.debx_valor.getValue();
		Date fecha = this.dbx_fecha.getValue();
		String descripcion = this.text_descripcion.getValue().trim();
		if (concepto != null && valor != null) {
			Movimiento ingresos = (Movimiento) this.win_ingresos_form.getAttribute("INGRESOS");
			if (ingresos == null) {
				ingresos = new Movimiento();
				ingresos.setConcepto(concepto);
				ingresos.setMedioPago(medioPago);
				ingresos.setValor(valor);
				ingresos.setFecha(fecha);
				ingresos.setDescripcion(descripcion);
				this.movimientoDAO.save(ingresos);
			} else {
				ingresos.setConcepto(concepto);
				ingresos.setMedioPago(medioPago);
				ingresos.setValor(valor);
				ingresos.setFecha(fecha);
				ingresos.setDescripcion(descripcion);
				this.movimientoDAO.update(ingresos);
			}
			this.cargarListaIngresos();
			this.win_ingresos_form.setVisible(false);
		}
	}
}
