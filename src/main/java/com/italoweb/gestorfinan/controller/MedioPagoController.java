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

import com.italoweb.gestorfinan.model.MedioPago;
import com.italoweb.gestorfinan.repository.MedioPagoDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;

public class MedioPagoController extends Window implements AfterCompose {
    private static final long serialVersionUID = -3579867759499821765L;

    private Textbox text_filtrar_medios_pagos;
    private Listbox listbox_medios_pagos;
    private Window win_medios_pagos_form;
    private Textbox text_nombre;

    private MedioPagoDAO mediosPagosDAO;

    @Override
    public void afterCompose() {
        ComponentsUtil.connectVariablesController(this);
        this.mediosPagosDAO = new MedioPagoDAO();
        this.cargarListaMediosPagos();
        this.isMobile();
    }

    public void cargarListaMediosPagos() {
        this.text_filtrar_medios_pagos.setValue("");
        this.listbox_medios_pagos.getItems().clear();
        for (MedioPago medioPago : this.mediosPagosDAO.findAll()){
            this.crearListitem(medioPago);
        }
    }

    public void isMobile(){
        boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
        Listhead listhead = listbox_medios_pagos.getListhead();
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

    private void crearListitem(MedioPago medioPago) {
        Listitem listitem = new Listitem();
        this.actualizarListitem(medioPago, listitem);
        this.listbox_medios_pagos.appendChild(listitem);
    }

    private void actualizarListitem(MedioPago medioPago, Listitem listitem) {
        listitem.getChildren().clear();
        listitem.appendChild(new Listcell(medioPago.getId().toString()));
        listitem.appendChild( new Listcell(medioPago.getNombre()) );

        /*Acciones*/
        Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
        btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
            this.cargarWinMedioPagoForm(medioPago);
        });
        Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
        btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
            this.eliminarMedioPago(medioPago);
        });
        Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
        listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

        listitem.setValue(medioPago);
    }

    public void filtrarListMediosPagos(String filter) {
        filter = filter.trim().toUpperCase();
        for(Listitem listitem : this.listbox_medios_pagos.getItems()) {
            MedioPago attribute = listitem.getValue();
            listitem.setVisible(filter.isEmpty() || attribute.getNombre().toUpperCase().contains(filter));
        }
    }

    private void eliminarMedioPago(MedioPago medioPago) {
        this.mediosPagosDAO.delete(medioPago);
        this.cargarListaMediosPagos();
    }

    public void cargarWinMedioPagoForm(MedioPago medioPago) {
        this.text_nombre.setValue("");
        if(medioPago != null) {
            this.text_nombre.setValue(medioPago.getNombre());
        }
        this.win_medios_pagos_form.setAttribute("MEDIO_PAGO", medioPago);
        this.win_medios_pagos_form.doModal();
    }

    public void guardarWinMedioPagoForm() {
        String cod = this.text_nombre.getValue().trim();
        if(cod != null) {
            if(cod != null) {
                MedioPago medioPago = (MedioPago) this.win_medios_pagos_form.getAttribute("MEDIO_PAGO");
                if(medioPago == null) {
                    medioPago = new MedioPago();
                    medioPago.setNombre(cod);
                    this.mediosPagosDAO.save(medioPago);
                }else {
                    medioPago.setNombre(cod);
                    this.mediosPagosDAO.update(medioPago);
                }
                this.cargarListaMediosPagos();
                this.win_medios_pagos_form.setVisible(false);
            }
        }
    }
}
