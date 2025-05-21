package com.italoweb.gestorfinan;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;
import org.zkoss.zul.Messagebox;

import com.italoweb.gestorfinan.util.MD5Validator;

public class Init implements WebAppInit {

    @Override
    public void init(WebApp wapp) throws Exception {
        System.out.println("INICIANDO APLICACIÓN");
        Messagebox.setTemplate("components/messagebox.zul");
        generarMd5();
    }

    public void generarMd5(){
        String clave = "12345";
        String claveEncriptada = MD5Validator.encryptToMD5(clave);
        System.out.println(claveEncriptada);
    }
}
