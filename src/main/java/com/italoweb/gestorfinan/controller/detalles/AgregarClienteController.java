package com.italoweb.gestorfinan.controller.detalles;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.Cliente;
import com.italoweb.gestorfinan.model.Estado;
import com.italoweb.gestorfinan.model.TipoProveedor;
import com.italoweb.gestorfinan.repository.ClienteDAO;
import com.italoweb.gestorfinan.util.DialogUtil;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class AgregarClienteController extends GenericForwardComposer<Component> {

	private static final long serialVersionUID = 1L;
	private Window win_cliente_form;
	private Textbox text_nombre_cliente;
	private Textbox text_email_cliente;
	private Textbox text_telefono_cliente;
	private Textbox text_nit_cliente;
	private Component padre;
	private ClienteDAO clienteDAO = new ClienteDAO();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		padre = (Component) Executions.getCurrent().getArg().get("padre");
		this.text_nombre_cliente.setValue("");
		this.text_nit_cliente.setValue("");
		this.text_email_cliente.setValue("");
		this.text_telefono_cliente.setValue("");
		this.text_telefono_cliente.setMaxlength(14);
    	this.text_nombre_cliente.setMaxlength(30);
    	this.text_nit_cliente.setMaxlength(20);
		this.text_telefono_cliente.setZclass("none");
		this.text_telefono_cliente.setWidgetListener("onBind", "jq(this.getInputNode()).mask('(999) 999-9999');");
   
	}

	public void onClick$guardarWinClienteForm() {
		System.out.println("onClick$guardarWinClienteForm");
		String nombre = this.text_nombre_cliente.getValue().trim();
		String nit = this.text_nit_cliente.getValue().trim();
		String email = this.text_email_cliente.getValue().trim();
		String telefono = this.text_telefono_cliente.getValue();
		TipoProveedor tipoProveedor = new TipoProveedor();
		tipoProveedor.setId(1L);
		String mensaje = "Cliente Guardado Exitosamente.";
		if (StringUtils.isBlank(nit)) {
			DialogUtil.showError("El Nit es Obligatorio.");
			return;
		}
		if (StringUtils.isBlank(nombre)) {
			DialogUtil.showError("El nombre es Obligatorio.");
			return;
		}
		if (!FormatoUtil.esEmailValido(email)) {
			DialogUtil.showError("El correo electrónico no es válido");
			text_email_cliente.setFocus(true);
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
		if (cliente == null) {
			cliente = new Cliente();
			cliente.setNombre(nombre);
			cliente.setNit(nit);
			cliente.setEmail(email);
			cliente.setTelefono(telefono);
			cliente.setTipoProveedor(tipoProveedor);
			cliente.setEstado(Estado.ACTIVO);
			cliente.setFechaCreacion(new Date());
			this.clienteDAO.save(cliente);
		}

		DialogUtil.showShortMessage("success", mensaje);
		Events.sendEvent(new Event("onClienteCreado", padre, cliente));
		win_cliente_form.detach();
	}

	public void onClick$btnCancelar() {
		win_cliente_form.detach();
	}
}
