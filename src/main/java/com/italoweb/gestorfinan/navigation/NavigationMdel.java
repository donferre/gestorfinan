package com.italoweb.gestorfinan.navigation;

public class NavigationMdel {
    public static final String INDEX_PAGE = "/views/dashboard.zul";
    public static final String BLANK_ZUL = "/blank.zul";
    public static final String LOGIN_ZUL = "/login.zul";

    private String contentUrl = INDEX_PAGE;

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
