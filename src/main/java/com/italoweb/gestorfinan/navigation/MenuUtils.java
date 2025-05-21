package com.italoweb.gestorfinan.navigation;

import java.util.Iterator;
import java.util.List;

public class MenuUtils {

    // Buscar un MenuItem por ID
    public static MenuItem findById(List<MenuItem> menuItems, String id) {
        for (MenuItem item : menuItems) {
            if (item.getId().equals(id)) {
                return item;
            }
            MenuItem found = findById(item.getSubMenu(), id);
            if (found != null) return found;
        }
        return null;
    }

    // Eliminar un MenuItem por ID (en cualquier nivel)
    public static boolean deleteById(List<MenuItem> menuItems, String id) {
        Iterator<MenuItem> iterator = menuItems.iterator();
        while (iterator.hasNext()) {
            MenuItem item = iterator.next();
            if (item.getId().equals(id)) {
                iterator.remove();
                return true;
            }
            if (deleteById(item.getSubMenu(), id)) {
                return true;
            }
        }
        return false;
    }

    // Actualizar un MenuItem por ID
    public static boolean updateItemById(List<MenuItem> items, String id, MenuItem updatedItem) {
        for (int i = 0; i < items.size(); i++) {
            MenuItem current = items.get(i);
            if (current.getId().equals(id)) {
                items.set(i, updatedItem); // reemplaza el item entero
                return true;
            }
            if (current.getSubMenu() != null && !current.getSubMenu().isEmpty()) {
                boolean updated = updateItemById(current.getSubMenu(), id, updatedItem);
                if (updated) return true;
            }
        }
        return false;
    }

    // Agregar submenú a un MenuItem por ID
    public static boolean addSubMenuTo(List<MenuItem> menuItems, String parentId, MenuItem newSubMenu) {
        MenuItem parent = findById(menuItems, parentId);
        if (parent != null) {
            parent.addSubMenu(newSubMenu);
            return true;
        }
        return false;
    }
}
