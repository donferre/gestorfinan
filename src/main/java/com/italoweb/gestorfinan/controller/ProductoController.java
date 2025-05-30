package com.italoweb.gestorfinan.controller;

import java.math.BigDecimal;
import java.util.Arrays;
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

import com.italoweb.gestorfinan.components.Chosenbox;
import com.italoweb.gestorfinan.model.Producto;
import com.italoweb.gestorfinan.repository.ProductoDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;

public class ProductoController extends Window implements AfterCompose {
    private static final long serialVersionUID = -6732848727890683953L;

    private Textbox text_filtrar_productos;
    private Listbox listbox_productos;
    private Window win_productos_form;
    private Textbox text_nombre;
    private Decimalbox debx_precio;
    private Div div_emails;
    private Chosenbox chosenbox;

    private ProductoDAO productoDAO;

    @Override
    public void afterCompose() {
        ComponentsUtil.connectVariablesController(this);
        this.cargarComponentes();
        this.productoDAO = new ProductoDAO();
        this.cargarListaProductos();
        this.isMobile();
    }

    public void cargarComponentes(){
        List<String> emails = Arrays.asList("italoandres21@gmail.com", "carlosmario33@gmail.com", "marioluna@hotmail.com", "lucia3122@gmail.com",
                "djmario@hotmail.com", "dj.com", "dl.com", "andres21@gmail.com");
        this.chosenbox = new Chosenbox();
        this.chosenbox.setModel(emails);
        this.chosenbox.setMultiple(true);
        this.chosenbox.setWrapMode("scroll");
        this.div_emails.appendChild(this.chosenbox);
    }

    public void cargarListaProductos() {
        this.text_filtrar_productos.setValue("");
        this.listbox_productos.getItems().clear();
        for (Producto producto : this.productoDAO.findAll()){
            this.crearListitem(producto);
        }
    }

    public void isMobile(){
        boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
        Listhead listhead = listbox_productos.getListhead();
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

    private void crearListitem(Producto producto) {
        Listitem listitem = new Listitem();
        this.actualizarListitem(producto, listitem);
        this.listbox_productos.appendChild(listitem);
    }

    private void actualizarListitem(Producto producto, Listitem listitem) {
        listitem.getChildren().clear();
        listitem.appendChild(new Listcell(producto.getId().toString()));
        listitem.appendChild( new Listcell(producto.getNombre()) );
        listitem.appendChild( new Listcell(producto.getPrecio().toString()) );

        /*Acciones*/
        Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
        btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
            this.cargarWinProductoForm(producto);
        });
        Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
        btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
            this.eliminarProducto(producto);
        });
        Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);
        listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));

        listitem.setValue(producto);
    }

    public void filtrarListProductos(String filter) {
        filter = filter.trim().toUpperCase();
        for(Listitem listitem : this.listbox_productos.getItems()) {
            Producto attribute = listitem.getValue();
            listitem.setVisible(filter.isEmpty() || attribute.getNombre().toUpperCase().contains(filter));
        }
    }

    private void eliminarProducto(Producto producto) {
        this.productoDAO.delete(producto);
        this.cargarListaProductos();
    }

    public void cargarWinProductoForm(Producto producto) {
        this.text_nombre.setValue("");
        this.debx_precio.setValue(new BigDecimal(0));
        if(producto != null) {
            this.text_nombre.setValue(producto.getNombre());
            this.debx_precio.setValue(producto.getPrecio());
        }
        this.win_productos_form.setAttribute("PRODUCTO", producto);
        this.win_productos_form.doModal();
    }

    public void guardarWinProductoForm() {
        String nombre = this.text_nombre.getValue().trim();
        BigDecimal precio = this.debx_precio.getValue();
        if(nombre != null) {
            if(precio != null) {
                Producto producto = (Producto) this.win_productos_form.getAttribute("PRODUCTO");
                if(producto == null) {
                    producto = new Producto();
                    producto.setNombre(nombre);
                    producto.setPrecio(precio);
                    this.productoDAO.save(producto);
                }else {
                    producto.setNombre(nombre);
                    producto.setPrecio(precio);
                    this.productoDAO.update(producto);
                }
                this.cargarListaProductos();
                this.win_productos_form.setVisible(false);
            }
        }
    }
}
