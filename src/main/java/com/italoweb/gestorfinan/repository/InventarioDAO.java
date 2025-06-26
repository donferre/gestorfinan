package com.italoweb.gestorfinan.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import com.italoweb.gestorfinan.dto.InventarioResumenDTO;
import com.italoweb.gestorfinan.model.Cliente;
import com.italoweb.gestorfinan.model.Inventario;
import com.italoweb.gestorfinan.model.Producto;
import com.italoweb.gestorfinan.model.Proveedor;
import com.italoweb.gestorfinan.model.TipoMovimiento;
import com.italoweb.gestorfinan.model.compra.CompraDetalle;
import com.italoweb.gestorfinan.model.compra.Compras;
import com.italoweb.gestorfinan.model.venta.VentaDetalle;
import com.italoweb.gestorfinan.model.venta.Ventas;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class InventarioDAO extends GenericDAOImpl<Inventario, Long> {
	public InventarioDAO() {
		super(Inventario.class);
	}

	public void registrarMovimientoInventarioPorCompra(Compras compraGuardada) {
		List<CompraDetalle> detalles = compraGuardada.getDetalles();
		Proveedor proveedor = compraGuardada.getProveedor();
		ProductoDAO productoDAO = new ProductoDAO();
		for (CompraDetalle detalle : detalles) {
			Producto producto = detalle.getProducto();
			producto.setPrecioCompra(detalle.getPrecioCompra());
			producto.setPrecioVenta(detalle.getPrecioVenta());

			productoDAO.update(producto);

			Inventario movimiento = new Inventario();
			movimiento.setProducto(detalle.getProducto());
			movimiento.setCompra(compraGuardada);
			movimiento.getCompra().setProveedor(proveedor);;
			movimiento.setCantidadStock(detalle.getCantidadIngresar());
			movimiento.setTipoMovimiento(TipoMovimiento.INGRESOS);
			movimiento.setFecha(LocalDateTime.now());
			movimiento.setObservaciones("Ingreso por compra #" + compraGuardada.getId());

			this.save(movimiento);
		}
	}
	
	public void registrarMovimientoInventarioPorVenta(Ventas ventaGuardada) {
		List<VentaDetalle> detalles = ventaGuardada.getDetalles();
		Cliente cliente = ventaGuardada.getCliente();
		ProductoDAO productoDAO = new ProductoDAO();
		for (VentaDetalle detalle : detalles) {
			Producto producto = detalle.getProducto();
			producto.setPrecioCompra(detalle.getProducto().getPrecioCompra());
			producto.setPrecioVenta(detalle.getProducto().getPrecioVenta());

			productoDAO.update(producto);

			Inventario movimiento = new Inventario();
			movimiento.setProducto(detalle.getProducto());
			movimiento.setVenta(ventaGuardada);
			movimiento.getVenta().setCliente(cliente);
			movimiento.setCantidadStock(detalle.getCantidadVendidad());
			movimiento.setTipoMovimiento(TipoMovimiento.EGRESOS);
			movimiento.setFecha(LocalDateTime.now());
			movimiento.setObservaciones("Venta registrada #" + ventaGuardada.getId());

			this.save(movimiento);
		}
	}
	
	public void registrarMovimientoInventarioPorVenta(Ventas ventaGuardada, Session session) {
	    List<VentaDetalle> detalles = ventaGuardada.getDetalles();
	    Cliente cliente = ventaGuardada.getCliente();

	    for (VentaDetalle detalle : detalles) {
	        Producto producto = detalle.getProducto();
	        producto.setPrecioCompra(detalle.getProducto().getPrecioCompra());
	        producto.setPrecioVenta(detalle.getProducto().getPrecioVenta());

	        session.merge(producto); 

	        Inventario movimiento = new Inventario();
	        movimiento.setProducto(detalle.getProducto());
	        movimiento.setVenta(ventaGuardada);
	        movimiento.getVenta().setCliente(cliente);
	        movimiento.setCantidadStock(detalle.getCantidadVendidad());
	        movimiento.setTipoMovimiento(TipoMovimiento.EGRESOS);
	        movimiento.setFecha(LocalDateTime.now());
	        movimiento.setObservaciones("Venta registrada #" + ventaGuardada.getId());

	        session.persist(movimiento); 
	    }
	}


	public List<InventarioResumenDTO> obtenerResumenInventario() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
					    SELECT
					        i.producto.id,
					        i.producto.codigo,
					        i.producto.nombre,
					        SUM(
					            CASE
					                WHEN i.tipoMovimiento = :ingreso THEN i.cantidadStock
					                WHEN i.tipoMovimiento = :egreso THEN -i.cantidadStock
					                ELSE 0
					            END
					        ),
					        MAX(i.fecha)
					    FROM Inventario i
					    GROUP BY i.producto.id, i.producto.codigo, i.producto.nombre
					    ORDER BY i.producto.nombre
					""";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("ingreso", TipoMovimiento.INGRESOS);
			query.setParameter("egreso", TipoMovimiento.EGRESOS);

			List<Object[]> resultados = query.getResultList();

			List<InventarioResumenDTO> resumen = new ArrayList<>();
			for (Object[] fila : resultados) {
				InventarioResumenDTO dto = new InventarioResumenDTO();
				dto.setProductoId((Long) fila[0]);
				dto.setCodigoProducto((String) fila[1]);
				dto.setNombreProducto((String) fila[2]);
				dto.setStockActual(((Number) fila[3]).intValue());
				dto.setUltimaActualizacion((LocalDateTime) fila[4]);
				resumen.add(dto);
			}

			return resumen;
		}
	}
	
	public List<Object[]> obtenerStockActualPorProducto() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = """
						SELECT
						    i.producto.id,
						    SUM(
						        CASE
						            WHEN i.tipoMovimiento = :ingreso THEN i.cantidadStock
						            WHEN i.tipoMovimiento = :egreso THEN -i.cantidadStock
						            ELSE 0
						        END
						    )
						FROM Inventario i
						GROUP BY i.producto.id
			""";

			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("ingreso", TipoMovimiento.INGRESOS);
			query.setParameter("egreso", TipoMovimiento.EGRESOS);
			return query.getResultList();
		}
	}


}
