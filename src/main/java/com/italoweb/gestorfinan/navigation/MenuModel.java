package com.italoweb.gestorfinan.navigation;

import java.util.ArrayList;
import java.util.List;

import com.italoweb.gestorfinan.util.HEXUtil;

public class MenuModel {

    private List<MenuItem> menuItems;

    public MenuModel() {
    }

    public List<MenuItem> cargarMenu(){
        menuItems = new ArrayList<>();
        // Menú principal
        menuItems.add(new MenuItem("Inicio", bookmarkUrl("/project/index.zul"), "z-icon-home"));
        menuItems.add(new MenuItem("Configuraciones", bookmarkUrl("/configuraciones_base.zul"), "z-icon-user"));
        menuItems.add(new MenuItem("Clientes", bookmarkUrl("/cliente.zul"), "z-icon-user"));
        menuItems.add(new MenuItem("Conceptos", bookmarkUrl("/conceptos.zul"), "z-icon-user"));
        menuItems.add(new MenuItem("Productos", bookmarkUrl("/productos.zul"), "z-icon-signal"));
        menuItems.add(new MenuItem("Medios de Pagos", bookmarkUrl("/mediosPagos.zul"), "z-icon-signal"));
        menuItems.add(new MenuItem("Notas", bookmarkUrl("/nota.zul"), "z-icon-signal"));

        //Caja
        MenuItem caja = new MenuItem("Caja", null, "z-icon-list");
        caja.addSubMenu(new MenuItem("Ingresos", bookmarkUrl("/ingresos.zul"), "z-icon-user"));
        caja.addSubMenu(new MenuItem("Egresos", bookmarkUrl("/egresos.zul"), "z-icon-user"));
        menuItems.add(caja);

        // Habitaciones con submenús
        MenuItem habitaciones = new MenuItem("Habitaciones", null, "z-icon-th-list");
        habitaciones.addSubMenu(new MenuItem("Analytics", bookmarkUrl("/project/project.zul"), null));
        habitaciones.addSubMenu(new MenuItem("Ecommerce", bookmarkUrl("/project/chartjs.zul"), null));
        habitaciones.addSubMenu(new MenuItem("Crypto", bookmarkUrl("/project/home.zul"), null));
        menuItems.add(habitaciones);

        // Reportes con submenús
        MenuItem reportes = new MenuItem("Reportes", null, "z-icon-book");
        reportes.addSubMenu(new MenuItem("Dashboard Analytics", "#dashboard-analytics.zul", null));
        reportes.addSubMenu(new MenuItem("Dashboard Ecommerce", "#dashboard-ecommerce.zul", null));
        menuItems.add(reportes);

        // Reportes con submenús
        MenuItem ajustes = new MenuItem("Ajustes", null, "z-icon-book");
        ajustes.addSubMenu(new MenuItem("Menu", bookmarkUrl("/menuConfig.zul"), null));
        menuItems.add(ajustes);

        // Cerrar Sesión con submenús
        MenuItem auth = new MenuItem("Cerrar Sesión", null, "z-icon-play");
        auth.addSubMenu(new MenuItem("Login", "#login.zul", null));
        auth.addSubMenu(new MenuItem("Register", "#register.zul", null));
        menuItems.add(auth);

        // Multi Level con subniveles
        MenuItem multiLevel = new MenuItem("Multi Level", null, null);
        MenuItem twoLinks = new MenuItem("Two Links", null, null);
        twoLinks.addSubMenu(new MenuItem("Link 1", "#link1.zul", null));
        twoLinks.addSubMenu(new MenuItem("Link 2", "#link2.zul", null));
        multiLevel.addSubMenu(twoLinks);
        menuItems.add(multiLevel);

        return menuItems;
    }


    public static String bookmarkUrl(String src){
        String bookmark = "#";
        return bookmark+HEXUtil.convertirATextoHexLargo(src);
    }

}
