package com.italoweb.gestorfinan.controller.detalles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.Producto;
import com.italoweb.gestorfinan.repository.InventarioDAO;
import com.italoweb.gestorfinan.repository.ProductoDAO;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class SeleccionarProductoController extends GenericForwardComposer<Component> {

	private static final long serialVersionUID = 1L;
	private Window win_seleccionar_producto;
	private Listbox lstProductos;
	private Listheader lbl_desc;
	private Listheader lbl_precio;
	private Listheader lbl_inventario;
	private Component padre;
	private String tipo;
	private Textbox text_filtrar_productos;
	private InventarioDAO inventarioDAO = new InventarioDAO();
	private ProductoDAO productoServicio = new ProductoDAO();
	private Map<Long, Integer> stockDisponiblePorProducto;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		padre = (Component) Executions.getCurrent().getArg().get("padre");
		tipo = (String) Executions.getCurrent().getArg().get("tipo");
		if (tipo.equalsIgnoreCase("C")) {
			lbl_desc.setVisible(true);
			lbl_precio.setVisible(false);
		} else if(tipo.equalsIgnoreCase("V")) {
			lbl_desc.setVisible(false);
			lbl_precio.setVisible(true);
			lbl_inventario.setVisible(true);
		}else if(tipo.equalsIgnoreCase("D")) {
			lbl_desc.setVisible(false);
			lbl_precio.setVisible(false);
			lbl_inventario.setVisible(false);
		}
		win_seleccionar_producto.setWidth("800px");
		win_seleccionar_producto.setHeight("800px");
		this.text_filtrar_productos.setValue("");
		stockDisponiblePorProducto = obtenerStockActualPorProducto();
		cargarProductos();
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

	private void cargarProductos() {
		List<Producto> productos = productoServicio.findAll();
		for (Producto p : productos) {
			Listitem item = new Listitem();
			item.setValue(p);
			item.appendChild(new Listcell(p.getCodigo()));
			item.appendChild(new Listcell(p.getNombre()));
			item.appendChild(new Listcell(p.getDescripcion()));
			item.appendChild(new Listcell(FormatoUtil.formatearMoneda(p.getPrecioVenta())));
			Integer stock = stockDisponiblePorProducto.getOrDefault(p.getId(), 0);
			item.appendChild(new Listcell(stock.toString()));

			item.addEventListener(Events.ON_DOUBLE_CLICK, event -> {
				if (tipo.equalsIgnoreCase("V")) {
					Integer stockEvento = stockDisponiblePorProducto.getOrDefault(p.getId(), 0);
					if (stockEvento <= 0) {
						Messagebox.show("El producto seleccionado no tiene stock disponible.");
						return;
					}
				}
				Events.sendEvent(new Event("onProductoSeleccionado", padre, p));
				win_seleccionar_producto.detach();
			});
			lstProductos.appendChild(item);
		}
	}

	public void onClick$btnSeleccionar() {
		System.out.println("ejecutando: onClick$btnSeleccionar");
		Listitem selectedItem = lstProductos.getSelectedItem();

		if (selectedItem != null) {
			Producto productoSeleccionado = selectedItem.getValue();
			if (tipo.equalsIgnoreCase("V")) {
				Integer stock = stockDisponiblePorProducto.getOrDefault(productoSeleccionado.getId(), 0);
				if (stock <= 0) {
					Messagebox.show("El producto seleccionado no tiene stock disponible.");
					return;
				}
			}
			Events.sendEvent(new Event("onProductoSeleccionado", padre, productoSeleccionado));
			win_seleccionar_producto.detach();
		} else {
			Messagebox.show("Por favor, seleccione un producto.");
		}
	}

	public void onChanging$text_filtrar_productos(InputEvent event) {
		filtrarListProductos(event.getValue());
	}

	public void filtrarListProductos(String filter) {
		filter = filter.trim().toUpperCase();
		for (Listitem listitem : this.lstProductos.getItems()) {
			Producto p = listitem.getValue();
			boolean visible = filter.isEmpty()
					|| (p.getCodigo() != null && p.getCodigo().toUpperCase().contains(filter))
					|| (p.getNombre() != null && p.getNombre().toUpperCase().contains(filter))
					|| (p.getDescripcion() != null && p.getDescripcion().toUpperCase().contains(filter))
					|| (p.getPrecioVenta() != null && p.getPrecioVenta().toString().contains(filter));

			listitem.setVisible(visible);
		}
	}

	public void onClick$btnCancelar() {
		win_seleccionar_producto.detach();
	}
}
