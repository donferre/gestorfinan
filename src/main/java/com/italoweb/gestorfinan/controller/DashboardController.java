package com.italoweb.gestorfinan.controller;

import java.math.BigDecimal;
import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.italoweb.gestorfinan.dto.CompraEstadisticaDTO;
import com.italoweb.gestorfinan.repository.GraficosCompraDAO;

import tools.dynamia.zk.addons.chartjs.CategoryChartjsData;

public class DashboardController {

	private final GraficosCompraDAO dao = new GraficosCompraDAO();

	private CategoryChartjsData ventasPorMesModel;
	private CategoryChartjsData comprasPorMesModel;
	private CategoryChartjsData totalVentasPorMesModel;
	private CategoryChartjsData totalComprasPorMesModel;
	private CategoryChartjsData topCategoriasModel;
	private CategoryChartjsData productoMasVendidoModel;
	private List<CompraEstadisticaDTO> totalVentasDTO;
	private List<CompraEstadisticaDTO> totalComprasDTO;

	@Init
	@NotifyChange("*")
	public void init() {
		cargarVentasPorMes();
		cargarComprasPorMes();
		cargarTopCategorias();
		cargarProductoMasVendido();
		cargarTotalVentasPorMes();
		cargarTotalComprasPorMes();
	}

	private void cargarVentasPorMes() {
	    ventasPorMesModel = new CategoryChartjsData();
	    ventasPorMesModel.setDatasetLabel("Cantidad Vendida");
	    List<CompraEstadisticaDTO> datos = dao.obtenerVentasPorMes();
	    if (datos == null || datos.isEmpty()) {
	        // Sin datos aún, mostramos un valor neutro
	        ventasPorMesModel.add("Sin datos", 0);
	    } else {
	        for (CompraEstadisticaDTO dto : datos) {
	            ventasPorMesModel.add(dto.getPeriodo(), dto.getCantidad());
	        }
	    }
	}

	private void cargarComprasPorMes() {
	    comprasPorMesModel = new CategoryChartjsData();
	    comprasPorMesModel.setDatasetLabel("Cantidad Comprada");
	    List<CompraEstadisticaDTO> datos = dao.obtenerComprasPorMes();
	    if (datos == null || datos.isEmpty()) {
	        comprasPorMesModel.add("Sin datos", 0);
	    } else {
	        for (CompraEstadisticaDTO dto : datos) {
	            comprasPorMesModel.add(dto.getPeriodo(), dto.getCantidad());
	        }
	    }
	}

	private void cargarTotalVentasPorMes() {
	    totalVentasPorMesModel = new CategoryChartjsData();
	    totalVentasPorMesModel.setDatasetLabel("Total Ventas ($)");
	    // Carga DTO con el total del mes actual, lo guardamos aunque sea cero
	    totalVentasDTO = dao.obtenerTotalVentasMesActual();
	    if (totalVentasDTO == null) {
	        totalVentasDTO = List.of(new CompraEstadisticaDTO("Mes Actual", BigDecimal.ZERO));
	    }
	    // Ahora llenamos la serie histórica
	    List<CompraEstadisticaDTO> datos = dao.obtenerTotalVentasPorMes();
	    if (datos == null || datos.isEmpty()) {
	        totalVentasPorMesModel.add("Sin datos", BigDecimal.ZERO);
	    } else {
	        for (CompraEstadisticaDTO dto : datos) {
	            totalVentasPorMesModel.add(dto.getPeriodo(), dto.getCantidad());
	        }
	    }
	}

	private void cargarTotalComprasPorMes() {
	    totalComprasPorMesModel = new CategoryChartjsData();
	    totalComprasPorMesModel.setDatasetLabel("Total Compras ($)");
	    totalComprasDTO = dao.obtenerTotalComprasMesActual();
	    if (totalComprasDTO == null) {
	        totalComprasDTO = List.of(new CompraEstadisticaDTO("Mes Actual", BigDecimal.ZERO));
	    }
	    List<CompraEstadisticaDTO> datos = dao.obtenerTotalComprasPorMes();
	    if (datos == null || datos.isEmpty()) {
	        totalComprasPorMesModel.add("Sin datos", BigDecimal.ZERO);
	    } else {
	        for (CompraEstadisticaDTO dto : datos) {
	            totalComprasPorMesModel.add(dto.getPeriodo(), dto.getCantidad());
	        }
	    }
	}

	private void cargarTopCategorias() {
	    topCategoriasModel = new CategoryChartjsData();
	    topCategoriasModel.setDatasetLabel("Categorías más vendidas");
	    List<CompraEstadisticaDTO> datos = dao.obtenerTop5CategoriasMasVendidas();
	    if (datos == null || datos.isEmpty()) {
	        datos = dao.obtenerTop5CategoriasMasVendidasActual();
	    }
	    if (datos == null || datos.isEmpty()) {
	        topCategoriasModel.add("Sin datos", 0);
	    } else {
	        for (CompraEstadisticaDTO dto : datos) {
	            topCategoriasModel.add(dto.getPeriodo(), dto.getCantidad().intValue());
	        }
	    }
	}

	private void cargarProductoMasVendido() {
	    productoMasVendidoModel = new CategoryChartjsData();
	    productoMasVendidoModel.setDatasetLabel("Producto más vendido");
	    CompraEstadisticaDTO dto = dao.obtenerProductoMasVendido();
	    if (dto == null) {
	        dto = dao.obtenerProductoMasVendidoActual();
	    }
	    if (dto == null) {
	        productoMasVendidoModel.add("Sin datos", 0);
	    } else {
	        productoMasVendidoModel.add(dto.getPeriodo(), dto.getCantidad().intValue());
	    }
	}

	public CategoryChartjsData getVentasPorMesModel() {
		return ventasPorMesModel;
	}

	public CategoryChartjsData getComprasPorMesModel() {
		return comprasPorMesModel;
	}

	public CategoryChartjsData getTopCategoriasModel() {
		return topCategoriasModel;
	}

	public CategoryChartjsData getProductoMasVendidoModel() {
		return productoMasVendidoModel;
	}

	public CategoryChartjsData getTotalVentasPorMesModel() {
		return totalVentasPorMesModel;
	}

	public CategoryChartjsData getTotalComprasPorMesModel() {
		return totalComprasPorMesModel;
	}

	public List<CompraEstadisticaDTO> getTotalVentasDTO() {
		return totalVentasDTO;
	}

	public List<CompraEstadisticaDTO> getTotalComprasDTO() {
		return totalComprasDTO;
	}

}
