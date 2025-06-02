package com.italoweb.gestorfinan.controller;

import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.SesionUsuario;
import com.italoweb.gestorfinan.repository.SesionUsuarioDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;

public class SesionUsuarioController extends Window implements AfterCompose {

    private static final long serialVersionUID = 1138215323743097237L;
	private Textbox text_filtrar_sessiones;
    private Listbox listbox_sessiones;

    private SesionUsuarioDAO sesionUsuarioDAO;

    @Override
    public void afterCompose() {
        ComponentsUtil.connectVariablesController(this);
        this.cargarComponentes();
        this.sesionUsuarioDAO = new SesionUsuarioDAO();
        this.cargarLista();
        this.isMobile();
    }

    public void cargarComponentes(){
    }

    public void cargarLista() {
        this.text_filtrar_sessiones.setValue("");
        this.listbox_sessiones.getItems().clear();
        for (SesionUsuario sesion : this.sesionUsuarioDAO.listarTodas()){
            this.crearListitem(sesion);
        }
    }

    public void isMobile(){
        boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
        Listhead listhead = listbox_sessiones.getListhead();
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

    private void crearListitem(SesionUsuario sesion) {
        Listitem listitem = new Listitem();
        this.actualizarListitem(sesion, listitem);
        this.listbox_sessiones.appendChild(listitem);
    }

    private void actualizarListitem(SesionUsuario sesion, Listitem listitem) {
        listitem.getChildren().clear();
        listitem.appendChild(new Listcell(sesion.getSessionId().toString()));
        listitem.appendChild(new Listcell(sesion.getUsuario().getUsername()));
        listitem.appendChild(new Listcell(sesion.getHoraInicio().toString()));
        listitem.appendChild(new Listcell(sesion.getHoraFin() != null ? sesion.getHoraFin().toString() : "--") );
        listitem.appendChild(ComponentsUtil.getListcell("", estado(sesion.isActivo()))); 
        listitem.setValue(sesion);
    }
    
    private Span estado(boolean estado) {
    	Span span = ComponentsUtil.getSpan(null);
        String sclass = "fs-4";
        if(estado) {
            sclass += " fa fa-check-circle text-success";
        }else {
            sclass += " fa fa-times-circle text-danger";
        }
        span.setClass(sclass);
		return span;
    }

}
