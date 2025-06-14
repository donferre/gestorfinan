package com.italoweb.gestorfinan.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.Cliente;
import com.italoweb.gestorfinan.model.Estado;
import com.italoweb.gestorfinan.model.MedioPago;
import com.italoweb.gestorfinan.model.ParametrosGenerales;
import com.italoweb.gestorfinan.model.Producto;
import com.italoweb.gestorfinan.model.Usuario;
import com.italoweb.gestorfinan.model.venta.VentaDetalle;
import com.italoweb.gestorfinan.model.venta.Ventas;
import com.italoweb.gestorfinan.repository.ClienteDAO;
import com.italoweb.gestorfinan.repository.InventarioDAO;
import com.italoweb.gestorfinan.repository.MedioPagoDAO;
import com.italoweb.gestorfinan.repository.ParametrosGeneralesDAO;
import com.italoweb.gestorfinan.repository.VentasDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class VentasController extends GenericForwardComposer<Component> {

	private static final long serialVersionUID = 1L;

	private Grid gridDetalle;
	private Rows rowsDetalle;
	private Rows rowsPorcentaje;
	private Combobox comb_cliente;
	private Combobox comb_medio_pago;
	private Textbox text_usuario_vendedor;
	private Datebox date_fecha_venta;
	private Textbox txt_numero_factura;
	private Label lblSubtotal;
	private Label lblIVA;
	private Label lblTotal;
	private String facturaDian;
	private String factura;
	private BigDecimal total;
	private Decimalbox debx_efectivo_a_devolver;
	private String consecutivoFactura;
	private Usuario usuarioSession = new Usuario();
	private MedioPagoDAO medioPagoDAO = new MedioPagoDAO();
	private ClienteDAO clienteDAO = new ClienteDAO();
	private InventarioDAO inventarioDAO = new InventarioDAO();
	private VentasDAO ventasDAO = new VentasDAO();
	private ParametrosGeneralesDAO parametrosGeneralesDAO = new ParametrosGeneralesDAO();
	private Ventas ventas = new Ventas();
	private List<VentaDetalle> listaDetalle = new ArrayList<>();
	private Map<Long, Integer> stockDisponiblePorProducto;
	private ParametrosGenerales parametro;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		ventas = new Ventas();
		ventas.setDetalles(new ArrayList<>());
		actualizarTotales();
		parametro = parametrosGeneralesDAO.obtenerUnicoRegistro();
		if (parametro != null) {
			if (parametro.getResponsableIVA().equalsIgnoreCase("S")) {
				factura = parametro.getPrefijoDian() != null ? parametro.getPrefijoDian() : "";
				consecutivoFactura = parametro.getConsecutivoDian().toString();
			} else if (parametro.getResponsableIVA().equalsIgnoreCase("N")) {
				factura = parametro.getPrefijo() != null ? parametro.getPrefijo() : "";
				consecutivoFactura = parametro.getConsecutivo().toString();
			}
		}
		String nuevaFactura = generarNuevaFactura();
		txt_numero_factura.setValue(nuevaFactura);
		txt_numero_factura.setReadonly(true);
		self.addEventListener("onProductoSeleccionado", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				Producto productoSeleccionado = (Producto) event.getData();
				System.out.println("Producto recibido: " + productoSeleccionado.getNombre());
				agregarProductoADetalle(productoSeleccionado);
			}
		});
		self.addEventListener("onClienteCreado", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				Cliente clienteCreado = (Cliente) event.getData();
				System.out.println("Cliente recibido: " + clienteCreado.getNombre());
				cargarClienteCreado(clienteCreado);
			}
		});
		self.addEventListener("onCancelar", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				comb_medio_pago.setSelectedItem(null);
			}
		});
		self.addEventListener("onAceptar", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				BigDecimal valorDevolver = (BigDecimal) event.getData();
				debx_efectivo_a_devolver.setFormat("#,##0.00");
				debx_efectivo_a_devolver.setReadonly(true);
				debx_efectivo_a_devolver.setValue(valorDevolver);
			}
		});
		this.usuarioSession = this.session();
		System.out.println("ejecutando: doAfterCompose");
		cargarComponentes();
	}

	public String generarNuevaFactura() {
		try {
			String ultimaFactura = ventasDAO.obtenerUltimaFactura();
			System.out.println("ULTIMA FACTURA DETECTADA: " + ultimaFactura);

			String prefijoConfig = factura != null ? factura : "";
			int consecutivoConfig = ComponentsUtil.safeParse(consecutivoFactura, 0);
			String prefijoBD = "";
			int numeroBD = 0;

			// Extraer prefijo y número de la BD si existe
			if (ultimaFactura != null && !ultimaFactura.isEmpty()) {
				Pattern pattern = Pattern.compile("([a-zA-Z]*)(\\d+)");
				Matcher matcher = pattern.matcher(ultimaFactura);
				if (matcher.find()) {
					prefijoBD = matcher.group(1) != null ? matcher.group(1) : "";
					numeroBD = ComponentsUtil.safeParse(matcher.group(2), 0);
				}
			}

			String prefijoFinal = "";
			if (!prefijoBD.isEmpty() && prefijoBD.equals(prefijoConfig)) {
				prefijoFinal = prefijoBD;
			} else if (!prefijoConfig.isEmpty()) {
				prefijoFinal = prefijoConfig;
			}

			int numeroFinal;
			if (consecutivoConfig > numeroBD) {
				numeroFinal = consecutivoConfig;
			} else {
				numeroFinal = numeroBD + 1;
			}

			facturaDian = prefijoFinal + String.format("%04d", numeroFinal);
			System.out.println("Nueva factura generada: " + facturaDian);
			return facturaDian;

		} catch (Exception e) {
			e.printStackTrace();
			return facturaDian = factura + consecutivoFactura;
		}
	}

	public void cargarComponentes() {
		System.out.println("ejecutando: cargarComponentes");
		text_usuario_vendedor.setValue(usuarioSession.getUsername().toUpperCase());
		text_usuario_vendedor.setDisabled(true);
		date_fecha_venta.setDisabled(true);
		date_fecha_venta.setValue(new Date());
		cargarMedioPago();
		cargarCliente();
		isMobile();
		stockDisponiblePorProducto = obtenerStockActualPorProducto();
	}

	private Map<Long, Integer> obtenerStockActualPorProducto() {
		Map<Long, Integer> stockMap = new HashMap<>();
		try {
			List<Object[]> resultados = inventarioDAO.obtenerStockActualPorProducto();
			for (Object[] fila : resultados) {
				Long productoId = (Long) fila[0];
				Number stock = (Number) fila[1];
				stockMap.put(productoId, stock.intValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stockMap;
	}

	public void cargarMedioPago() {
		System.out.println("ejecutando: cargarMedioPago");
		this.comb_medio_pago.setAutocomplete(false);
		List<MedioPago> listaMedioPago = this.medioPagoDAO.findAll();
		for (MedioPago medioPago : listaMedioPago) {
			this.comb_medio_pago.appendChild(ComponentsUtil.getComboitem(medioPago.getNombre(), null, medioPago));
		}
		comb_medio_pago.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				Comboitem item = comb_medio_pago.getSelectedItem();
				if (item != null) {
					MedioPago medioPago = item.getValue();
					System.out.println("Medio de pago seleccionado: " + medioPago.getNombre());

					if ("efectivo".equalsIgnoreCase(medioPago.getNombre())) {
						abrirVentanaEfectivo();
					}
				}
			}
		});

	}

	public void cargarCliente() {
		System.out.println("ejecutando: cargarCliente");
		this.comb_cliente.setAutocomplete(false);
		List<Cliente> listaProveedores = this.clienteDAO.filtroEstadoActivo(Estado.ACTIVO);
		for (Cliente cliente : listaProveedores) {
			this.comb_cliente.appendChild(ComponentsUtil
					.getComboitem("[" + cliente.getNit() + "]" + " " + cliente.getNombre(), null, cliente));
		}
	}

	public void cargarClienteCreado(Cliente cliente) {
		System.out.println("ejecutando: cargarClienteCreado");
		boolean encontrado = false;
		for (Comboitem item : comb_cliente.getItems()) {
			Cliente c = (Cliente) item.getValue();
			if (c != null && c.getId().equals(cliente.getId())) {
				comb_cliente.setSelectedItem(item);
				encontrado = true;
				break;
			}
		}
		if (!encontrado) {
			Comboitem nuevoItem = new Comboitem("[" + cliente.getNit() + "] " + cliente.getNombre());
			nuevoItem.setValue(cliente);
			comb_cliente.appendChild(nuevoItem);
			comb_cliente.setSelectedItem(nuevoItem);
		}
	}

	public void isMobile() {
		System.out.println("ejecutando: isMobile");
		boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
		Columns columns = gridDetalle.getColumns();
		if (columns != null) {
			List<Component> columnChildren = columns.getChildren();
			for (Component comp : columnChildren) {
				if (comp instanceof Column) {
					Column column = (Column) comp;
					if (esMovil) {
						column.setHflex("min");
					} else {
						column.setHflex("true");
					}
				}
			}
		}
	}

	private Usuario session() {
		Session session = Sessions.getCurrent();
		Object usuario = session.getAttribute("usuario");
		return (Usuario) usuario;
	}

	public void onChanging$comb_medio_pago(InputEvent event) {
		String filtro = event.getValue();
		filtrarCombo(comb_medio_pago, filtro);
	}

	public void onChanging$comb_cliente(InputEvent event) {
		String filtro = event.getValue();
		filtrarCombo(comb_cliente, filtro);
	}

	public void filtrarCombo(Combobox combo, String filter) {
		filter = filter.trim().toUpperCase();
		for (Comboitem comboItem : combo.getItems()) {
			String label = comboItem.getLabel();
			comboItem.setVisible(filter.isEmpty() || label.toUpperCase().contains(filter));
		}
	}

	/** Acción del botón "Agregar Cliente" */
	public void onClick$btnAgregarCliente() {
		System.out.println("ejecutando: onClick$btnAgregarCliente");
		Map<String, Object> params = new HashMap<>();
		params.put("padre", self);

		Window modal = (Window) Executions.createComponents("/views/detalles/agregarClienteDet.zul", self, params);
		modal.doModal();
	}

	/** Acción del botón "Asignar efectivo" */
	public void abrirVentanaEfectivo() {
		System.out.println("ejecutando: abrirVentanaEfectivo");
		System.out.println("TOTAL: "+total);
		Map<String, Object> params = new HashMap<>();
		params.put("padre", self);
		params.put("total_venta", total);
		if (total != null && total.compareTo(BigDecimal.ZERO) > 0) {
			Window modal = (Window) Executions.createComponents("/views/detalles/efectivoRecibido.zul", self, params);
			modal.doModal();
		}else {
			comb_medio_pago.setSelectedItem(null);
			DialogUtil.showShortMessage("fail", "Debe seleccionar productos para calcular venta en efectivo.");
		}

	}

	/** Acción del botón "Agregar Producto" */
	public void onClick$btnAgregarProducto() {
		System.out.println("ejecutando: onClick$btnAgregarProducto");
		Map<String, Object> params = new HashMap<>();
		params.put("padre", self);
		params.put("tipo", "V");

		Window modal = (Window) Executions.createComponents("/views/detalles/agregarProductoDet.zul", self, params);
		modal.doModal();
	}

	/** Agrega un producto a la lista de detalles y renderiza la fila */
	private void agregarProductoADetalle(Producto producto) {
		System.out.println("ejecutando: agregarProductoADetalle");
		// Verificar si el producto ya está en la lista
		boolean productoYaExiste = listaDetalle.stream()
				.anyMatch(detalle -> detalle.getProducto().getId().equals(producto.getId()));
		if (productoYaExiste) {
			Messagebox.show("El producto '" + producto.getNombre() + "' ya fue agregado.", "Producto duplicado",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		VentaDetalle detalle = new VentaDetalle();
		detalle.setProducto(producto);
		detalle.setCantidadVendidad(1);
		detalle.setTotalVenta(
				detalle.getProducto().getPrecioVenta().multiply(BigDecimal.valueOf(detalle.getCantidadVendidad())));
		ventas.getDetalles().add(detalle);
		listaDetalle.add(detalle);
		renderFilaDetalle(detalle);
		actualizarTotales();
	}

	/** Agrega fila a fila del Grid renderizando */
	private void renderFilaDetalle(VentaDetalle detalle) {
		Row row = new Row();

		row.appendChild(new Label(detalle.getProducto().getCodigo()));
		row.appendChild(new Label(detalle.getProducto().getNombre()));
		Decimalbox decPrecioVenta = new Decimalbox(detalle.getProducto().getPrecioVenta());
		System.out.println("PARAMETRO: " + parametro.getHabilitaPrecioVenta());
		if (parametro.getHabilitaPrecioVenta().equalsIgnoreCase("N")) {
			decPrecioVenta.setInplace(true);
			decPrecioVenta.setReadonly(true);
		} else {
			decPrecioVenta.setInplace(false);
			decPrecioVenta.setReadonly(false);
		}
		decPrecioVenta.setWidth("120px");
		decPrecioVenta.setConstraint("no empty");
		decPrecioVenta.setFormat("#,##0.00");
		row.appendChild(decPrecioVenta);

		Intbox intCantidad = new Intbox(detalle.getCantidadVendidad());
		intCantidad.setInplace(false);
		intCantidad.setWidth("60px");
		intCantidad.addEventListener("onChange", e -> {
			Integer nuevaCantidad = intCantidad.getValue();

			if (nuevaCantidad != null && nuevaCantidad > 0) {
				Integer stockDisponible = stockDisponiblePorProducto.getOrDefault(detalle.getProducto().getId(), 0);

				if (nuevaCantidad > stockDisponible) {
					Messagebox.show("La cantidad ingresada excede el stock disponible (" + stockDisponible + ").");
					intCantidad.setValue(detalle.getCantidadVendidad());
					return;
				}

				detalle.setCantidadVendidad(nuevaCantidad);

				BigDecimal total = detalle.getProducto().getPrecioVenta().multiply(BigDecimal.valueOf(nuevaCantidad));
				detalle.setTotalVenta(total);

				((Label) row.getChildren().get(5)).setValue(FormatoUtil.formatearMoneda(total));
				actualizarTotales();
			} else {
				Messagebox.show("La cantidad debe ser mayor a cero.");
				intCantidad.setValue(detalle.getCantidadVendidad());
			}
		});

		row.appendChild(intCantidad);

		row.appendChild(new Label(detalle.getProducto().getImpuesto().getPorcentaje() + " %"));

		Label lblTotalFila = new Label("$ " + FormatoUtil.formatDecimal(detalle.getTotalVenta()));
		row.appendChild(lblTotalFila);

		decPrecioVenta.addEventListener("onChange", e -> {
			BigDecimal nuevoPrecioVenta = decPrecioVenta.getValue();
			if (nuevoPrecioVenta != null && nuevoPrecioVenta.compareTo(BigDecimal.ZERO) >= 0) {
				detalle.getProducto().setPrecioVenta(nuevoPrecioVenta);
				recalcularTotal(detalle, intCantidad.getValue(), decPrecioVenta.getValue(), lblTotalFila);
				actualizarTotales();
			} else {
				DialogUtil.showError("El precio de venta debe ser igual o mayor a cero.");
				decPrecioVenta.setValue(detalle.getProducto().getPrecioVenta());
			}
		});

		Button btnEliminar = new Button("Eliminar");
		btnEliminar.addEventListener("onClick", e -> {
			ventas.getDetalles().remove(detalle);
			listaDetalle.remove(detalle);
			rowsDetalle.removeChild(row);
			actualizarTotales();
		});
		row.appendChild(btnEliminar);

		rowsDetalle.appendChild(row);
	}

	private void recalcularTotal(VentaDetalle detalle, Integer cantidad, BigDecimal precio, Label lblTotalFila) {
		if (cantidad == null) {
			lblTotalFila.setValue("$ 0.00");
			return;
		}
		BigDecimal total = precio.multiply(BigDecimal.valueOf(cantidad));
		detalle.setCantidadVendidad(cantidad);
		detalle.setTotalVenta(total);
		lblTotalFila.setValue("$ " + FormatoUtil.formatDecimal(total));
	}

	/** Calcula el precio del producto con descuento si aplica */
//	private BigDecimal calcularPrecio(Producto producto) {
//		LocalDateTime ahora = LocalDateTime.now();
//
//		if (producto.getFechaIniciaDescuento() != null && producto.getFechaFinalDescuento() != null) {
//			if (!ahora.isBefore(producto.getFechaIniciaDescuento()) && !ahora.isAfter(producto.getFechaFinalDescuento())
//					&& producto.getDescuento() != null) {
//				return producto.getDescuento();
//			}
//		}
//
//		// Precio base temporal (puedes adaptar si tienes campo `precioBase`)
//		return BigDecimal.TEN;
//	}

	/** Actualiza los totales */
	private void actualizarTotales() {
		BigDecimal subTotal = ventas.getDetalles().stream().map(detalle -> {
			BigDecimal precio = detalle.getProducto().getPrecioVenta() != null ? detalle.getProducto().getPrecioVenta()
					: BigDecimal.ZERO;
			BigDecimal cantidad = BigDecimal.valueOf(detalle.getCantidadVendidad());
			return precio.multiply(cantidad);
		}).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalIVA = BigDecimal.ZERO;
		rowsPorcentaje.getChildren().clear();

		Map<BigDecimal, BigDecimal> ivaPorPorcentaje = new HashMap<>();

		for (VentaDetalle detalle : listaDetalle) {
			Producto producto = detalle.getProducto();
			if (producto != null && producto.getImpuesto() != null && producto.getImpuesto().getPorcentaje() != null
					&& producto.getImpuesto().getPorcentaje().compareTo(BigDecimal.ZERO) > 0) {

				BigDecimal porcentaje = producto.getImpuesto().getPorcentaje();
				BigDecimal porcentajeIVA = porcentaje.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
				BigDecimal precio = producto.getPrecioVenta() != null ? producto.getPrecioVenta() : BigDecimal.ZERO;
				BigDecimal cantidad = BigDecimal.valueOf(detalle.getCantidadVendidad());

				BigDecimal ivaItem = precio.multiply(cantidad).multiply(porcentajeIVA);
				ivaPorPorcentaje.merge(porcentaje, ivaItem, BigDecimal::add);
			}
		}

		for (Map.Entry<BigDecimal, BigDecimal> entry : ivaPorPorcentaje.entrySet()) {
			BigDecimal porcentaje = entry.getKey();
			BigDecimal ivaTotalPorPorcentaje = entry.getValue();
			totalIVA = totalIVA.add(ivaTotalPorPorcentaje);

			Row row = new Row();
			Label porcentajeLabel = new Label(porcentaje.toPlainString() + " %");
			Label impuestoTotalLabel = new Label("$ " + FormatoUtil.formatDecimal(ivaTotalPorPorcentaje));

			row.appendChild(porcentajeLabel);
			row.appendChild(impuestoTotalLabel);
			rowsPorcentaje.appendChild(row);
		}

		total = subTotal.add(totalIVA);
		lblSubtotal.setValue("$ " + FormatoUtil.formatDecimal(subTotal));
		lblIVA.setValue("$ " + FormatoUtil.formatDecimal(totalIVA));
		lblTotal.setValue("$ " + FormatoUtil.formatDecimal(total));
		System.out.println("TOTAL: "+total);
	}

	public void onClick$btnGuardarVenta() {
		if (!validarCabecera()) {
			return;
		}
		if (ventas.getDetalles().isEmpty()) {
			DialogUtil.showError("Debe agregar al menos un producto a la compra.");
			return;
		}

		ventas.setFactura(txt_numero_factura.getValue());
		ventas.setFechaVenta(FormatoUtil.convertirADateLocal(date_fecha_venta.getValue()));
		Comboitem itemCliente = comb_cliente.getSelectedItem();
		Comboitem itemMedioPago = comb_medio_pago.getSelectedItem();

		if (itemCliente != null) {
			ventas.setCliente(itemCliente.getValue());
		}
		if (itemMedioPago != null) {
			ventas.setMedioPago((MedioPago) itemMedioPago.getValue());
		}
		try {
			// Obtener stock
			List<Object[]> resultados = inventarioDAO.obtenerStockActualPorProducto();
			Map<Long, Integer> stockDisponiblePorProducto = new HashMap<>();
			for (Object[] fila : resultados) {
				Long productoId = (Long) fila[0];
				Number stock = (Number) fila[1];
				stockDisponiblePorProducto.put(productoId, stock.intValue());
			}
			// Validar stock por cada producto en la venta
			for (VentaDetalle detalle : ventas.getDetalles()) {
				Long productoId = detalle.getProducto().getId();
				Integer stockDisponible = stockDisponiblePorProducto.getOrDefault(productoId, 0);

				if (detalle.getCantidadVendidad() > stockDisponible) {
					DialogUtil.showError("La cantidad en stock para el producto " + detalle.getProducto().getNombre()
							+ " es insuficiente.");
					return;
				}

				detalle.setVenta(ventas);
			}

			VentasDAO ventaDAO = new VentasDAO();
			ventaDAO.save(ventas);

			InventarioDAO inventarioDAO = new InventarioDAO();
			inventarioDAO.registrarMovimientoInventarioPorVenta(ventas);

			Messagebox.show("Venta guardada exitosamente.", "Éxito", Messagebox.OK, Messagebox.INFORMATION, e -> {
				limpiarPantallaVenta();
			});
		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("Error al guardar la venta: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}

	/** Valida los campos de la cabecera antes de guardar */
	private boolean validarCabecera() {
		if (comb_cliente.getSelectedItem() == null) {
			DialogUtil.showError("Debe seleccionar un Cliente.");
			return false;
		}
		if (txt_numero_factura.getValue() == null) {
			DialogUtil.showError("Debe ingresar el número de factura.");
			return false;
		}
		if (date_fecha_venta.getValue() == null) {
			DialogUtil.showError("Debe ingresar la fecha de pago.");
			return false;
		}
		if (comb_medio_pago.getSelectedItem() == null) {
			DialogUtil.showError("Debe seleccionar un medio de pago.");
			return false;
		}
		return true;
	}

	private void limpiarPantallaVenta() {
		ventas.setCliente(null);
		ventas.setFactura(null);
		ventas.setFechaVenta(null);
		ventas.setMedioPago(null);

		if (ventas.getDetalles() != null) {
			ventas.getDetalles().clear();
		}
		rowsDetalle.getChildren().clear();
		rowsPorcentaje.getChildren().clear();
		txt_numero_factura.setValue(null);
		comb_cliente.setSelectedItem(null);
		comb_medio_pago.setSelectedItem(null);
		txt_numero_factura.setValue(generarNuevaFactura());
		lblTotal.setValue("$ 0.00");
		lblSubtotal.setValue("$ 0.00");
		lblIVA.setValue("$ 0.00");
		debx_efectivo_a_devolver.setValue("$ 0.00");
	}

}
