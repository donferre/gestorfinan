package com.italoweb.gestorfinan.model.factura;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.italoweb.gestorfinan.model.venta.Ventas;
import com.italoweb.gestorfinan.repository.InventarioDAO;
import com.italoweb.gestorfinan.util.HibernateUtil;

public class VentaFacturaService {

	private final InventarioDAO inventarioDAO = new InventarioDAO();

	public void guardarVentaConFactura(Ventas venta, Factura factura) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			// 1) Asegura la bidireccionalidad
			if (factura.getDetalles() != null) {
				for (FacturaDetalle fd : factura.getDetalles()) {
					fd.setFactura(factura);
				}
			}

			// 2) Persiste factura Y venta en la MISMA tx/session
			session.persist(factura);
			venta.setFactura(factura);
			session.persist(venta);

			// 3) Movimiento de inventario con la misma sesión
			inventarioDAO.registrarMovimientoInventarioPorVenta(venta, session);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw new RuntimeException("Error al guardar venta y factura: " + e.getMessage(), e);
		} finally {
			session.close();
		}
	}
}
