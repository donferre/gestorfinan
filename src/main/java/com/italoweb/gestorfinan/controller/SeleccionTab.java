package com.italoweb.gestorfinan.controller;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;

public class SeleccionTab extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    @Wire
    private Include contentInclude;
    @Wire
    private Toolbar toolbar;
    @Wire
    private Label current_subpage;

    private static final Map<String, String> rutas = new HashMap<>();

    static {
    	rutas.put("Descuento de Productos", "producto_descuento");
    	rutas.put("Usuarios", "usuario");
    	rutas.put("Roles", "rol");
    	rutas.put("Medios de Pago", "mediosPagos");
    	rutas.put("Unidad de Compra", "unidad_compra");
    	rutas.put("Categoria", "categoria");
        rutas.put("Impuesto", "impuesto");
        // más entradas si tienes
    }
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Toolbarbutton primero = (Toolbarbutton) toolbar.getFirstChild();

        if (primero != null) {
            String label = primero.getLabel();
            String fileName = rutas.get(label);

            if (fileName != null) {
                contentInclude.setSrc("/views/" + fileName + ".zul");
                current_subpage.setValue(label);
                System.out.println("Pantalla inicial cargada visualmente: /views/" + fileName + ".zul");
            }
        }
    }

    @Listen("onClick = toolbarbutton")
    public void onMenuSelect(Event event) {
        System.out.println("Evento onClick recibido");

        Toolbarbutton btn = (Toolbarbutton) event.getTarget();
        String label = btn.getLabel();
        System.out.println("Label del botón: " + label);

        String fileName = rutas.get(label);
        System.out.println("Archivo zul a cargar: " + fileName);

        if (fileName != null) {
            contentInclude.setSrc("/views/" + fileName + ".zul");
            current_subpage.setValue(label);
            System.out.println("Carga exitosa de: /views/" + fileName + ".zul");
        } else {
            current_subpage.setValue("Pantalla no encontrada");
            contentInclude.setSrc(null);
            System.out.println("No existe ruta para el label: " + label);
        }
    }

}
