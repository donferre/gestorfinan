package com.italoweb.gestorfinan.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
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

import com.italoweb.gestorfinan.components.Switch;
import com.italoweb.gestorfinan.model.Cliente;
import com.italoweb.gestorfinan.model.Estado;
import com.italoweb.gestorfinan.model.PerfilUsuario;
import com.italoweb.gestorfinan.model.TipoProveedor;
import com.italoweb.gestorfinan.model.Usuario;
import com.italoweb.gestorfinan.repository.ClienteDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class ClienteController extends Window implements AfterCompose {
    private static final long serialVersionUID = 7513499911306546485L;
    private Textbox text_filtro_clientes;
    private Listbox listbox_clientes;
    private Map<String, Listitem> listitems= new HashMap<String, Listitem>();


    private Window win_cliente_form;
    private Textbox text_nombre;
    private Textbox text_email;
    private Textbox text_telefono;
    private Textbox text_nit;

    private ClienteDAO clienteDAO;

    private Usuario usuarioSession = new Usuario();

    @Override
    public void afterCompose() {
        ComponentsUtil.connectVariablesController(this);
        this.usuarioSession = this.session();
        this.clienteDAO = new ClienteDAO();
        this.cargarListaClientes();
        this.cargarComponentes();
        this.isMobile();
    }
    
    public void cargarComponentes(){
    	this.text_telefono.setMaxlength(14);
    	this.text_telefono.setValue("#");
    	this.text_nombre.setMaxlength(30);
    	this.text_nit.setMaxlength(20);
		this.text_telefono.setZclass("none");
		this.text_telefono.setWidgetListener("onBind", "jq(this.getInputNode()).mask('(999) 999-9999');");
    }

    private Usuario session(){
        Session session = Sessions.getCurrent();
        Object usuario = session.getAttribute("usuario");
        return (Usuario) usuario;
    }

    public void cargarListaClientes() {
        this.text_filtro_clientes.setValue("");
        this.listbox_clientes.getItems().clear();
        for (Cliente cliente : this.clienteDAO.findAll()){
            this.createListitem(cliente);
        }
    }

    public void isMobile(){
        boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
        Listhead listhead = listbox_clientes.getListhead();
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

    private void createListitem(Cliente cliente) {
        Listitem listitem = new Listitem();
        this.updateListitem(cliente, listitem);
        this.listitems.put(cliente.getId().toString(), listitem);
        this.listbox_clientes.appendChild(listitem);
    }

    private void updateListitem(Cliente cliente, Listitem listitem) {
        listitem.getChildren().clear();
        listitem.appendChild(new Listcell(cliente.getId().toString()));
        listitem.appendChild( new Listcell(cliente.getNombre()) );
        listitem.appendChild( new Listcell(cliente.getNit()) );
        listitem.appendChild( new Listcell(cliente.getEmail()) );
        listitem.appendChild( new Listcell(cliente.getTelefono()) );

        /*Estado*/
        Switch checkActive = ComponentsUtil.getSwitch(cliente.getEstado().equals(Estado.ACTIVO));
        checkActive.addEventListener(Switch.ON_TOGGLE, new SerializableEventListener<Event>() {
            private static final long serialVersionUID = 1711205549535046996L;
            public void onEvent(Event event) throws Exception {
                boolean check = (Boolean) event.getData();
                Estado estado = check ? Estado.ACTIVO : Estado.INACTIVO;
                clienteDAO.actualizarEstado(estado, cliente.getId());
            }
        });
        listitem.appendChild(ComponentsUtil.getListcell(null, checkActive));

        /*Acciones*/
        Button btnEdit = ComponentsUtil.getButton("", "btn btn-primary", "z-icon-pencil");
        btnEdit.addEventListener(Events.ON_CLICK, (e) -> {
            this.cargarWinClienteForm(cliente);
        });
        Button btnDelete = ComponentsUtil.getButton("", "btn btn-danger", "z-icon-trash-o");
        btnDelete.addEventListener(Events.ON_CLICK, (e) -> {
            this.eliminarCliente(cliente);
        });
        Div divBtnGroup = ComponentsUtil.getDiv("btn-group btn-group-sm", btnEdit, btnDelete);

        if (this.usuarioSession.getPerfilUsuario().equals(PerfilUsuario.ADMIN)) {
            listitem.appendChild(ComponentsUtil.getListcell("", divBtnGroup));
        }

        listitem.setValue(cliente);
    }

    public void filtrarListClientes(String filter) {
        filter = filter.trim().toUpperCase();
        for (Listitem listitem : this.listbox_clientes.getItems()) {
            Cliente attribute = listitem.getValue();
            boolean matchNombre = attribute.getNombre().toUpperCase().contains(filter);
            boolean matchEmail = attribute.getEmail().toUpperCase().contains(filter);
            listitem.setVisible(filter.isEmpty() || matchNombre || matchEmail);
        }
    }


    private void eliminarCliente(Cliente cliente) {
        DialogUtil.showConfirmDialog("¿Está seguro de que desea eliminar este registro?", "Confirmación")
        .thenAccept(confirmed -> {
            if (confirmed) {
                this.clienteDAO.delete(cliente);
                this.cargarListaClientes();
                DialogUtil.showShortMessage("success", "Cliente Eliminado Exitosamente");
            }
        });
    }

    public void cargarWinClienteForm(Cliente cliente) {
        this.text_nombre.setValue("");
        this.text_nit.setValue("");
        this.text_email.setValue("");
        this.text_telefono.setValue("");
        if(cliente != null) {
            this.text_nombre.setValue(cliente.getNombre());
            this.text_nit.setValue(cliente.getNit());
            this.text_email.setValue(cliente.getEmail());
            this.text_telefono.setValue(cliente.getTelefono());
        }
        this.win_cliente_form.setAttribute("CLIENTE", cliente);
        this.win_cliente_form.doModal();
    }

    public void guardarWinClienteForm() {
    	String nombre = this.text_nombre.getValue().trim();
        String nit = this.text_nit.getValue().trim();
        String email = this.text_email.getValue().trim();
        String telefono = this.text_telefono.getValue();
        TipoProveedor tipoProveedor = new TipoProveedor();
        tipoProveedor.setId(1L);
        String mensaje = "Cliente Guardado Exitosamente.";
        if (StringUtils.isBlank(nit)) {
        	DialogUtil.showError("El nit es Obligatorio.");
        	return;
        }
        if (StringUtils.isBlank(nombre)) {
        	DialogUtil.showError("El nombre es Obligatorio.");
        	return;
		}
        if (!FormatoUtil.esEmailValido(email)) {
			DialogUtil.showError("El correo electrónico no es válido");
			text_email.setFocus(true);
			return;
		}
        if (StringUtils.isBlank(email)) {
        	DialogUtil.showError("El E-mail es Obligatorio.");
        	return;
		}
        
        if (telefono.length() != 14) {
        	DialogUtil.showError("El telefono es Obligatorio.");
        	return;
		}

        Cliente cliente = (Cliente) this.win_cliente_form.getAttribute("CLIENTE");
        if(cliente == null) {
            cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setNit(nit);
            cliente.setEmail(email);
            cliente.setTelefono(telefono);
            cliente.setTipoProveedor(tipoProveedor);
            cliente.setEstado(Estado.ACTIVO);
            cliente.setFechaCreacion(new Date());
            this.clienteDAO.save(cliente);
        }else {
            cliente.setNombre(nombre);
            cliente.setNit(nit);
            cliente.setEmail(email);
            cliente.setTelefono(telefono);
            this.clienteDAO.update(cliente);
            mensaje = "Cliente Editado Exitosamente";
        }
        this.cargarListaClientes();
        this.win_cliente_form.setVisible(false);
        DialogUtil.showShortMessage("success", mensaje);
            
    }
}