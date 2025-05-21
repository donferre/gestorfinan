package com.italoweb.gestorfinan.controller;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.CategoriaNota;
import com.italoweb.gestorfinan.model.Nota;
import com.italoweb.gestorfinan.repository.CategoriaNotaDAO;
import com.italoweb.gestorfinan.repository.NotaDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;

public class NotaController extends Window implements AfterCompose {

    private static final long serialVersionUID = 7046752081045054122L;
	private Div div_notas;
    private CategoriaNotaDAO categoriaNotaDAO;
    private NotaDAO notaDAO;

    @Override
    public void afterCompose() {
        ComponentsUtil.connectVariablesController(this);
        this.categoriaNotaDAO = new CategoriaNotaDAO();
        this.notaDAO = new NotaDAO();
        this.cargarComponentes();
    }

    public void cargarComponentes(){
        //this.crearNota();
        this.crearCategoria();
    }

    public void crearNota(){
        Div columna = ComponentsUtil.getDiv("card col-3 mr-3 bg-light p-2 rounded shadow-sm mr-5");

        // Título y botón (+)
        Span titulo = ComponentsUtil.getSpan("font-weight-bold", new Label("Trabajando"));

        Button btnAgregar = ComponentsUtil.getButton("", "btn btn-sm btn-primary", "fa fa-plus");
        // Aquí puedes agregar un listener si quieres que haga algo al hacer click

        Div encabezado = ComponentsUtil.getDiv("d-flex justify-content-between align-items-center mb-2", titulo, btnAgregar);

        // Tarjeta 1
        Div tarjeta1Body = ComponentsUtil.getDiv("card-body p-2");
        tarjeta1Body.appendChild(new Label("Add the unit number and description in an action that sends an email when a unit changes status"));
        Div tarjeta1 = ComponentsUtil.getDiv("card mb-2", tarjeta1Body);

        // Tarjeta 2
        Div tarjeta2Body = ComponentsUtil.getDiv("card-body p-2");
        tarjeta2Body.appendChild(new Label("From the mobile phone you have the ability to assign the patient to the call as long as the call does not have one, you only have to fill out first name, last name and DOB"));
        Div tarjeta2 = ComponentsUtil.getDiv("card mb-2", tarjeta2Body);

        // Agregar todo a la columna
        columna.appendChild(encabezado);
        columna.appendChild(tarjeta1);
        columna.appendChild(tarjeta2);

        div_notas.appendChild(columna);

    }

    public List<Div> crearTarjeta(CategoriaNota categoria){
        List<Div> listDiv = new ArrayList<>();
        List<Nota> notas = notaDAO.filtrarxCategoriaId(categoria);
        for (Nota nota : notas){
            Div tarjetaBody = ComponentsUtil.getDiv("card-body p-2 card-border");
            tarjetaBody.appendChild(new Label(nota.getTitulo()));
            Div tarjeta = ComponentsUtil.getDiv("card mb-2", tarjetaBody);
            tarjeta.setAttribute("NOTA", nota);
            listDiv.add(tarjeta);
        }
        return listDiv;
    }

    public void crearCategoria(){
        List<CategoriaNota> categoriaNotas = categoriaNotaDAO.findAll();
        for (CategoriaNota categoria : categoriaNotas){
            Div columna = ComponentsUtil.getDiv("card col-3 mr-3 bg-light p-2 rounded shadow-sm mr-5");
            columna.setDroppable("NOTA");
            columna.addEventListener(Events.ON_DROP, new SerializableEventListener<Event>() {
                private static final long serialVersionUID = -316136864430388856L;
                public void onEvent(Event arg0) throws Exception {
                    DropEvent de = (DropEvent) arg0;
                    Div divTarjeta = (Div) de.getDragged();
                    Nota getNota = (Nota) divTarjeta.getAttribute("NOTA");
                    notaDAO.actualizarCategoriaNotaPorIdNota(getNota.getId(), categoria);
                    columna.appendChild(divTarjeta);
                }
            });

            // Título y botón (+)
            Span titulo = ComponentsUtil.getSpan("font-weight-bold", new Label(categoria.getNombre()));
            Button btnAgregar = ComponentsUtil.getButton("", "btn btn-sm btn-primary", "fa fa-plus");
            // Aquí puedes agregar un listener si quieres que haga algo al hacer click
            Div encabezado = ComponentsUtil.getDiv("d-flex justify-content-between align-items-center mb-2", titulo, btnAgregar);
            columna.appendChild(encabezado);
            for (Div tarjeta : this.crearTarjeta(categoria)){
                tarjeta.setDraggable("NOTA");
                columna.appendChild(tarjeta);
            }

            div_notas.appendChild(columna);
        }
    }
}
