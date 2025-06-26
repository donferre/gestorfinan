package com.italoweb.gestorfinan.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Textbox;

import com.italoweb.gestorfinan.controller.GeneradorFacturaController.ItemFactura;
import com.italoweb.gestorfinan.model.factura.Factura;
import com.italoweb.gestorfinan.model.factura.FacturaImpuesto;
import com.italoweb.gestorfinan.repository.FacturaDAO;
import com.italoweb.gestorfinan.util.FacturaHelper;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class ReimpresionFacturaController extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	@Wire
	private Textbox txtNumeroFactura;
	@Wire
	private Iframe iframeFactura;

	private final FacturaDAO facturaDAO = new FacturaDAO();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	@Listen("onClick = #btnGenerar")
	public void onGenerar() {
		String numero = txtNumeroFactura.getValue().trim().toUpperCase();
		if (numero.isEmpty()) {
			Clients.showNotification("Ingrese un número de factura", "warning", txtNumeroFactura, "after_center", 2000);
			return;
		}
		try {
			// 1) Cargar Factura
			Factura f = facturaDAO.buscarPorNumeroFactura(numero);
			if (f == null) {
				Clients.showNotification("Factura no encontrada", "warning", txtNumeroFactura, "after_center", 2000);
				return;
			}

			// 2) Extraer datos de cabecera
			String nombreEmpresa = f.getNombreEmpresa();
			String nitEmpresa = f.getNitEmpresa();
			String direccionEmpresa = f.getDireccionEmpresa();
			String telefonoEmpresa = f.getTelefonoEmpresa();
			String emailEmpresa = f.getEmail();
			String numeroImpresa = f.getNumeroFacturaImpresa();
			String fechaStr = FormatoUtil.convertirAStringLocal(f.getFecha());
			String vendedor = f.getVendedor() != null ? f.getVendedor().toUpperCase() : "";

			// 3) Construir ítems
			List<ItemFactura> items = f.getDetalles().stream()
					.map(d -> new ItemFactura(d.getProducto().getCodigo(), d.getProducto().getNombre(),
							d.getProducto().getDescripcion(), d.getCantidad(),
							d.getDescuentoUnitario() != null ? d.getDescuentoUnitario().doubleValue() : 0,
							d.getPrecioUnitario().doubleValue()))
					.toList();
			// 3.1) Recuperar ya el desglose de impuestos
			List<FacturaImpuesto> impuestos = f.getImpuestos();
			// 4) Generar el PDF a File
			GeneradorFacturaController gen = new GeneradorFacturaController();
			String nombreArchivo = "factura_" + numeroImpresa + ".pdf";
			File pdfFile;
			String tipoFactura = FacturaHelper.obtenerTipoFactura();

			if ("mm".equalsIgnoreCase(tipoFactura)) {
				pdfFile = gen.generarFacturaTicket80mm(nombreArchivo, nombreEmpresa, nitEmpresa, direccionEmpresa,
						telefonoEmpresa, emailEmpresa, f.getCliente(), fechaStr, numeroImpresa, items, f.getSubtotal(),
						f.getIva(), f.getDescuento(), f.getTotal(), vendedor, impuestos);
			} else {
				pdfFile = gen.generarFactura(nombreArchivo, nombreEmpresa, nitEmpresa, direccionEmpresa,
						telefonoEmpresa, emailEmpresa, f.getCliente(), fechaStr, numeroImpresa, items, f.getSubtotal(),
						f.getIva(), f.getDescuento(), f.getTotal(), vendedor, impuestos);
			}

			// 5) Leer bytes y codificar a Base64
			byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
			String b64 = Base64.getEncoder().encodeToString(pdfBytes);
			String src = "data:application/pdf;base64," + b64;

			// 6) Volcar en el iframe
			iframeFactura.setSrc(src);

		} catch (Exception ex) {
			Clients.showNotification("Error al generar PDF: " + ex.getMessage(), "error", null, "top_center", 3000);
		}
	}

	@Listen("onClick = #btnDescargar")
	public void onDescargar() {
		String numero = txtNumeroFactura.getValue().trim();
		if (numero.isEmpty()) {
			Clients.showNotification("Ingrese un número de factura", "warning", txtNumeroFactura, "after_center", 2000);
			return;
		}
		try {
			// 1) Cargar la factura desde BD
			Factura f = facturaDAO.buscarPorNumeroFactura(numero);
			if (f == null) {
				Clients.showNotification("Factura no encontrada", "warning", txtNumeroFactura, "after_center", 2000);
				return;
			}

			// 2) Preparar datos de cabecera
			String nombreEmpresa = f.getNombreEmpresa();
			String nitEmpresa = f.getNitEmpresa();
			String direccionEmpresa = f.getDireccionEmpresa();
			String telefonoEmpresa = f.getTelefonoEmpresa();
			String emailEmpresa = f.getEmail();
			String numeroImpresa = f.getNumeroFacturaImpresa();
			String fechaStr = FormatoUtil.convertirAStringLocal(f.getFecha());
			String vendedor = f.getVendedor() != null ? f.getVendedor().toUpperCase() : "";

			// 3) Construir lista de ítems
			List<ItemFactura> items = f.getDetalles().stream()
					.map(d -> new ItemFactura(d.getProducto().getCodigo(), d.getProducto().getNombre(),
							d.getProducto().getDescripcion(), d.getCantidad(),
							d.getDescuentoUnitario() != null ? d.getDescuentoUnitario().doubleValue() : 0,
							d.getPrecioUnitario().doubleValue()))
					.toList();
			// 3.1) Recuperar el desglose
			List<FacturaImpuesto> impuestos = f.getImpuestos();
			// 4) Generar el PDF como File
			GeneradorFacturaController gen = new GeneradorFacturaController();
			String nombreArchivo = "factura_" + numeroImpresa + ".pdf";
			File pdfFile;
			String tipoFactura = FacturaHelper.obtenerTipoFactura();

			if ("mm".equalsIgnoreCase(tipoFactura)) {
				pdfFile = gen.generarFacturaTicket80mm(nombreArchivo, nombreEmpresa, nitEmpresa, direccionEmpresa,
						telefonoEmpresa, emailEmpresa, f.getCliente(), fechaStr, numeroImpresa, items, f.getSubtotal(),
						f.getIva(), f.getDescuento(), f.getTotal(), vendedor, impuestos);
			} else {
				pdfFile = gen.generarFactura(nombreArchivo, nombreEmpresa, nitEmpresa, direccionEmpresa,
						telefonoEmpresa, emailEmpresa, f.getCliente(), fechaStr, numeroImpresa, items, f.getSubtotal(),
						f.getIva(), f.getDescuento(), f.getTotal(), vendedor, impuestos);
			}

			// 5) Leer bytes y forzar descarga
			byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
			AMedia media = new AMedia(nombreArchivo, "pdf", "application/pdf", new ByteArrayInputStream(pdfBytes));
			Filedownload.save(media);

		} catch (Exception ex) {
			Clients.showNotification("Error al descargar PDF: " + ex.getMessage(), "error", null, "top_center", 3000);
		}
	}

}
