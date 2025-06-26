package com.italoweb.gestorfinan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.BookmarkEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;
import org.zkoss.zul.Style;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.config.Apariencia;
import com.italoweb.gestorfinan.config.AparienciaManager;
import com.italoweb.gestorfinan.config.MenuManager;
import com.italoweb.gestorfinan.controller.LoginController;
import com.italoweb.gestorfinan.model.Roles;
import com.italoweb.gestorfinan.model.Usuario;
import com.italoweb.gestorfinan.navigation.EstadoMenu;
import com.italoweb.gestorfinan.navigation.MenuItem;
import com.italoweb.gestorfinan.navigation.NavigationMdel;
import com.italoweb.gestorfinan.util.AppProperties;
import com.italoweb.gestorfinan.util.HEXUtil;

public class Base extends Window implements AfterCompose {

    private static final long serialVersionUID = 7029570521929714394L;
    private Include include_main_root;
    private Div sidebarNav;
    private List<MenuItem> menuItems;
    private Label titulo;
    private A nav_toggle;
    private Div sidebar;
    private Image logo_app;
    private static final String LOGO_DIR = AppProperties.get("app.src").concat("/config/images");
    private static final String LOGO_FILENAME = "logo_app.jpg";

    @Override
    public void afterCompose() {
        ConventionWires.wireVariables(this, this);
        AparienciaManager manager = new AparienciaManager();
        List<Apariencia> list = manager.getApariencia();
        initMenu();
        buildSidebar();
        this.loadPages(NavigationMdel.INDEX_PAGE);
        this.addEventListener("onBookmarkChange", (evt) -> {
            BookmarkEvent event = (BookmarkEvent) evt;
            String newBookmark = event.getBookmark();
            loadBookmarkChange(newBookmark);
        });

        /*Styles*/
        String colorPrimario = "#3f8f71";

        boolean degradadoNavbar = true;
        String colorPrimarioDegr = "#41d59d";
        String colorSecundarioDegr = "#3f8f71";

        String colorNavbar = degradadoNavbar
                ? String.format("linear-gradient(-45deg, %s 0%%, %s 100%%)", colorPrimarioDegr, colorSecundarioDegr)
                : colorPrimario;

        if (list.size() > 0){
            colorPrimario = list.get(0).getColorPrimary();
            colorPrimarioDegr = list.get(0).getGradientStartNavbar();
            colorSecundarioDegr = list.get(0).getGradientEndNavbar();
            colorNavbar = degradadoNavbar
                    ? String.format("linear-gradient(-45deg, %s 0%%, %s 100%%)", colorPrimarioDegr, colorSecundarioDegr)
                    : colorPrimario;
        }

        StringBuilder css = new StringBuilder();
        css.append(":root {\n");
        css.append(String.format("    --color-navbar: %s !important;\n", colorNavbar));
        css.append(String.format("    --color-primario: %s;\n", colorPrimario));
        css.append("}\n\n");

        Style style = new Style();
        style.setContent(css.toString());
        this.appendChild(style);

        AparienciaManager aparienciaManager = new AparienciaManager();
        if (aparienciaManager.getApariencia() != null){
            if (aparienciaManager.getApariencia().size() > 0){
                String titulo = aparienciaManager.getApariencia().get(0).getName();
                this.titulo.setValue(titulo);
                String scriptTitlePage = String.format("document.title = '%s';", titulo);
                Clients.evalJavaScript(scriptTitlePage);
            }
        }

        this.nav_toggle.setIconSclass("fa fa-chevron-circle-left");
        this.nav_toggle.addEventListener(Events.ON_CLICK, event -> {
            boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
            String iconClass = nav_toggle.getIconSclass();
            if (iconClass.contains("fa-chevron-circle-left")) {
                nav_toggle.setIconSclass("z-icon-bars");
                sidebar.setClass("sidebar z-div collapsed");
                if (esMovil){
                    titulo.setVisible(true);
                }else{
                    titulo.setVisible(true);
                }
            } else {
                nav_toggle.setIconSclass("fa fa-chevron-circle-left");
                sidebar.setClass("sidebar z-div");
                if (esMovil){
                    titulo.setVisible(false);
                }else{
                    titulo.setVisible(true);
                }
            }
        });

        boolean esMovil = Executions.getCurrent().getBrowser("mobile") != null;
        if (esMovil){
            titulo.setVisible(false);
        }else {
            titulo.setVisible(true);
        }
        
        this.cargarLogoActual();
    }

    public void loadPages(String src) {
        if (this.include_main_root.getChildPage() != null) {
            this.include_main_root.getChildPage().removeComponents();
        }

        Usuario user = this.session();
        if (user == null){
            src = NavigationMdel.LOGIN_ZUL;
            Executions.sendRedirect(src);
            return; // <-- Agrega return aquí para evitar continuar
        }

        if (!pageExists(src)) {
            src = NavigationMdel.BLANK_ZUL;
            this.include_main_root.setSrc(null);
        }
        this.include_main_root.setSrc(src);
    }

    private Usuario session(){
        Session session = Sessions.getCurrent();
        Object usuario = session.getAttribute("usuario");
        return (Usuario) usuario;
    }

    public void salir(){
        LoginController.logout();
    }

    private boolean pageExists(String src) {
        System.out.println(src);
        try (InputStream is = Executions.getCurrent().getDesktop().getWebApp().getResourceAsStream(src)) {
            return is != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void loadBookmarkChange(String bookmarkID) throws Exception {
        String srcBase = "/views";
        String desencriptado = HEXUtil.convertirAOriginalDesdeHexLargo(bookmarkID);

        for (Component comp : sidebarNav.getChildren()) {
            if (comp instanceof Div) {
                for (Component child : comp.getChildren()) {
                    if (child instanceof A) {
                        A link = (A) child;
                        if (link.getHref() != null){
                            String href = link.getHref().replace("#", ""); // Quitar el #
                            if (href.equals(bookmarkID)) {
                                link.setSclass("sidebar-link active"); // Marcar activo
                            } else {
                                link.setSclass("sidebar-link"); // Quitar active de otros
                            }
                        }

                    }
                }
            }
        }

        loadPages(srcBase.concat(desencriptado));
    }

    //Inicializa los elementos del menú
    private void initMenu() {
        //Obtener MenuJSON/
        MenuManager manager = new MenuManager();
        //MenuModel menuModel = new MenuModel();
        //manager.saveMenu(menuModel.cargarMenu());

        this.menuItems = new ArrayList<>();
        this.menuItems = manager.getMenu();
        this.menuItems = this.filterActiveMenuItems(this.menuItems);
        //int size = this.menuItems.size();
    }
    
    private List<MenuItem> filterActiveMenuItems(List<MenuItem> items) {
        List<MenuItem> activeItems = new ArrayList<>();
        for (MenuItem item : items) {
            if (item.getStatus() != EstadoMenu.INACTIVO) {
            	if (this.session() != null) {
                	Roles rolSession = this.session().getRoles();
                    if (item.getRoles() == null || item.getRoles().size() < 1 ||
                        item.getRoles().stream().anyMatch(r -> r.getId().equals(rolSession.getId()))) {
                        // Si item.getRoles() es null o contiene el rol, entra aquí
                        item.setSubMenu(filterActiveMenuItems(item.getSubMenu())); //
                        activeItems.add(item);
                    }
                    
				}
            }
        }
        return activeItems;
    }

    // Construye el menú dinámicamente en la vista
    private void buildSidebar() {
        sidebarNav.appendChild(createSidebarHeader("Menu"));

        for (MenuItem item : menuItems) {
            if (item.getSubMenu().isEmpty()) {
                sidebarNav.appendChild(createSidebarItem(item));
            } else {
                sidebarNav.appendChild(createSidebarWithDropdown(item));
            }
        }
    }

    private Div createSidebarHeader(String title) {
        Div header = new Div();
        header.setSclass("sidebar-header");
        header.appendChild(new Label(title));
        return header;
    }

    private Div createSidebarItem(MenuItem item) {
        Div div = new Div();
        div.setSclass("sidebar-item");

        A link = new A();
        link.setHref(item.getHref());
        link.setSclass("sidebar-link");
        link.setStyle("padding: 10px;");
        if (item.getIconClass() != null) {
            link.setIconSclass(item.getIconClass());
        }
        link.setLabel(item.getTitle());

        div.appendChild(link);
        return div;
    }

    private Div createSidebarWithDropdown(MenuItem item) {
        // Contenedor principal del menú desplegable
        Div div = new Div();
        div.setSclass("sidebar-item sidebar-parent");

        // Enlace principal (padre del dropdown)
        A link = new A();
        link.setSclass("sidebar-link sidebar-toggle");
        link.setStyle("padding: 10px;");
        if (item.getIconClass() != null) {
            link.setIconSclass(item.getIconClass());
        }
        link.setLabel(item.getTitle());

        Span caret = new Span();
        caret.setSclass("submenu-caret fa fa-chevron-down");
        caret.setId("caret-" + item.getId()); // Asigna un ID único si lo necesitas
        link.appendChild(caret);

        link.addEventListener(Events.ON_CLICK, event -> {
            // Encuentra el ícono dentro del link
            Span caretIcon = (Span) link.getFellowIfAny("caret-" + item.getId());

            if (caretIcon != null) {
                String currentClass = caretIcon.getSclass();
                if (currentClass.contains("chevron-down")) {
                    caretIcon.setSclass("submenu-caret fa fa-chevron-up");
                } else {
                    caretIcon.setSclass("submenu-caret fa fa-chevron-down");
                }
            }
        });



        div.appendChild(link);

        // Contenedor del dropdown
        Div dropdown = new Div();
        dropdown.setSclass("sidebar-dropdown list-unstyled collapse sidebar-pages");

        for (MenuItem subItem : item.getSubMenu()) {
            if (subItem.getSubMenu().isEmpty()) {
                dropdown.appendChild(createSidebarItem(subItem));
            } else {
                dropdown.appendChild(createSidebarWithDropdown(subItem));
            }
        }

        div.appendChild(dropdown);
        return div;
    }
    
    private void mostrarPreview(byte[] data) {
        String base64 = "data:image/jpeg;base64," +
                Base64.getEncoder().encodeToString(data);
        AparienciaManager aparienciaManager = new AparienciaManager();
        List<Apariencia> apariencia = aparienciaManager.getApariencia();
        String widthLogo = "100px";
        if (apariencia.size() > 0) {
        	int size = apariencia.get(0).getSizeLogo();
			if (Objects.nonNull(size)) {
				widthLogo = size+"px";
			}
		}
        this.logo_app.setSrc(base64);
        this.logo_app.setWidth(widthLogo);
    }

    private void cargarLogoActual() {
        File file = new File(LOGO_DIR, LOGO_FILENAME);
        if (file.exists()) {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                mostrarPreview(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
