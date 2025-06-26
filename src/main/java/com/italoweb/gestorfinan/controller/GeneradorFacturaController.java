package com.italoweb.gestorfinan.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.italoweb.gestorfinan.model.Cliente;
import com.italoweb.gestorfinan.model.factura.FacturaImpuesto;
import com.italoweb.gestorfinan.util.FacturaHelper;
import com.italoweb.gestorfinan.util.FormatoUtil;

public class GeneradorFacturaController {

	public static class ItemFactura {
		public String codigo;
		public String nombre;
		public String descripcion;
		public int cantidad;
		public double descuento;
		public double precioUnitario;

		public ItemFactura(String codigo, String nombre, String descripcion, int cantidad, double descuento,
				double precioUnitario) {
			this.codigo = codigo;
			this.nombre = nombre;
			this.descripcion = descripcion;
			this.cantidad = cantidad;
			this.descuento = descuento;
			this.precioUnitario = precioUnitario;
		}

		public double getTotal() {
			return cantidad * precioUnitario;
		}
	}

	@SuppressWarnings("deprecation")
	public File generarFactura(String nombreArchivo, String nombreEmpresa, String nitEmpresa, String direccionEmpresa,
			String telefonoEmpresa, String emailEmpresa, Cliente cliente, String fecha, String numeroFactura,
			List<ItemFactura> items, BigDecimal subTotal, BigDecimal totalIVA, BigDecimal totalDcto,
			BigDecimal totalVenta, String vendedor, List<FacturaImpuesto> impuestos) throws IOException {
		final PDRectangle PAGE_SIZE = PDRectangle.A4;
		final float PAGE_W = PAGE_SIZE.getWidth();
		final float PAGE_H = PAGE_SIZE.getHeight();
		final float M = 30; // margen
		final float LEAD = 11; // interlínea
		final float FONT_TITLE = 11;
		final float FONT_HEADER = 7;
		final float FONT_CELL = 6;

		PDDocument doc = new PDDocument();
		try {
			// 1) Simular altura del header
			PDPage dummyPage = new PDPage(PAGE_SIZE);
			float headerH = drawHeader(null, dummyPage, doc, 0, new HeaderData(nombreEmpresa, nitEmpresa,
					direccionEmpresa, telefonoEmpresa, emailEmpresa, numeroFactura, fecha, cliente));
			float footerEst = (impuestos.size() + 4) * LEAD + LEAD;
			float usable = PAGE_H - 2 * M - headerH - footerEst;
			int maxItems = Math.max(1, (int) (usable / (FONT_CELL + 3)));

			int totalPages = (int) Math.ceil((double) items.size() / maxItems);

			for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
				PDPage page = new PDPage(PAGE_SIZE);
				doc.addPage(page);
				try (PDPageContentStream c = new PDPageContentStream(doc, page)) {
					// 2) Dibujar cabecera real y obtener yStart
					float yStart = drawHeader(c, page, doc, pageIndex, new HeaderData(nombreEmpresa, nitEmpresa,
							direccionEmpresa, telefonoEmpresa, emailEmpresa, numeroFactura, fecha, cliente));

					// 3) Configurar columnas proporcionalmente
					float usableW = PAGE_W - 2 * M;
					float[] pct = { 0.10f, 0.15f, 0.30f, 0.10f, 0.15f, 0.10f, 0.10f };
					float[] colW = new float[pct.length], colX = new float[pct.length + 1];
					colX[0] = M;
					for (int i = 0; i < pct.length; i++) {
						colW[i] = usableW * pct[i];
						colX[i + 1] = colX[i] + colW[i];
					}
					float cellPadding = 6;
					float rowH = FONT_CELL + cellPadding * 2;

					// 4) Dibuja encabezado de tabla
					c.setNonStrokingColor(0, 112, 192);
					c.addRect(M, yStart - rowH, usableW, rowH);
					c.fill();
					c.setNonStrokingColor(255, 255, 255);
					c.beginText();
					c.setFont(PDType1Font.HELVETICA_BOLD, FONT_HEADER);
					c.newLineAtOffset(M + 2, yStart - rowH + 2);
					String[] hdrs = { "CÓDIGO", "NOMBRE", "DESCRIP.", "CANT", "PRECIO", "DCTO", "VALOR" };
					for (int i = 0; i < hdrs.length; i++) {
						c.showText(hdrs[i]);
						if (i < hdrs.length - 1)
							c.newLineAtOffset(colW[i], 0);
					}
					c.endText();

					// 5) Dibuja filas
					int start = pageIndex * maxItems;
					int end = Math.min(start + maxItems, items.size());
					// AHORA: dejamos un gap igual a la interlínea (LEAD)
					float curY = yStart - rowH - LEAD - cellPadding;

					c.setNonStrokingColor(0, 0, 0);
					c.setFont(PDType1Font.HELVETICA, FONT_CELL);

					for (int i = start; i < end; i++) {
						ItemFactura it = items.get(i);
						c.beginText();
						c.newLineAtOffset(M + 2, curY + 2);
						String[] vals = { it.codigo, truncar(it.nombre, 20), truncar(it.descripcion, 20),
								String.valueOf(it.cantidad),
								FormatoUtil.formatDecimal(BigDecimal.valueOf(it.precioUnitario)),
								FormatoUtil.formatDecimal(BigDecimal.valueOf(it.descuento)) + "%",
								FormatoUtil.formatDecimal(
										BigDecimal.valueOf(it.cantidad).multiply(BigDecimal.valueOf(it.precioUnitario))
												.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(it.descuento)
														.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
												.setScale(2, RoundingMode.HALF_UP)) };
						for (int j = 0; j < vals.length; j++) {
							c.showText(vals[j]);
							if (j < vals.length - 1)
								c.newLineAtOffset(colW[j], 0);
						}
						c.endText();
						curY -= rowH;
					}

					// 6) Líneas de la tabla
					c.setLineWidth(0.3f);
					for (int i = 0; i <= (end - start) + 1; i++) {
						float ly = yStart - i * rowH;
						c.moveTo(M, ly);
						c.lineTo(M + usableW, ly);
					}
					for (float x : colX) {
						c.moveTo(x, yStart);
						c.lineTo(x, curY + rowH);
					}
					c.stroke();

					// 7) Totales en última página, misma fuente que celdas
					if (pageIndex == totalPages - 1) {
						dibujarTotalesConImpuestosA4(c, M, curY - LEAD, usableW, subTotal, impuestos, totalDcto,
								totalVenta, PDType1Font.HELVETICA, FONT_CELL, LEAD);
					}

					// 8) Pie de página
					c.beginText();
					c.setFont(PDType1Font.HELVETICA, FONT_HEADER);
					String pg = "Pág. " + (pageIndex + 1) + " de " + totalPages;
					float pw = PDType1Font.HELVETICA.getStringWidth(pg) / 1000 * FONT_HEADER;
					c.newLineAtOffset(PAGE_W - M - pw, M / 2);
					c.showText(pg);
					c.endText();
				}
			}

			File out = new File(System.getProperty("java.io.tmpdir"), nombreArchivo);
			doc.save(out);
			return out;
		} finally {
			doc.close();
		}
	}

	private static class HeaderData {
		String emp, nit, dir, tel, mail, numFac, fecha;
		Cliente cli;

		HeaderData(String e, String n, String d, String t, String m, String nf, String f, Cliente c) {
			emp = e;
			nit = n;
			dir = d;
			tel = t;
			mail = m;
			numFac = nf;
			fecha = f;
			cli = c;
		}
	}

	/**
	 * Dibuja logo, datos empresa y cliente. Devuelve Y para iniciar la tabla. Si
	 * 'c' es null, solo simula altura.
	 */
	private float drawHeader(PDPageContentStream c, PDPage page, PDDocument doc, int pageIndex, HeaderData hd)
			throws IOException {
		float pageH = page.getMediaBox().getHeight();
		float pageW = page.getMediaBox().getWidth();
		final float M = 30, LEAD = 11, T = 11, H = 7;

		// Simulación
		if (c == null) {
			return 50 + 5 // logo
					+ T + 2 + 6 * LEAD // empresa
					+ 18 + 4 * LEAD + 5 // facturada a + cliente
					+ 10; // gap
		}

		float y = pageH - M;
		if (pageIndex == 0) {
			// logo centrado
			File logoFile = new File(FacturaHelper.obtenerRutaLogo());
			if (logoFile.exists()) {
				PDImageXObject logo = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), doc);
				float lw = 180, lh = 50;
				c.drawImage(logo, (pageW - lw) / 2, y - lh, lw, lh);
			}
			y -= 55;

			// nombre empresa
			c.beginText();
			c.setFont(PDType1Font.HELVETICA_BOLD, T);
			c.newLineAtOffset(M, y);
			c.showText(hd.emp);
			c.endText();
			y -= T + 2;

			// datos empresa
			c.setFont(PDType1Font.HELVETICA, H);
			for (String line : new String[] { "NIT: " + hd.nit, "Dirección: " + hd.dir, "Tel: " + hd.tel,
					"E-mail: " + hd.mail, "Factura N°: " + hd.numFac, "Fecha: " + hd.fecha }) {
				c.beginText();
				c.newLineAtOffset(M, y);
				c.showText(line);
				c.endText();
				y -= LEAD;
			}
			// <-- gap extra para que la Fecha no sea tapada:
			y -= 4;

			// bloque FACTURADA A
			c.setNonStrokingColor(0, 112, 192);
			c.addRect(M, y - 1, 250, 15);
			c.fill();
			c.setNonStrokingColor(255, 255, 255);
			c.beginText();
			c.setFont(PDType1Font.HELVETICA_BOLD, H);
			c.newLineAtOffset(M + 5, y + 2);
			c.showText("FACTURADA A:");
			c.endText();
			y -= 18;

			// datos cliente
			c.setNonStrokingColor(0, 0, 0);
			c.setFont(PDType1Font.HELVETICA, H);
			for (String line : new String[] { "Nombre: " + hd.cli.getNombre(), "NIT: " + hd.cli.getNit(),
					"Correo: " + hd.cli.getEmail(), "Tel: " + hd.cli.getTelefono() }) {
				c.beginText();
				c.newLineAtOffset(M + 5, y);
				c.showText(line);
				c.endText();
				y -= LEAD;
			}
			y -= 5;
		} else {
			y -= 10;
		}

		return y;
	}

	/**
	 * Dibuja totales e impuestos con misma fuente y tamaño que celdas.
	 */
	private void dibujarTotalesConImpuestosA4(PDPageContentStream c, float xStart, float yStart, float totalWidth,
			BigDecimal subTotal, List<FacturaImpuesto> impuestos, BigDecimal totalDcto, BigDecimal totalVenta,
			PDType1Font font, float fontSize, float lead) throws IOException {
		float y = yStart;

		// impuestos primero
		for (FacturaImpuesto imp : impuestos) {
			if (imp.getPorcentaje().compareTo(BigDecimal.ZERO) == 0 || imp.getValor().compareTo(BigDecimal.ZERO) == 0)
				continue;
			String lbl = "IVA " + imp.getPorcentaje().setScale(2, RoundingMode.HALF_UP) + "%:";
			String val = FormatoUtil.formatDecimal(imp.getValor());

			c.beginText();
			c.setFont(font, fontSize);
			c.newLineAtOffset(xStart, y);
			c.showText(lbl);
			c.endText();

			float w = font.getStringWidth(val) / 1000 * fontSize;
			c.beginText();
			c.setFont(font, fontSize);
			c.newLineAtOffset(xStart + totalWidth - w, y);
			c.showText(val);
			c.endText();

			y -= lead;
		}

		// SUBTOTAL, DESCUENTO, TOTAL
		String[] et = { "SUBTOTAL:", "DESCUENTO:", "TOTAL:" };
		BigDecimal[] vs = { subTotal, totalDcto, totalVenta };

		for (int i = 0; i < et.length; i++) {
			if (i == 1 && vs[i].compareTo(BigDecimal.ZERO) == 0)
				continue;
			String lbl = et[i];
			String val = FormatoUtil.formatDecimal(vs[i]);

			c.beginText();
			c.setFont(font, fontSize);
			c.newLineAtOffset(xStart, y);
			c.showText(lbl);
			c.endText();

			float w = font.getStringWidth(val) / 1000 * fontSize;
			c.beginText();
			c.setFont(font, fontSize);
			c.newLineAtOffset(xStart + totalWidth - w, y);
			c.showText(val);
			c.endText();

			y -= lead;
		}
	}

	private String truncar(String txt, int len) {
		if (txt == null)
			return "";
		return txt.length() > len ? txt.substring(0, len) : txt;
	}

	public File generarFacturaTicket80mm(String nombreArchivo, String nombreEmpresa, String nitEmpresa,
			String direccionEmpresa, String telefonoEmpresa, String emailEmpresa, Cliente cliente, String fecha,
			String numeroFactura, List<ItemFactura> items, BigDecimal subTotal, BigDecimal totalIVA,
			BigDecimal totalDcto, BigDecimal totalVenta, String vendedor, List<FacturaImpuesto> impuestos)
			throws IOException {
		// Fuentes y tamaños
		final float FONT_SIZE_TITULO = 8f;
		final float FONT_SIZE_CABECERA = 9f;
		PDType1Font FONT_REGULAR = PDType1Font.HELVETICA;
		PDType1Font FONT_BOLD = PDType1Font.HELVETICA_BOLD;

		// Alto dinámico
		int lineas = 40 + items.size() * 2; // más espacio para posibles wraps
		float alto = Math.max(400, FONT_SIZE_CABECERA * lineas);
		PDRectangle ticketSize = new PDRectangle(226.77f, alto);

		File archivo = new File(System.getProperty("java.io.tmpdir"), nombreArchivo);
		try (PDDocument documento = new PDDocument()) {
			PDPage pagina = new PDPage(ticketSize);
			documento.addPage(pagina);
			try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {
				float margin = 5f;
				float width = ticketSize.getWidth() - 2 * margin;
				float y = ticketSize.getHeight() - margin - FONT_SIZE_CABECERA;

				// ===== Nombre Empresa ===== (centrado)
				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_CABECERA);
				float titleWidth = FONT_BOLD.getStringWidth(nombreEmpresa) / 1000 * FONT_SIZE_CABECERA;
				float titleX = (ticketSize.getWidth() - titleWidth) / 2;
				contenido.newLineAtOffset(titleX, y);
				contenido.showText(nombreEmpresa);
				contenido.endText();
				y -= (FONT_SIZE_CABECERA + 4);

				// ===== Cabecera Empresa =====
				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(margin, y);
				contenido.showText("NIT:");
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				contenido.showText(" " + nitEmpresa);
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 2);

				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(margin, y);
				contenido.showText("Tel:");
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				contenido.showText(" " + telefonoEmpresa);
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 2);

				// Dirección con wrap
				List<String> linesDir = wrapText(direccionEmpresa, FONT_REGULAR, FONT_SIZE_TITULO, width);
				for (String linea : linesDir) {
					contenido.beginText();
					contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
					contenido.newLineAtOffset(margin, y);
					contenido.showText(linea);
					contenido.endText();
					y -= (FONT_SIZE_TITULO + 2);
				}

				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(margin, y);
				contenido.showText("E-mail:");
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				contenido.showText(" " + emailEmpresa);
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 6);

				// ===== Info Factura / Vendedor =====
				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(margin, y);
				contenido.showText("Factura:");
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				contenido.showText(" " + numeroFactura);
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 2);

				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(margin, y);
				contenido.showText("Fecha:");
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				contenido.showText(" " + fecha);
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 2);

				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(margin, y);
				contenido.showText("Vendedor:");
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				contenido.showText(" " + vendedor);
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 6);

				// Línea separadora
				contenido.setLineWidth(0.3f);
				contenido.moveTo(margin, y);
				contenido.lineTo(ticketSize.getWidth() - margin, y);
				contenido.stroke();
				y -= (FONT_SIZE_TITULO + 4);

				// ===== Cliente =====
				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(margin, y);
				contenido.showText("Cliente:");
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				contenido.showText(" " + cliente.getNombre());
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 2);

				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(margin, y);
				contenido.showText("NIT:");
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				contenido.showText(" " + cliente.getNit());
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 2);

				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(margin, y);
				contenido.showText("Tel:");
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				contenido.showText(" " + cliente.getTelefono());
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 2);

				if (cliente.getEmail() != null) {
					contenido.beginText();
					contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
					contenido.newLineAtOffset(margin, y);
					contenido.showText("E-mail:");
					contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
					contenido.showText(" " + cliente.getEmail());
					contenido.endText();
					y -= (FONT_SIZE_TITULO + 4);
				} else {
					y -= (FONT_SIZE_TITULO + 2);
				}

				// Separador antes de ítems
				contenido.moveTo(margin, y);
				contenido.lineTo(ticketSize.getWidth() - margin, y);
				contenido.stroke();
				y -= (FONT_SIZE_TITULO + 4);

				// ===== Encabezado Tabla =====
				float colNameX = margin;
				float colCantX = margin + width * 0.25f;
				float colUnitX = margin + width * 0.35f;
				float colDctoX = margin + width * 0.6f;
				float colTotalX = margin + width * 0.75f;
				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(colNameX, y);
				contenido.showText("Nombre");
				contenido.newLineAtOffset(colCantX - colNameX, 0);
				contenido.showText("Cant");
				contenido.newLineAtOffset(colUnitX - colCantX, 0);
				contenido.showText("Unit");
				contenido.newLineAtOffset(colDctoX - colUnitX, 0);
				contenido.showText("Dcto");
				contenido.newLineAtOffset(colTotalX - colDctoX, 0);
				contenido.showText("Total");
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 4);

				// ===== Detalle Ítems =====
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				for (ItemFactura item : items) {
					// 1. Truncar nombre a 10 chars + "..."
					int maxChars = 10;
					String nombre = item.nombre.length() > maxChars ? item.nombre.substring(0, maxChars) + "..."
							: item.nombre;

					// Nombre
					contenido.beginText();
					contenido.newLineAtOffset(colNameX, y);
					contenido.showText(nombre);
					contenido.endText();

					// Cantidad
					contenido.beginText();
					contenido.newLineAtOffset(colCantX, y);
					contenido.showText(String.valueOf(item.cantidad));
					contenido.endText();

					// Unitario (precio lista)
					contenido.beginText();
					contenido.newLineAtOffset(colUnitX, y);
					contenido.showText(FormatoUtil.formatDecimal(BigDecimal.valueOf(item.precioUnitario)));
					contenido.endText();

					// Descuento (reserva espacio aunque vacío)
					contenido.beginText();
					contenido.newLineAtOffset(colDctoX, y);
					String dctoText = item.descuento > 0 ? item.descuento + "%" : "";
					contenido.showText(dctoText);
					contenido.endText();

					// Total
					BigDecimal qty = BigDecimal.valueOf(item.cantidad);
					BigDecimal unitPrice = BigDecimal.valueOf(item.precioUnitario);
					BigDecimal discountPct = BigDecimal.valueOf(item.descuento).divide(BigDecimal.valueOf(100), 4,
							RoundingMode.HALF_UP);

					BigDecimal bruto = unitPrice.multiply(qty);
					BigDecimal total = bruto.multiply(BigDecimal.ONE.subtract(discountPct)).setScale(2,
							RoundingMode.HALF_UP);

					contenido.beginText();
					contenido.newLineAtOffset(colTotalX, y);
					contenido.showText(FormatoUtil.formatDecimal(total));
					contenido.endText();

					// Avanzar línea
					y -= (FONT_SIZE_TITULO + 4);
				}

				// Separador antes de totales
				contenido.moveTo(margin, y);
				contenido.lineTo(ticketSize.getWidth() - margin, y);
				contenido.stroke();
				y -= (FONT_SIZE_TITULO + 6);

				// ===== Totales finales con impuestos al principio =====
				float labelX = margin;
				float valueX = margin + width;

				// 1) Dibuja todas las líneas de IVA primero, usando yPos
				float yPos = y - (FONT_SIZE_TITULO + 2);
				for (FacturaImpuesto imp : impuestos) {
					if (imp.getPorcentaje().compareTo(BigDecimal.ZERO) == 0
							|| imp.getValor().compareTo(BigDecimal.ZERO) == 0) {
						continue;
					}
					// Etiqueta IVA X %:
					contenido.beginText();
					contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
					contenido.newLineAtOffset(labelX, yPos);
					contenido.showText("IVA " + imp.getPorcentaje().setScale(2, RoundingMode.HALF_UP) + "%:");
					contenido.endText();
					// Valor
					contenido.beginText();
					contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
					float valorWidth = FONT_REGULAR.getStringWidth(FormatoUtil.formatDecimal(imp.getValor())) / 1000
							* FONT_SIZE_TITULO;
					contenido.newLineAtOffset(valueX - valorWidth, yPos);
					contenido.showText(FormatoUtil.formatDecimal(imp.getValor()));
					contenido.endText();

					yPos -= (FONT_SIZE_TITULO + 2);
				}

				// 2) Ahora actualiza tu 'y' para el resto de totales
//				    Lo igualamos a yPos menos un pequeño padding
				y = yPos - (FONT_SIZE_TITULO + 2);

				// 3) Subtotal
				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(labelX, y);
				contenido.showText("Subtotal:");
				contenido.endText();
				contenido.beginText();
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				float textWidth = FONT_REGULAR.getStringWidth(FormatoUtil.formatDecimal(subTotal)) / 1000
						* FONT_SIZE_TITULO;
				contenido.newLineAtOffset(valueX - textWidth, y);
				contenido.showText(FormatoUtil.formatDecimal(subTotal));
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 2);

				// 4) IVA (global)
				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(labelX, y);
				contenido.showText("IVA:");
				contenido.endText();
				contenido.beginText();
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				textWidth = FONT_REGULAR.getStringWidth(FormatoUtil.formatDecimal(totalIVA)) / 1000 * FONT_SIZE_TITULO;
				contenido.newLineAtOffset(valueX - textWidth, y);
				contenido.showText(FormatoUtil.formatDecimal(totalIVA));
				contenido.endText();
				y -= (FONT_SIZE_TITULO + 2);

				// 5) Descuento (si aplica)
				if (totalDcto.compareTo(BigDecimal.ZERO) > 0) {
					contenido.beginText();
					contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
					contenido.newLineAtOffset(labelX, y);
					contenido.showText("Descuento:");
					contenido.endText();
					contenido.beginText();
					contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
					textWidth = FONT_REGULAR.getStringWidth(FormatoUtil.formatDecimal(totalDcto)) / 1000
							* FONT_SIZE_TITULO;
					contenido.newLineAtOffset(valueX - textWidth, y);
					contenido.showText(FormatoUtil.formatDecimal(totalDcto));
					contenido.endText();
					y -= (FONT_SIZE_TITULO + 2);
				}

				// 6) Total final
				contenido.beginText();
				contenido.setFont(FONT_BOLD, FONT_SIZE_TITULO);
				contenido.newLineAtOffset(labelX, y);
				contenido.showText("Total:");
				contenido.endText();
				contenido.beginText();
				contenido.setFont(FONT_REGULAR, FONT_SIZE_TITULO);
				textWidth = FONT_REGULAR.getStringWidth(FormatoUtil.formatDecimal(totalVenta)) / 1000
						* FONT_SIZE_TITULO;
				contenido.newLineAtOffset(valueX - textWidth, y);
				contenido.showText(FormatoUtil.formatDecimal(totalVenta));
				contenido.endText();

			}
			documento.save(archivo);
		}
		return archivo;
	}

	/**
	 * Divide un texto en varias líneas para que quepa dentro de un ancho dado.
	 */
	private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
		List<String> lines = new ArrayList<>();
		String[] words = text.split(" ");
		StringBuilder line = new StringBuilder();
		for (String word : words) {
			String tmp = line.length() == 0 ? word : line + " " + word;
			float size = font.getStringWidth(tmp) / 1000 * fontSize;
			if (size > maxWidth && line.length() > 0) {
				lines.add(line.toString());
				line = new StringBuilder(word);
			} else {
				line = new StringBuilder(tmp);
			}
		}
		if (line.length() > 0)
			lines.add(line.toString());
		return lines;
	}

	/**
	 * Dibuja subtotal, cada porcentaje de IVA y sus valores, descuento y total
	 */
	private void dibujarTotalesConImpuestos(PDPageContentStream contenido, float xStart, float yStart,
			BigDecimal subTotal, List<FacturaImpuesto> impuestos, BigDecimal totalDcto, BigDecimal totalVenta)
			throws IOException {
		final float rowHeight = 14f;
		PDType1Font bold = PDType1Font.HELVETICA_BOLD;
		PDType1Font regular = PDType1Font.HELVETICA;
		float labelX = xStart;
		float valueX = xStart + 200; // ajusta este offset según tu layout
		float y = yStart;

		// 1) Subtotal
		contenido.beginText();
		contenido.setFont(bold, 10);
		contenido.newLineAtOffset(labelX, y);
		contenido.showText("SUBTOTAL:");
		contenido.endText();

		contenido.beginText();
		contenido.setFont(regular, 10);
		contenido.newLineAtOffset(valueX, y);
		contenido.showText(FormatoUtil.formatDecimal(subTotal));
		contenido.endText();

		y -= rowHeight;

		// 2) Desglose de IVA por porcentaje
		for (FacturaImpuesto imp : impuestos) {
			if (imp.getPorcentaje().compareTo(BigDecimal.ZERO) == 0 || imp.getValor().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}
			contenido.beginText();
			contenido.setFont(bold, 10);
			contenido.newLineAtOffset(labelX, y);
			contenido.showText("IVA " + imp.getPorcentaje().setScale(2, RoundingMode.HALF_UP) + " %:");
			contenido.endText();

			contenido.beginText();
			contenido.setFont(regular, 10);
			contenido.newLineAtOffset(valueX, y);
			contenido.showText(FormatoUtil.formatDecimal(imp.getValor()));
			contenido.endText();

			y -= rowHeight;
		}

		// 3) Descuento (si aplica)
		if (totalDcto.compareTo(BigDecimal.ZERO) > 0) {
			contenido.beginText();
			contenido.setFont(bold, 10);
			contenido.newLineAtOffset(labelX, y);
			contenido.showText("DESCUENTO:");
			contenido.endText();

			contenido.beginText();
			contenido.setFont(regular, 10);
			contenido.newLineAtOffset(valueX, y);
			contenido.showText(FormatoUtil.formatDecimal(totalDcto));
			contenido.endText();

			y -= rowHeight;
		}

		// 4) Total final
		contenido.beginText();
		contenido.setFont(bold, 10);
		contenido.newLineAtOffset(labelX, y);
		contenido.showText("TOTAL:");
		contenido.endText();

		contenido.beginText();
		contenido.setFont(regular, 10);
		contenido.newLineAtOffset(valueX, y);
		contenido.showText(FormatoUtil.formatDecimal(totalVenta));
		contenido.endText();
	}

}
