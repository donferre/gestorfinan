package com.italoweb.gestorfinan.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.italoweb.gestorfinan.dto.CompraEstadisticaDTO;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class GraficosCompraDAO {

	public List<CompraEstadisticaDTO> obtenerComprasPorDia() {
		int anioActual = Year.now().getValue();
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT c.fechaIngreso, COUNT(c.id)
					    FROM Compras c
					    WHERE YEAR(c.fechaIngreso) = :anio
					    GROUP BY c.fechaIngreso
					    ORDER BY c.fechaIngreso
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("anio", anioActual);
			List<Object[]> resultados = query.getResultList();

			List<CompraEstadisticaDTO> lista = new ArrayList<>();
			for (Object[] fila : resultados) {
				String fecha = fila[0].toString();
				Long count = (Long) fila[1];
				BigDecimal cantidad = BigDecimal.valueOf(count);
				lista.add(new CompraEstadisticaDTO(fecha, cantidad));
			}
			return lista;
		}
	}

	public List<CompraEstadisticaDTO> obtenerComprasPorMes() {
		int anioActual = Year.now().getValue();
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT YEAR(c.fechaIngreso), MONTH(c.fechaIngreso), COUNT(c.id)
					    FROM Compras c
					    WHERE YEAR(c.fechaIngreso) = :anio
					    GROUP BY YEAR(c.fechaIngreso), MONTH(c.fechaIngreso)
					    ORDER BY YEAR(c.fechaIngreso), MONTH(c.fechaIngreso)
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("anio", anioActual);
			List<Object[]> resultados = query.getResultList();

			List<CompraEstadisticaDTO> lista = new ArrayList<>();
			for (Object[] fila : resultados) {
				Integer anio = (Integer) fila[0];
				Integer mes = (Integer) fila[1];
				Long count = (Long) fila[2];
				BigDecimal cantidad = BigDecimal.valueOf(count);
				String periodo = anio + "-" + String.format("%02d", mes);
				lista.add(new CompraEstadisticaDTO(periodo, cantidad));
			}
			return lista;
		}
	}

	public List<CompraEstadisticaDTO> obtenerVentasPorMes() {
		int anioActual = Year.now().getValue();
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT YEAR(v.fechaVenta), MONTH(v.fechaVenta), COUNT(v.id)
					    FROM Ventas v
					    WHERE YEAR(v.fechaVenta) = :anio
					    GROUP BY YEAR(v.fechaVenta), MONTH(v.fechaVenta)
					    ORDER BY YEAR(v.fechaVenta), MONTH(v.fechaVenta)
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("anio", anioActual);
			List<Object[]> resultados = query.getResultList();

			List<CompraEstadisticaDTO> lista = new ArrayList<>();
			for (Object[] fila : resultados) {
				Integer anio = (Integer) fila[0];
				Integer mes = (Integer) fila[1];
				Long count = (Long) fila[2];
				BigDecimal cantidad = BigDecimal.valueOf(count);
				String periodo = anio + "-" + String.format("%02d", mes);
				lista.add(new CompraEstadisticaDTO(periodo, cantidad));
			}
			return lista;
		}
	}

	public CompraEstadisticaDTO obtenerProductoMasVendido() {
		YearMonth mesAnterior = YearMonth.now().minusMonths(1);
		int anio = mesAnterior.getYear();
		int mes = mesAnterior.getMonthValue();

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT p.nombre, SUM(vd.cantidadVendidad)
					    FROM VentaDetalle vd
					    JOIN vd.producto p
					    JOIN vd.venta v
					    WHERE YEAR(v.fechaVenta) = :anio AND MONTH(v.fechaVenta) = :mes
					    GROUP BY p.nombre
					    ORDER BY SUM(vd.cantidadVendidad) DESC
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class).setParameter("anio", anio)
					.setParameter("mes", mes).setMaxResults(1);

			Object[] fila = query.uniqueResult();
			if (fila != null) {
				String nombre = (String) fila[0];
				Number rawCantidad = (Number) fila[1];
				BigDecimal cantidad = BigDecimal.valueOf(rawCantidad.doubleValue());
				return new CompraEstadisticaDTO(nombre, cantidad);
			}
			return null;
		}
	}

	public CompraEstadisticaDTO obtenerProductoMasVendidoActual() {
		int anioActual = Year.now().getValue();
		int mesActual = LocalDate.now().getMonthValue();
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT p.nombre, SUM(vd.cantidadVendidad)
					    FROM VentaDetalle vd
					    JOIN vd.producto p
					    JOIN vd.venta v
					    WHERE YEAR(v.fechaVenta) = :anio AND MONTH(v.fechaVenta) = :mes
					    GROUP BY p.nombre
					    ORDER BY SUM(vd.cantidadVendidad) DESC
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class).setParameter("anio", anioActual)
					.setParameter("mes", mesActual).setMaxResults(1);
			Object[] fila = query.uniqueResult();
			if (fila != null) {
				String nombre = (String) fila[0];
				Number rawCantidad = (Number) fila[1];
				BigDecimal cantidad = BigDecimal.valueOf(rawCantidad.doubleValue());
				return new CompraEstadisticaDTO(nombre, cantidad);
			}
			return null;
		}
	}

	public List<CompraEstadisticaDTO> obtenerTop5CategoriasMasVendidas() {
		YearMonth mesAnterior = YearMonth.now().minusMonths(1);
		int anio = mesAnterior.getYear();
		int mes = mesAnterior.getMonthValue();
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT c.nombre, SUM(vd.cantidadVendidad)
					    FROM VentaDetalle vd
					    JOIN vd.producto p
					    JOIN p.categoria c
					    JOIN vd.venta v
					    WHERE YEAR(v.fechaVenta) = :anio AND MONTH(v.fechaVenta) = :mes
					    GROUP BY c.nombre
					    ORDER BY SUM(vd.cantidadVendidad) DESC
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class).setParameter("anio", anio)
					.setParameter("mes", mes).setMaxResults(5);

			List<Object[]> resultados = query.getResultList();
			List<CompraEstadisticaDTO> lista = new ArrayList<>();
			for (Object[] fila : resultados) {
				String categoria = (String) fila[0];
				Number rawCantidad = (Number) fila[1];
				BigDecimal cantidad = BigDecimal.valueOf(rawCantidad.doubleValue());
				lista.add(new CompraEstadisticaDTO(categoria, cantidad));
			}
			return lista;
		}
	}

	public List<CompraEstadisticaDTO> obtenerTop5CategoriasMasVendidasActual() {
		int anioActual = Year.now().getValue();
		int mesActual = LocalDate.now().getMonthValue();
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT c.nombre, SUM(vd.cantidadVendidad)
					    FROM VentaDetalle vd
					    JOIN vd.producto p
					    JOIN p.categoria c
					    JOIN vd.venta v
					    WHERE YEAR(v.fechaVenta) = :anio AND MONTH(v.fechaVenta) = :mes
					    GROUP BY c.nombre
					    ORDER BY SUM(vd.cantidadVendidad) DESC
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class).setParameter("anio", anioActual)
					.setParameter("mes", mesActual).setMaxResults(5);

			List<Object[]> resultados = query.getResultList();
			List<CompraEstadisticaDTO> lista = new ArrayList<>();
			for (Object[] fila : resultados) {
				String categoria = (String) fila[0];
				Number rawCantidad = (Number) fila[1];
				BigDecimal cantidad = BigDecimal.valueOf(rawCantidad.doubleValue());
				lista.add(new CompraEstadisticaDTO(categoria, cantidad));
			}
			return lista;
		}
	}

	public List<CompraEstadisticaDTO> obtenerTotalComprasPorMes() {
		int anioActual = Year.now().getValue();
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT YEAR(c.fechaPago), MONTH(c.fechaPago), SUM(cd.totalCompra)
					    FROM CompraDetalle cd
					    JOIN cd.compra c
					    WHERE YEAR(c.fechaPago) = :anio
					    GROUP BY YEAR(c.fechaPago), MONTH(c.fechaPago)
					    ORDER BY YEAR(c.fechaPago), MONTH(c.fechaPago)
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("anio", anioActual);
			List<Object[]> resultados = query.getResultList();

			List<CompraEstadisticaDTO> lista = new ArrayList<>();
			for (Object[] fila : resultados) {
				Integer anio = (Integer) fila[0];
				Integer mes = (Integer) fila[1];
				BigDecimal total = (BigDecimal) fila[2];
				String periodo = anio + "-" + String.format("%02d", mes);
				lista.add(new CompraEstadisticaDTO(periodo, total));
			}
			return lista;
		}
	}

	public List<CompraEstadisticaDTO> obtenerTotalVentasPorMes() {
		int anioActual = Year.now().getValue();
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT YEAR(v.fechaVenta), MONTH(v.fechaVenta), SUM(vd.totalVenta)
					    FROM VentaDetalle vd
					    JOIN vd.venta v
					    WHERE YEAR(v.fechaVenta) = :anio
					    GROUP BY YEAR(v.fechaVenta), MONTH(v.fechaVenta)
					    ORDER BY YEAR(v.fechaVenta), MONTH(v.fechaVenta)
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("anio", anioActual);
			List<Object[]> resultados = query.getResultList();

			List<CompraEstadisticaDTO> lista = new ArrayList<>();
			for (Object[] fila : resultados) {
				Integer anio = (Integer) fila[0];
				Integer mes = (Integer) fila[1];
				BigDecimal total = (BigDecimal) fila[2];
				String periodo = anio + "-" + String.format("%02d", mes);
				lista.add(new CompraEstadisticaDTO(periodo, total));
			}
			return lista;
		}
	}

	public List<CompraEstadisticaDTO> obtenerTotalVentasMesActual() {
		YearMonth ahora = YearMonth.now();
		int anio = ahora.getYear();
		int mes = ahora.getMonthValue();

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT SUM(vd.totalVenta)
					    FROM VentaDetalle vd
					    JOIN vd.venta v
					    WHERE YEAR(v.fechaVenta) = :anio AND MONTH(v.fechaVenta) = :mes
					""";

			Query<BigDecimal> query = session.createQuery(hql, BigDecimal.class);
			query.setParameter("anio", anio);
			query.setParameter("mes", mes);

			BigDecimal total = query.uniqueResult();
			String periodo = anio + "-" + String.format("%02d", mes);

			CompraEstadisticaDTO dto = new CompraEstadisticaDTO(periodo, total != null ? total : BigDecimal.ZERO);
			return List.of(dto);
		}
	}

	public List<CompraEstadisticaDTO> obtenerTotalComprasMesActual() {
		YearMonth ahora = YearMonth.now();
		int anio = ahora.getYear();
		int mes = ahora.getMonthValue();

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT SUM(cd.totalCompra)
					    FROM CompraDetalle cd
					    JOIN cd.compra c
					    WHERE YEAR(c.fechaPago) = :anio AND MONTH(c.fechaPago) = :mes
					""";

			Query<BigDecimal> query = session.createQuery(hql, BigDecimal.class);
			query.setParameter("anio", anio);
			query.setParameter("mes", mes);

			BigDecimal total = query.uniqueResult();
			String periodo = anio + "-" + String.format("%02d", mes);

			CompraEstadisticaDTO dto = new CompraEstadisticaDTO(periodo, total != null ? total : BigDecimal.ZERO);
			return List.of(dto);
		}
	}

}
