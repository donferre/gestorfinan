package com.italoweb.gestorfinan.controller;

import java.util.List;

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

import com.italoweb.gestorfinan.model.Concepto;
import com.italoweb.gestorfinan.model.TipoMovimiento;
import com.italoweb.gestorfinan.repository.ConceptoDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;

public class ConceptoController extends Window implements AfterCompose {
    private static final long serialVersionUID = 1017544894264788562L;

    private Textbox text_filtrar_conceptos;
    private Listbox listbox_conceptos;
    private Window win_conceptos_form;
    private Textbox text_nombre;
    private Combobox comb_tipo_movimiento;

    private ConceptoDAO conceptoDAO;

    @Override
    public void afterCompose() {
        ComponentsUtil.connectVariablesController(this);
        this.conceptoDAO = new ConceptoDAO();
        this.cargarComponentes();
        this.cargarListaConceptos();
        this.isMobile();
    }

    public void cargarComponentes(){
        this.comb_tipo_movimiento.setAutocomplete(false);
        for(TipoMovimiento tipoMovimiento : TipoMovimiento.values()) {
            this.comb_tipo_movimiento.appendChild(ComponentsUtil.getComboitem(tipoMovimiento.getLabel(), null, tipoMovimiento));
        }
    }

    public void cargarListaConceptos() {
        this.text_filtrar_conceptos.setValue("");
        this.listbox_conceptos.getItems().clear();
        for (Concepto concepto : this.conceptoDAO.findAll()){
            this.crearListitem(concepto);
        }
    }

    public void isMobile(){
        boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
        Listhead listhead = listbox_conceptos.getListhead();
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

    private void crearListitem(Concepto concepto) {
        Listitem listitem = new Listitem();
        this.actualizarListitem(concepto, listitem);
        this.listbox_conceptos.appendChild(listitem);
    }

    private void actualizarListitem(Concepto concepto, Listitem listitem) {
        listitem.getChildren().clear();
        listitem.appendChild(new Listcell(concepto.getId().toString()));
        listitem.appendChild(new Listcell(concepto.getNombre()));
        listitem.appendChild(new Listcell(concepto.getTipoMovimiento().getLabel()));

        Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
        btnEdit.addEventListener(Events.ON_CLICK, (e) -> this.cargarWinConceptoForm(concepto));

        Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
        btnDelete.addEventListener(Events.ON_CLICK, (e) -> this.eliminarConcepto(concepto));

        Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
        listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

        listitem.setValue(concepto);
    }

    public void filtrarListConceptos(String filter) {
        filter = filter.trim().toUpperCase();
        for (Listitem listitem : this.listbox_conceptos.getItems()) {
            Concepto concepto = listitem.getValue();
            listitem.setVisible(filter.isEmpty() || concepto.getNombre().toUpperCase().contains(filter));
        }
    }

    private void eliminarConcepto(Concepto concepto) {
        this.conceptoDAO.delete(concepto);
        this.cargarListaConceptos();
    }

    public void cargarWinConceptoForm(Concepto concepto) {
        this.text_nombre.setValue("");
        this.comb_tipo_movimiento.setSelectedIndex(0);
        if (concepto != null) {
            this.text_nombre.setValue(concepto.getNombre());
            ComponentsUtil.setComboboxValue(this.comb_tipo_movimiento, concepto.getTipoMovimiento());
        }
        this.win_conceptos_form.setAttribute("CONCEPTO", concepto);
        this.win_conceptos_form.doModal();
    }

    public void filtrarComboTipoMov(String filter) {
        filter = filter.trim().toUpperCase();
        for(Comboitem comboItem : this.comb_tipo_movimiento.getItems()) {
            String tipo = comboItem.getLabel();
            comboItem.setVisible(filter.isEmpty() || tipo.toUpperCase().contains(filter));
        }
    }

    public void guardarWinConceptoForm() {
        String nombre = this.text_nombre.getValue().trim();
        TipoMovimiento tipoMovimiento = this.comb_tipo_movimiento.getSelectedItem().getValue();

        if (!nombre.isEmpty() && tipoMovimiento != null) {
            Concepto concepto = (Concepto) this.win_conceptos_form.getAttribute("CONCEPTO");
            if (concepto == null) {
                concepto = new Concepto();
                concepto.setNombre(nombre);
                concepto.setTipoMovimiento(tipoMovimiento);
                this.conceptoDAO.save(concepto);
            } else {
                concepto.setNombre(nombre);
                concepto.setTipoMovimiento(tipoMovimiento);
                this.conceptoDAO.update(concepto);
            }
            this.cargarListaConceptos();
            this.win_conceptos_form.setVisible(false);
        }
    }
}