package com.italoweb.gestorfinan.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.italoweb.gestorfinan.navigation.MenuItem;
import com.italoweb.gestorfinan.navigation.MenuUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MenuManager extends JsonManager<List<MenuItem>> {

    public MenuManager() {
        super("data/menu.json", new TypeReference<List<MenuItem>>() {});
    }

    public List<MenuItem> getMenu() {
        return this.get();
    }

    public void saveMenu(List<MenuItem> menuItems) {
        this.save(menuItems);
    }

    // 🔍 Buscar por ID usando búsqueda recursiva
    public MenuItem getById(String id) {
        return MenuUtils.findById(getMenu(), id);
    }

    // 🗑️ Eliminar por ID en cualquier nivel
    public boolean deleteById(String id) {
        List<MenuItem> menu = getMenu();
        boolean removed = MenuUtils.deleteById(menu, id);
        if (removed) {
            save(menu);
        }
        return removed;
    }

    // ✏️ Actualizar un menú existente
    public boolean updateById(String id, MenuItem updatedItem) {
        List<MenuItem> menu = getMenu();
        boolean updated = MenuUtils.updateItemById(menu, id, updatedItem);
        if (updated) {
            save(menu);
        }
        return updated;
    }

    // ➕ Crear (solo si no existe)
    public boolean create(MenuItem newItem) {
        if (getById(newItem.getId()) != null) {
            return false; // ya existe
        }
        List<MenuItem> menu = getMenu();
        menu.add(newItem);
        save(menu);
        return true;
    }

    // ➕ Agregar submenú a un ítem existente
    public boolean addSubMenu(String parentId, MenuItem newSubItem) {
        List<MenuItem> menu = getMenu();
        boolean added = MenuUtils.addSubMenuTo(menu, parentId, newSubItem);
        if (added) {
            save(menu);
        }
        return added;
    }

    public int getNextOrdenRaiz() {
        List<MenuItem> menu = getMenu();
        return menu.stream()
                .mapToInt(MenuItem::getOrder)
                .max()
                .orElse(0) + 1;
    }

    public int getNextOrdenHijo(String parentId) {
        List<MenuItem> menu = getMenu(); // Asumo que este devuelve todo el menú raíz

        Optional<MenuItem> parentItem = findMenuItemById(menu, parentId);
        if (parentItem.isPresent()) {
            List<MenuItem> subMenu = parentItem.get().getSubMenu();
            return subMenu.stream()
                    .mapToInt(MenuItem::getOrder)
                    .max()
                    .orElse(0) + 1;
        }

        return 1; // Si no se encuentra el padre, se retorna 1 por defecto
    }

    private Optional<MenuItem> findMenuItemById(List<MenuItem> items, String id) {
        for (MenuItem item : items) {
            if (item.getId().equals(id)) {
                return Optional.of(item);
            } else if (item.getSubMenu() != null && !item.getSubMenu().isEmpty()) {
                Optional<MenuItem> found = findMenuItemById(item.getSubMenu(), id);
                if (found.isPresent()) {
                    return found;
                }
            }
        }
        return Optional.empty();
    }

    public boolean moverArriba(String id) {
        List<MenuItem> menu = getMenu();
        boolean movido = moverArribaRecursivo(menu, id);
        if (movido) {
            save(menu);
        }
        return movido;
    }

    private boolean moverArribaRecursivo(List<MenuItem> items, String id) {
        // Ordenar primero para que el orden sea confiable
        items.sort(Comparator.comparingInt(MenuItem::getOrder));

        for (int i = 0; i < items.size(); i++) {
            MenuItem current = items.get(i);
            if (current.getId().equals(id)) {
                if (i > 0) {
                    // Intercambiamos orden con el anterior
                    MenuItem anterior = items.get(i - 1);
                    int tmpOrden = current.getOrder();
                    current.setOrder(anterior.getOrder());
                    anterior.setOrder(tmpOrden);

                    // Reordenamos la lista
                    items.sort(Comparator.comparingInt(MenuItem::getOrder));
                    return true;
                }
                return false; // Ya está al inicio
            }

            // Recursivamente en submenús
            if (current.getSubMenu() != null && !current.getSubMenu().isEmpty()) {
                boolean subResult = moverArribaRecursivo(current.getSubMenu(), id);
                if (subResult) return true;
            }
        }
        return false; // No encontrado
    }

    public boolean moverAbajo(String id) {
        List<MenuItem> menu = getMenu();
        boolean movido = moverAbajoRecursivo(menu, id);
        if (movido) {
            save(menu);
        }
        return movido;
    }

    private boolean moverAbajoRecursivo(List<MenuItem> items, String id) {
        // Ordenar primero por orden para garantizar el orden correcto
        items.sort(Comparator.comparingInt(MenuItem::getOrder));

        for (int i = 0; i < items.size(); i++) {
            MenuItem current = items.get(i);
            if (current.getId().equals(id)) {
                if (i < items.size() - 1) {
                    // Intercambiamos orden con el siguiente
                    MenuItem siguiente = items.get(i + 1);
                    int tmpOrden = current.getOrder();
                    current.setOrder(siguiente.getOrder());
                    siguiente.setOrder(tmpOrden);

                    // Reordenamos
                    items.sort(Comparator.comparingInt(MenuItem::getOrder));
                    return true;
                }
                return false; // Ya está al final
            }

            // Recursivo en submenú
            if (current.getSubMenu() != null && !current.getSubMenu().isEmpty()) {
                boolean subResult = moverAbajoRecursivo(current.getSubMenu(), id);
                if (subResult) return true;
            }
        }
        return false; // No encontrado
    }
}