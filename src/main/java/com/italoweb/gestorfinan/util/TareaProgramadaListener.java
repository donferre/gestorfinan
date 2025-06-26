package com.italoweb.gestorfinan.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.italoweb.gestorfinan.model.ProductoDescuento;
import com.italoweb.gestorfinan.repository.ProductoDescuentoDAO;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class TareaProgramadaListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;
    private ProductoDescuentoDAO productoDescuentoDAO = new ProductoDescuentoDAO();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        programarProximaEjecucion();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void programarProximaEjecucion() {
        long delay = calcularDelayHastaMedianocheEnSegundos();
        System.out.println("Próxima ejecución programada en " + delay + " segundos ("
                + LocalDateTime.now().plusSeconds(delay) + ")");

        scheduler.schedule(() -> {
            try {
                ejecutarLimpieza();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Al terminar, programa la siguiente
            programarProximaEjecucion();
        }, delay, TimeUnit.SECONDS);
    }

    private void ejecutarLimpieza() {
        System.out.println("Iniciando limpieza de descuentos expirados: " + LocalDateTime.now());
        List<ProductoDescuento> expirados = productoDescuentoDAO.obtenerDescuentosFinalizadosHastaHoy();
        for (ProductoDescuento pd : expirados) {
            productoDescuentoDAO.delete(pd);
            System.out.println("Eliminado descuento ID: " + pd.getId());
        }
        System.out.println("Limpieza completada. Total eliminados: " + expirados.size());
    }

    private long calcularDelayHastaMedianocheEnSegundos() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proximaMedianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
        Duration duracion = Duration.between(ahora, proximaMedianoche);
        return duracion.getSeconds();
    }
}
