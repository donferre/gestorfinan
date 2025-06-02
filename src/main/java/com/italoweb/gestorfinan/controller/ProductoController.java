package com.italoweb.gestorfinan.controller;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.model.Categoria;
import com.italoweb.gestorfinan.model.Impuesto;
import com.italoweb.gestorfinan.model.Producto;
import com.italoweb.gestorfinan.model.UnidadCompra;
import com.italoweb.gestorfinan.model.poblacion.Ciudad;
import com.italoweb.gestorfinan.repository.CategoriaDAO;
import com.italoweb.gestorfinan.repository.ImpuestoDAO;
import com.italoweb.gestorfinan.repository.ProductoDAO;
import com.italoweb.gestorfinan.repository.UnidadCompraDAO;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;

public class ProductoController extends Window implements AfterCompose {
	private static final long serialVersionUID = -6732848727890683953L;

	private Textbox text_filtrar_productos;
	private Listbox listbox_productos;
	private Window win_productos_form;
	private Textbox text_codigo;
	private Textbox text_nombre;
	private Textbox text_descripcion;
	private Combobox comb_unidad_compra;
	private Combobox comb_categoria;
	private Textbox text_marca;
	private Intbox int_stock_minimo;
	private Combobox comb_impuesto;
	private Decimalbox debx_porcentaje_impuesto;

	private ProductoDAO productoDAO = new ProductoDAO();
	private UnidadCompraDAO unidadCompraDAO = new UnidadCompraDAO();
	private ImpuestoDAO impuestoDAO = new ImpuestoDAO();
	private CategoriaDAO categoriaDAO = new CategoriaDAO();

	@Override
	public void afterCompose() {
		ComponentsUtil.connectVariablesController(this);
		this.cargarComponentes();
		this.cargarListaProductos();
		this.isMobile();
	}

	public void cargarComponentes() {
		cargarUnidadCompra();
		cargarCategoria();
		cargarImpuesto();
		this.debx_porcentaje_impuesto.setDisabled(true);
	}

	public void cargarUnidadCompra() {
		this.comb_unidad_compra.setAutocomplete(false);
		List<UnidadCompra> listaUnidadCompra = this.unidadCompraDAO.findAll();
		for (UnidadCompra unidadCompra : listaUnidadCompra) {
			this.comb_unidad_compra
					.appendChild(ComponentsUtil.getComboitem(unidadCompra.getNombre(), null, unidadCompra));
		}
	}

	public void cargarImpuesto() {
		this.comb_impuesto.setAutocomplete(false);
		List<Impuesto> listaImpuesto = this.impuestoDAO.findAll();
		for (Impuesto impuesto : listaImpuesto) {
			this.comb_impuesto.appendChild(ComponentsUtil.getComboitem(impuesto.getNombre(), null, impuesto));
		}
		this.comb_impuesto.addEventListener(Events.ON_SELECT, event -> {
			Comboitem selectedItem = this.comb_impuesto.getSelectedItem();
			if (selectedItem != null) {
				Impuesto impuestoSeleccionado = selectedItem.getValue();
				if (impuestoSeleccionado != null) {
					this.debx_porcentaje_impuesto.setValue(impuestoSeleccionado.getPorcentaje());
				} else {
					this.debx_porcentaje_impuesto.setValue(new BigDecimal(0)); // o BigDecimal.ZERO
				}
			}
		});
	}

	public void cargarCategoria() {
		this.comb_categoria.setAutocomplete(false);
		List<Categoria> listaCategoria = this.categoriaDAO.findAll();
		for (Categoria categoria : listaCategoria) {
			this.comb_categoria.appendChild(ComponentsUtil.getComboitem(categoria.getNombre(), null, categoria));
		}
	}

	public void cargarListaProductos() {
		this.text_filtrar_productos.setValue("");
		this.listbox_productos.getItems().clear();
		productoDAO.findAll().forEach(this::crearListitem);
	}

	public void isMobile() {
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
		listitem.appendChild(new Listcell(producto.getCodigo()));
		listitem.appendChild(new Listcell(producto.getNombre()));
		listitem.appendChild(new Listcell(producto.getDescripcion()));
		listitem.appendChild(new Listcell(producto.getUnidadCompra().getNombre().toString()));
		listitem.appendChild(new Listcell(producto.getImpuesto().getNombre()));
		listitem.appendChild(new Listcell(producto.getImpuesto().getPorcentaje().toString()));
		listitem.appendChild(new Listcell(producto.getCategoria().getNombre().toString()));
		listitem.appendChild(new Listcell(producto.getStockMinimo().toString()));
		listitem.appendChild(new Listcell(producto.getMarca()));

		/* Acciones */
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
		for (Listitem listitem : this.listbox_productos.getItems()) {
			Producto p = listitem.getValue();
			boolean visible = filter.isEmpty()
					|| (p.getCodigo() != null && p.getCodigo().toUpperCase().contains(filter))
					|| (p.getNombre() != null && p.getNombre().toUpperCase().contains(filter))
					|| (p.getDescripcion() != null && p.getDescripcion().toUpperCase().contains(filter))
					|| (p.getUnidadCompra() != null && p.getUnidadCompra().getNombre() != null
							&& p.getUnidadCompra().getNombre().toUpperCase().contains(filter))
					|| (p.getCategoria() != null && p.getCategoria().getNombre() != null
							&& p.getCategoria().getNombre().toUpperCase().contains(filter))
					|| (p.getImpuesto() != null && p.getImpuesto().getNombre() != null
							&& p.getImpuesto().getNombre().toUpperCase().contains(filter))
					|| (p.getStockMinimo() != null && p.getStockMinimo().toString().toUpperCase().contains(filter))
					|| (p.getMarca() != null && p.getMarca().toUpperCase().contains(filter));

			listitem.setVisible(visible);
		}
	}

	private void eliminarProducto(Producto producto) {
		DialogUtil.showConfirmDialog("¿Está seguro de que desea eliminar este registro?", "Confirmación")
				.thenAccept(confirmed -> {
					if (confirmed) {
						this.productoDAO.delete(producto);
						this.cargarListaProductos();
						DialogUtil.showShortMessage("success", "Ingreso Eliminado Exitosamente");
					}
				});
	}

	public void validaCodigo() {
		System.out.print("Ingreso a validar Código...");
		if (productoDAO.existeProductoConCodigo(this.text_codigo.getValue().trim())) {
			DialogUtil.showError("El código ingresado ya existe. Usar otro.");
			this.text_codigo.setValue(null);
			this.text_codigo.setFocus(true);
		}
	}

	public void filtrarComboCategoria(String filter) {
		filtrarCombo(this.comb_categoria, filter);
	}

	public void filtrarComboImpuesto(String filter) {
		filtrarCombo(this.comb_impuesto, filter);
	}

	public void filtrarUnidadCompra(String filter) {
		filtrarCombo(this.comb_unidad_compra, filter);
	}

	public void filtrarCombo(Combobox combo, String filter) {
		filter = filter.trim().toUpperCase();
		for (Comboitem comboItem : combo.getItems()) {
			String label = comboItem.getLabel();
			comboItem.setVisible(filter.isEmpty() || label.toUpperCase().contains(filter));
		}
	}

	public void cargarWinProductoForm(Producto producto) {
		this.text_codigo.setValue("");
		this.text_nombre.setValue("");
		this.text_descripcion.setValue("");
		this.comb_unidad_compra.setSelectedItem(null);
		this.comb_impuesto.setSelectedItem(null);
		this.debx_porcentaje_impuesto.setValue(new BigDecimal(0));
		this.comb_categoria.setSelectedItem(null);
		this.int_stock_minimo.setValue(0);
		this.text_marca.setValue("");

		if (producto != null) {
			System.out.println("INGRESO AQUI");
			this.text_codigo.setValue(producto.getCodigo());
			this.text_nombre.setValue(producto.getNombre());
			this.text_descripcion.setValue(producto.getDescripcion());
			ComponentsUtil.setComboboxValue(this.comb_impuesto, producto.getImpuesto());
			this.debx_porcentaje_impuesto.setValue(producto.getImpuesto().getPorcentaje());
			System.out.println("PORCENTAJE : " + producto.getImpuesto().getPorcentaje());
			ComponentsUtil.setComboboxValue(this.comb_unidad_compra, producto.getUnidadCompra());
			ComponentsUtil.setComboboxValue(this.comb_categoria, producto.getCategoria());
			this.int_stock_minimo.setValue(producto.getStockMinimo());
			this.text_marca.setValue(producto.getMarca());
		}

		this.win_productos_form.setAttribute("PRODUCTO", producto);
		this.win_productos_form.doModal();
	}

	public void guardarWinProductoForm() {
		String codigo = this.text_codigo.getValue().trim();
		String nombre = this.text_nombre.getValue().trim();
		String descripcion = this.text_descripcion.getValue().trim();
		UnidadCompra unidadCompra = this.comb_unidad_compra.getSelectedItem() != null
				? this.comb_unidad_compra.getSelectedItem().getValue()
				: null;

		Impuesto conceptoImpuesto = this.comb_impuesto.getSelectedItem() != null
				? this.comb_impuesto.getSelectedItem().getValue()
				: null;

		Categoria categoria = this.comb_categoria.getSelectedItem() != null
				? this.comb_categoria.getSelectedItem().getValue()
				: null;

		Integer stockMinimo = this.int_stock_minimo.getValue();
		String marca = this.text_marca.getValue().trim();

		// Validaciones
		if (productoDAO.existeProductoConCodigo(codigo)) {
			DialogUtil.showError("El código ingresado ya existe. Usar otro.");
		}
		if (StringUtils.isBlank(codigo)) {
			DialogUtil.showError("El Código es obligatorio.");
			return;
		}
		if (StringUtils.isBlank(nombre)) {
			DialogUtil.showError("El nombre es obligatorio.");
			return;
		}
		if (unidadCompra == null) {
			DialogUtil.showError("La Unidad de Compra es obligatoria.");
			return;
		}
		if (conceptoImpuesto == null) {
			DialogUtil.showError("El Concepto de Impuesto es obligatorio.");
			return;
		}
		if (categoria == null) {
			DialogUtil.showError("La Categoría es obligatoria.");
			return;
		}

		Producto producto = (Producto) this.win_productos_form.getAttribute("PRODUCTO");

		if (producto == null) {
			producto = new Producto();
		}

		producto.setCodigo(codigo);
		producto.setNombre(nombre);
		producto.setDescripcion(descripcion);
		producto.setUnidadCompra(unidadCompra);
		producto.setCategoria(categoria);
		producto.setMarca(marca);
		producto.setStockMinimo(stockMinimo);
		producto.setImpuesto(conceptoImpuesto);

		if (producto.getId() == null) {
			this.productoDAO.save(producto);
		} else {
			this.productoDAO.update(producto);
		}

		this.cargarListaProductos();
		this.win_productos_form.setVisible(false);
	}
}
