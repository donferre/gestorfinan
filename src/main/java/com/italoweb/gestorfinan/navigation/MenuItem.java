package com.italoweb.gestorfinan.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuItem {
    private String id;
    private String title;
    private String href;
    private String iconClass;
    private List<MenuItem> subMenu;
    private int order;
    private EstadoMenu status;

    public MenuItem() {
    }

    public MenuItem(String title, String href, String iconClass) {
        this.id = "MENU_" + UUID.randomUUID();
        this.title = title;
        this.href = href;
        this.iconClass = iconClass;
        this.subMenu = new ArrayList<>();
    }

    public MenuItem(String title, String href, String iconClass, int order) {
        this.id = "MENU_" + UUID.randomUUID();
        this.title = title;
        this.href = href;
        this.iconClass = iconClass;
        this.subMenu = new ArrayList<>();
        this.order = order;
    }

    public void addSubMenu(MenuItem item) {
        this.subMenu.add(item);
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }

    public String getIconClass() {
        return iconClass;
    }

    public List<MenuItem> getSubMenu() {
        return subMenu;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public void setSubMenu(List<MenuItem> subMenu) {
        this.subMenu = subMenu;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

	public EstadoMenu getStatus() {
		return status;
	}

	public void setStatus(EstadoMenu status) {
		this.status = status;
	}
}

