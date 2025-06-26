package com.italoweb.gestorfinan.controller;

import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import com.italoweb.gestorfinan.dto.CompraEstadisticaDTO;
import com.italoweb.gestorfinan.repository.GraficosCompraDAO;

import tools.dynamia.zk.addons.chartjs.CategoryChartjsData;

public class GraficoCompraController {

	private CategoryChartjsData categoryModel;
	private GraficosCompraDAO graficosDAO = new GraficosCompraDAO();
	
	public GraficoCompraController() {
		load();
	}


	@Command
	@NotifyChange("*")
	public void load() {
		initCategoryModel();
	}

	private void initCategoryModel() {
		categoryModel = new CategoryChartjsData();
		categoryModel.setDatasetLabel("Compras por Día");

		List<CompraEstadisticaDTO> datos = graficosDAO.obtenerComprasPorDia();

		for (CompraEstadisticaDTO dto : datos) {
			categoryModel.add(dto.getPeriodo(), dto.getCantidad().intValue());
		}
	}

	public CategoryChartjsData getCategoryModel() {
		return categoryModel;
	}

	public void setCategoryModel(CategoryChartjsData categoryModel) {
		this.categoryModel = categoryModel;
	}


}
