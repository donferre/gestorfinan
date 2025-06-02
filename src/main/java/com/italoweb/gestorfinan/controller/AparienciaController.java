package com.italoweb.gestorfinan.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.italoweb.gestorfinan.config.Apariencia;
import com.italoweb.gestorfinan.config.AparienciaManager;
import com.italoweb.gestorfinan.util.ComponentsUtil;
import com.italoweb.gestorfinan.util.DialogUtil;

public class AparienciaController extends Window implements AfterCompose {

    private static final long serialVersionUID = 5203021735260439522L;
	private Textbox colorPrimary, gradientStart, gradientEnd, name, description, logo;
    private AparienciaManager manager;
    private Image logoPreview;
    private Fileupload fileUploadLogo;

    // Directorio en 'webapp' para los archivos accesibles
    private static final String LOGO_DIR = System.getProperty("user.home") + "/gestorfinan/config/images";
    private static final String LOGO_FILENAME = "logo_app.jpg";

    @Override
    public void afterCompose() {
        this.manager = new AparienciaManager();
        ComponentsUtil.connectVariablesController(this);
        this.cargarComponentes();
        this.cargarApariencia();
        this.cargarLogoActual();
    }

    public void cargarComponentes(){
        colorPrimary.addEventListener(Events.ON_CHANGE, event -> {
            Apariencia apariencia = new Apariencia();
            List<Apariencia> listapariencia = this.manager.getApariencia();
            apariencia = listapariencia.get(0);
            apariencia.setColorPrimary(this.colorPrimary.getValue());

            if (listapariencia.size() > 0){
                listapariencia.set(0 , apariencia);
            }else {
                listapariencia.add(apariencia);
            }
            this.manager.saveApariencia(listapariencia);
        });

        gradientStart.addEventListener(Events.ON_CHANGE, event -> {
            List<Apariencia> listapariencia = this.manager.getApariencia();
            Apariencia apariencia = new Apariencia();
            apariencia = listapariencia.get(0);
            apariencia.setGradientStartNavbar(this.gradientStart.getValue());

            if (listapariencia.size() > 0){
                listapariencia.set(0 , apariencia);
            }else {
                listapariencia.add(apariencia);
            }
            this.manager.saveApariencia(listapariencia);
        });

        gradientEnd.addEventListener(Events.ON_CHANGE, event -> {
            List<Apariencia> listapariencia = this.manager.getApariencia();
            Apariencia apariencia = new Apariencia();
            apariencia = listapariencia.get(0);
            apariencia.setGradientEndNavbar(this.gradientEnd.getValue());
            if (listapariencia.size() > 0){
                listapariencia.set(0 , apariencia);
            }else {
                listapariencia.add(apariencia);
            }

            this.manager.saveApariencia(listapariencia);
        });

        // Manejar evento de subida
        this.fileUploadLogo.addEventListener(Events.ON_UPLOAD, event -> {
            UploadEvent uploadEvent = (UploadEvent) event;
            Media media = uploadEvent.getMedia();

            if (media != null && media.isBinary() && media.getContentType().startsWith("image")) {
                try {
                    guardarLogoEnDisco(media.getByteData());
                    mostrarPreview(media.getByteData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Archivo no válido o no es imagen.");
            }
        });
    }

    public void cargarApariencia(){
        List<Apariencia> apariencia = this.manager.getApariencia();
        if (apariencia != null)
            if (apariencia.size() > 0){
                this.colorPrimary.setValue(apariencia.get(0).getColorPrimary());
                this.gradientStart.setValue(apariencia.get(0).getGradientStartNavbar());
                this.gradientEnd.setValue(apariencia.get(0).getGradientEndNavbar());
                this.name.setValue(apariencia.get(0).getName());
                this.description.setValue(apariencia.get(0).getDescription());
                this.logo.setValue(apariencia.get(0).getLogo());
            }
    }

    public void onUploadLogo(UploadEvent event) {
        Media media = event.getMedia();
        if (media != null && media.isBinary() && media.getContentType().startsWith("image")) {
            String base64 = "data:" + media.getContentType() + ";base64," +
                    Base64.getEncoder().encodeToString(media.getByteData());

            logoPreview.setSrc(base64); // Muestra la imagen cargada como preview
        } else {
            // Aquí podrías mostrar un mensaje de error si no es imagen válida
        }
    }

    public void guardarCambios(){
        String name = this.name.getValue().trim();
        String desc = this.description.getValue().trim();
        String logo = this.logo.getValue().trim();

        if (StringUtils.isBlank(name)) {
            DialogUtil.showError("El campo nombre es obligatorio");
            return;
        }
        if (StringUtils.isBlank(desc)) {
            DialogUtil.showError("El campo descripción es obligatorio");
            return;
        }

        List<Apariencia> listapariencia = this.manager.getApariencia();
        Apariencia apariencia = new Apariencia();
        apariencia = listapariencia.get(0);
        apariencia.setName(name);
        apariencia.setDescription(desc);
        apariencia.setLogo(logo);
        if (listapariencia.size() > 0){
            listapariencia.set(0 , apariencia);
        }else {
            listapariencia.add(apariencia);
        }
        manager.saveApariencia(listapariencia);
        DialogUtil.showShortMessage("success", "Configuracion Guardada exitosamente");
    }

    private void guardarLogoEnDisco(byte[] data) throws IOException {
        File dir = new File(LOGO_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, LOGO_FILENAME);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }

    private void mostrarPreview(byte[] data) {
        String base64 = "data:image/jpeg;base64," +
                Base64.getEncoder().encodeToString(data);
        logoPreview.setSrc(base64);
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
