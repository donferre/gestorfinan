package com.italoweb.gestorfinan.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.zkoss.zul.Filedownload;
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
import com.italoweb.gestorfinan.model.ProductoDescuento;
import com.italoweb.gestorfinan.model.Usuario;
import com.italoweb.gestorfinan.model.factura.Factura;
import com.italoweb.gestorfinan.model.factura.FacturaDetalle;
import com.italoweb.gestorfinan.model.factura.FacturaImpuesto;
import com.italoweb.gestorfinan.model.factura.VentaFacturaService;
import com.italoweb.gestorfinan.model.venta.VentaDetalle;
import com.italoweb.gestorfinan.model.venta.Ventas;
import com.italoweb.gestorfinan.repository.ClienteDAO;
import com.italoweb.gestorfinan.repository.FacturaDAO;
import com.italoweb.gestorfinan.repository.InventarioDAO;
import com.italoweb.gestorfinan.repository.MedioPagoDAO;
import com.italoweb.gestorfinan.repository.ParametrosGeneralesDAO;
import com.italoweb.gestorfinan.repository.ProductoDescuentoDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;
import com.italoweb.gestorfinan.util.FacturaHelper;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class VentasController extends GenericForwardComposer<Component> {

	private static final long serialVersionUID = 1L;

	private Grid gridDetalle;
	private Rows rowsDetalle;
	private Rows rowsPorcentaje;
	private Rows rowsDescuento;
	private Combobox comb_cliente;
	private Combobox comb_medio_pago;
	private Textbox text_usuario_vendedor;
	private Datebox date_fecha_venta;
	private Textbox txt_numero_factura;
	private Label lblSubtotal;
	private Label lblIVA;
	private Label lblTotal;
	private Label debx_efectivo_a_devolver;
	private String facturaDian;
	private String factura;
	private String consecutivoFactura;
	private Usuario usuarioSession = new Usuario();
	private MedioPagoDAO medioPagoDAO = new MedioPagoDAO();
	private ClienteDAO clienteDAO = new ClienteDAO();
	private InventarioDAO inventarioDAO = new InventarioDAO();
	private FacturaDAO facturaDAO = new FacturaDAO();
	private ParametrosGeneralesDAO parametrosGeneralesDAO = new ParametrosGeneralesDAO();
	private ProductoDescuentoDAO productoDescuentoDAO = new ProductoDescuentoDAO();
	private Ventas ventas = new Ventas();
	private List<VentaDetalle> listaDetalle = new ArrayList<>();
	private Map<Long, Integer> stockDisponiblePorProducto;
	private ParametrosGenerales parametro;
	private BigDecimal subtotalVenta = BigDecimal.ZERO;
	private BigDecimal totalIVA = BigDecimal.ZERO;
	private BigDecimal totalDcto = BigDecimal.ZERO;
	private BigDecimal totalVenta = BigDecimal.ZERO;

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
				debx_efectivo_a_devolver.setValue("$ " + FormatoUtil.formatDecimal(valorDevolver));
			}
		});
		this.usuarioSession = this.session();
		System.out.println("ejecutando: doAfterCompose");
		cargarComponentes();
	}

	public String generarNuevaFactura() {
		try {
			String ultimaFactura = facturaDAO.obtenerUltimaFactura();
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
		System.out.println("TOTAL: " + totalVenta);
		Map<String, Object> params = new HashMap<>();
		params.put("padre", self);
		params.put("total_venta", totalVenta);
		if (totalVenta != null && totalVenta.compareTo(BigDecimal.ZERO) > 0) {
			Window modal = (Window) Executions.createComponents("/views/detalles/efectivoRecibido.zul", self, params);
			modal.doModal();
		} else {
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

		// Obtener descuento si existe
		Optional<ProductoDescuento> optDescuento = Optional.empty();
		try {
			optDescuento = productoDescuentoDAO.obtenerDescuentoPorIdProducto(producto.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (optDescuento.isPresent()) {
			BigDecimal porcentaje = optDescuento.get().getDescuento();
			detalle.setDescuentoProducto(porcentaje);
			BigDecimal precioFinal = calcularPrecioConDescuento(producto.getPrecioVenta(), porcentaje);
			detalle.setPrecioVentaFinal(precioFinal);
		} else {
			detalle.setDescuentoProducto(BigDecimal.ZERO);
			detalle.setPrecioVentaFinal(producto.getPrecioVenta());
		}

		detalle.setTotalVenta(
				detalle.getPrecioVentaFinal().multiply(BigDecimal.valueOf(detalle.getCantidadVendidad())));

		ventas.getDetalles().add(detalle);
		listaDetalle.add(detalle);
		renderFilaDetalle(detalle);
		actualizarTotales();
	}

	private BigDecimal calcularPrecioConDescuento(BigDecimal precioOriginal, BigDecimal porcentajeDescuento) {
		BigDecimal factor = porcentajeDescuento.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
		BigDecimal descuentoMonto = precioOriginal.multiply(factor);
		return precioOriginal.subtract(descuentoMonto).setScale(2, RoundingMode.HALF_UP);
	}

	/** Agrega fila a fila del Grid renderizando */
	private void renderFilaDetalle(VentaDetalle detalle) {
		System.out.println("Ejecutando: renderFilaDetalle: ");
		System.out.println("PARAMETRO: " + parametro.getHabilitaPrecioVenta());
		Row row = new Row();

		row.appendChild(new Label(detalle.getProducto().getCodigo()));
		row.appendChild(new Label(detalle.getProducto().getNombre()));
		Decimalbox decPrecioVenta = new Decimalbox(detalle.getProducto().getPrecioVenta());
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

		// CANTIDAD
		Intbox intCantidad = new Intbox(detalle.getCantidadVendidad());
		intCantidad.setInplace(false);
		intCantidad.setWidth("60px");

		row.appendChild(intCantidad);

		// IMPUESTO
		row.appendChild(new Label(detalle.getProducto().getImpuesto().getPorcentaje() + " %"));

		// DESCUENTO
		String textoDescuento = detalle.getDescuentoProducto() != null
				&& detalle.getDescuentoProducto().compareTo(BigDecimal.ZERO) > 0
						? FormatoUtil.formatDecimal(detalle.getDescuentoProducto()) + " %"
						: "";
		row.appendChild(new Label(textoDescuento));

		// VALOR PARCIAL
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

				BigDecimal total = detalle.getTotalVenta().multiply(BigDecimal.valueOf(nuevaCantidad));
				detalle.setTotalVenta(total);
				lblTotalFila.setValue("$ " + FormatoUtil.formatDecimal(total));
				actualizarTotales();
			} else {
				Messagebox.show("La cantidad debe ser mayor a cero.");
				intCantidad.setValue(detalle.getCantidadVendidad());
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
		if (detalle.getDescuentoProducto().compareTo(new BigDecimal(0)) > 0) {
			detalle.setTotalVenta(total);
			lblTotalFila.setValue("$ " + FormatoUtil.formatDecimal(total));
		}
	}

	/** Actualiza los totales */
	private void actualizarTotales() {
		subtotalVenta = BigDecimal.ZERO;
		totalIVA = BigDecimal.ZERO;
		totalDcto = BigDecimal.ZERO;

		rowsPorcentaje.getChildren().clear();
		rowsDescuento.getChildren().clear();

		Map<BigDecimal, BigDecimal> ivaPorPorcentaje = new HashMap<>();

		for (VentaDetalle detalle : listaDetalle) {
			BigDecimal precioOriginal = detalle.getProducto().getPrecioVenta();
			BigDecimal precioFinal = detalle.getPrecioVentaFinal() != null ? detalle.getPrecioVentaFinal()
					: precioOriginal;
			BigDecimal cantidad = BigDecimal.valueOf(detalle.getCantidadVendidad());

			subtotalVenta = subtotalVenta.add(precioFinal.multiply(cantidad));

			// Calcular descuento
			if (precioOriginal.compareTo(precioFinal) > 0) {
				BigDecimal montoDescuento = precioOriginal.subtract(precioFinal).multiply(cantidad);
				totalDcto = totalDcto.add(montoDescuento);
			}

			// Calcular IVA
			if (detalle.getProducto().getImpuesto() != null
					&& detalle.getProducto().getImpuesto().getPorcentaje() != null) {
				BigDecimal porcentaje = detalle.getProducto().getImpuesto().getPorcentaje();
				BigDecimal porcentajeIVA = porcentaje.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
				BigDecimal ivaItem = precioFinal.multiply(cantidad).multiply(porcentajeIVA);
				ivaPorPorcentaje.merge(porcentaje, ivaItem, BigDecimal::add);
				totalIVA = totalIVA.add(ivaItem);
			}
		}

		// Mostrar IVA desglosado
		for (Map.Entry<BigDecimal, BigDecimal> entry : ivaPorPorcentaje.entrySet()) {
			Row row = new Row();
			row.appendChild(new Label(entry.getKey() + " %"));
			row.appendChild(new Label("$ " + FormatoUtil.formatDecimal(entry.getValue())));
			rowsPorcentaje.appendChild(row);
		}

		// Mostrar descuentos
		if (totalDcto.compareTo(BigDecimal.ZERO) > 0) {
			Row row = new Row();
			row.appendChild(new Label("Dcto ganado:"));
			row.appendChild(new Label("$ " + FormatoUtil.formatDecimal(totalDcto)));
			rowsDescuento.appendChild(row);
		}

		totalVenta = subtotalVenta.add(totalIVA);

		lblSubtotal.setValue("$ " + FormatoUtil.formatDecimal(subtotalVenta));
		lblIVA.setValue("$ " + FormatoUtil.formatDecimal(totalIVA));
		lblTotal.setValue("$ " + FormatoUtil.formatDecimal(totalVenta));
	}

	public void onClick$btnGuardarVenta() {
		if (!validarCabecera())
			return;

		if (ventas.getDetalles().isEmpty()) {
			DialogUtil.showError("Debe agregar al menos un producto a la compra.");
			return;
		}

		try {
			System.out.println("Iniciando guardado de venta...");

			// --- Cabecera de venta ---
			ventas.setFechaVenta(FormatoUtil.convertirADateLocal(date_fecha_venta.getValue()));
			Comboitem itemCliente = comb_cliente.getSelectedItem();
			Comboitem itemMedioPago = comb_medio_pago.getSelectedItem();
			if (itemCliente != null)
				ventas.setCliente(itemCliente.getValue());
			if (itemMedioPago != null)
				ventas.setMedioPago((MedioPago) itemMedioPago.getValue());

			// --- Validar stock ---
			System.out.println("Validando stock...");
			List<Object[]> resultados = inventarioDAO.obtenerStockActualPorProducto();
			Map<Long, Integer> stockDisponiblePorProducto = resultados.stream()
					.collect(Collectors.toMap(fila -> (Long) fila[0], fila -> ((Number) fila[1]).intValue()));
			for (VentaDetalle detalle : ventas.getDetalles()) {
				Long productoId = detalle.getProducto().getId();
				int disponible = stockDisponiblePorProducto.getOrDefault(productoId, 0);
				if (detalle.getCantidadVendidad() > disponible) {
					DialogUtil.showError("Stock insuficiente para " + detalle.getProducto().getNombre());
					return;
				}
				detalle.setVenta(ventas);
			}

			// --- Construir factura y sus detalles ---
			Factura factura = new Factura();
			factura.setNumero(txt_numero_factura.getValue());
			factura.setFecha(FormatoUtil.convertirADateLocal(date_fecha_venta.getValue()));
			factura.setCliente(ventas.getCliente());
			factura.setNombreEmpresa(FacturaHelper.obtenerNombreEmpresa());
			factura.setNitEmpresa(FacturaHelper.obtenerNitEmpresa());
			factura.setTelefonoEmpresa(FacturaHelper.obtenerTelefonoEmpresa());
			factura.setDireccionEmpresa(FacturaHelper.obtenerDireccionEmpresa());
			factura.setEmail(FacturaHelper.obtenerCorreoEmpresa());
			factura.setNumeroFacturaImpresa(factura.getNumero());
			factura.setVendedor(usuarioSession.getUsername().toUpperCase());

			List<FacturaDetalle> detallesFactura = new ArrayList<>();
			BigDecimal totalDescuento = BigDecimal.ZERO;
			for (VentaDetalle vd : ventas.getDetalles()) {
				FacturaDetalle fd = new FacturaDetalle();
				// Descuento
				if (vd.getDescuentoProducto() != null) {
					fd.setDescuentoUnitario(vd.getDescuentoProducto());
					BigDecimal descUnit = vd.getDescuentoProducto();
					BigDecimal descTotal = descUnit.multiply(BigDecimal.valueOf(vd.getCantidadVendidad()));
					totalDescuento = totalDescuento.add(descTotal);
				}
				fd.setFactura(factura);
				fd.setProducto(vd.getProducto());
				fd.setDescripcion(vd.getProducto().getNombre());
				fd.setCantidad(vd.getCantidadVendidad());
				BigDecimal precioOriginal = vd.getProducto().getPrecioVenta();
				fd.setPrecioUnitario(precioOriginal);
				fd.setTotal(vd.getTotalVenta());
				detallesFactura.add(fd);
			}

			// Asignar totales a factura
			factura.setSubtotal(subtotalVenta);
			factura.setIva(totalIVA);
			factura.setDescuento(totalDcto);
			factura.setTotal(totalVenta);
			factura.setDetalles(detallesFactura);
			ventas.setTotalVenta(totalVenta);

			// === NUEVA SECCIÓN: DESGLOSE DE IVA POR PORCENTAJE ===
			// (se utiliza el mismo cálculo que en actualizarTotales())
			Map<BigDecimal, BigDecimal> ivaPorPorcentaje = new HashMap<>();
			for (VentaDetalle vd : ventas.getDetalles()) {
				BigDecimal precioBase = vd.getPrecioVentaFinal() != null ? vd.getPrecioVentaFinal()
						: vd.getProducto().getPrecioVenta();
				BigDecimal cantidad = BigDecimal.valueOf(vd.getCantidadVendidad());
				BigDecimal pctIVA = vd.getProducto().getImpuesto().getPorcentaje();
				BigDecimal factor = pctIVA.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
				BigDecimal ivaItem = precioBase.multiply(cantidad).multiply(factor);
				ivaPorPorcentaje.merge(pctIVA, ivaItem, BigDecimal::add);
			}
			// Asociar cada porcentaje y su valor a la factura
			for (Map.Entry<BigDecimal, BigDecimal> entry : ivaPorPorcentaje.entrySet()) {
				FacturaImpuesto imp = new FacturaImpuesto(factura, entry.getKey(), entry.getValue());
				factura.addImpuesto(imp);
			}
			// ========================================================

			// 🔁 Guardar venta + factura + detalles + impuestos en cascada
			VentaFacturaService service = new VentaFacturaService();
			service.guardarVentaConFactura(ventas, factura);

			// Generar el PDF ya con el desglose de IVA
			generarFacturaPdf(ventas);

			Messagebox.show("Venta y factura guardadas correctamente.", "Éxito", Messagebox.OK, Messagebox.INFORMATION,
					e -> limpiarPantallaVenta());

		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("Error al guardar: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
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
		// Nueva instancia para evitar problemas con persistencia
		ventas = new Ventas();
		ventas.setDetalles(new ArrayList<>());

		// Limpieza de listas auxiliares
		listaDetalle.clear();

		// Reinicio de totales
		subtotalVenta = BigDecimal.ZERO;
		totalIVA = BigDecimal.ZERO;
		totalDcto = BigDecimal.ZERO;
		totalVenta = BigDecimal.ZERO;

		// Limpieza visual
		rowsDetalle.getChildren().clear();
		rowsPorcentaje.getChildren().clear();
		rowsDescuento.getChildren().clear();

		txt_numero_factura.setValue(generarNuevaFactura());
		comb_cliente.setSelectedItem(null);
		comb_medio_pago.setSelectedItem(null);
		lblSubtotal.setValue("$ 0.00");
		lblIVA.setValue("$ 0.00");
		lblTotal.setValue("$ 0.00");
		debx_efectivo_a_devolver.setValue("$ 0.00");

		// Fecha actual
		date_fecha_venta.setValue(new Date());
	}

	private void generarFacturaPdf(Ventas venta) {
		try {
			GeneradorFacturaController generator = new GeneradorFacturaController();

			// 1) Obtener la factura y validar
			Factura factura = venta.getFactura();
			if (factura == null) {
				DialogUtil.showError("La venta no tiene una factura asociada.");
				return;
			}

			// 2) Convertir detalles a ítems para PDF
			List<GeneradorFacturaController.ItemFactura> items = factura.getDetalles().stream()
					.map(d -> new GeneradorFacturaController.ItemFactura(d.getProducto().getCodigo(),
							d.getProducto().getNombre(), d.getProducto().getDescripcion(), d.getCantidad(),
							d.getDescuentoUnitario() != null ? d.getDescuentoUnitario().doubleValue() : 0,
							d.getPrecioUnitario().doubleValue()))
					.toList();

			// 3) Datos de cabecera desde FacturaHelper
			String nombreEmpresa = FacturaHelper.obtenerNombreEmpresa();
			String nitEmpresa = FacturaHelper.obtenerNitEmpresa();
			String direccionEmpresa = FacturaHelper.obtenerDireccionEmpresa();
			String telefonoEmpresa = FacturaHelper.obtenerTelefonoEmpresa();
			String emailEmpresa = FacturaHelper.obtenerCorreoEmpresa();

			// 4) Otros datos generales
			Cliente cliente = venta.getCliente();
			String usuarioVendedor = usuarioSession.getUsername().toUpperCase();
			String nombreArchivo = "factura_" + factura.getNumero() + ".pdf";
			String fechaStr = FormatoUtil.convertirAStringLocal(factura.getFecha());

			// 5) Valores monetarios seguros
			BigDecimal subTotal = factura.getSubtotal() != null ? factura.getSubtotal() : BigDecimal.ZERO;
			BigDecimal totalIVA = factura.getIva() != null ? factura.getIva() : BigDecimal.ZERO;
			BigDecimal totalDcto = factura.getDescuento() != null ? factura.getDescuento() : BigDecimal.ZERO;
			BigDecimal totalVenta = factura.getTotal() != null ? factura.getTotal() : BigDecimal.ZERO;
			List<FacturaImpuesto> impuestos = factura.getImpuestos();
			// 6) Generar PDF según tipo
			File pdfGenerado;
			if ("mm".equalsIgnoreCase(parametro.getTipoFactura())) {
				System.out.println("Factura de tamaño 80mm");
				pdfGenerado = generator.generarFacturaTicket80mm(nombreArchivo, nombreEmpresa, nitEmpresa,
						direccionEmpresa, telefonoEmpresa, emailEmpresa, cliente, fechaStr, factura.getNumero(), items,
						subTotal, totalIVA, totalDcto, totalVenta, usuarioVendedor,impuestos);
			} else if ("mc".equalsIgnoreCase(parametro.getTipoFactura())) {
				System.out.println("Factura de tamaño media carta");
				pdfGenerado = generator.generarFactura(nombreArchivo, nombreEmpresa, nitEmpresa, direccionEmpresa,
						telefonoEmpresa, emailEmpresa, cliente, fechaStr, factura.getNumero(), items, subTotal,
						totalIVA, totalDcto, totalVenta, usuarioVendedor,impuestos);
			} else {
				System.out.println("IMPLEMENTAR MAS TIPOS DE TAMAÑO DE FACTURA.");
				return;
			}

			// 7) Descargar el PDF en ZK
			Filedownload.save(pdfGenerado, "application/pdf");

		} catch (IOException e) {
			e.printStackTrace();
			DialogUtil.showError("No se pudo generar la factura PDF: " + e.getMessage());
		}
	}

}
