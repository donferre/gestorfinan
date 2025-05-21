package com.italoweb.gestorfinan.util;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

public class DialogUtil implements Serializable {

    private static final long serialVersionUID = 362052517592380092L;

	public static void showInformation(String message) {
        if (!validateDesktop()) {
            return;
        }
        try {
            Clients.clearBusy();
            Messagebox.show(message, "", Messagebox.OK, Messagebox.INFORMATION);
        } catch (UiException e) {
            //IKernel.getInfodiggLogger(null).info("Error showing INFO message to disconnected client:" + message);
        }
    }

    public static boolean validateDesktop() {
        Execution execution = Executions.getCurrent();
        if (execution==null) {
            return false;
        }
        Desktop desktop = execution.getDesktop();
        if (desktop==null) {
            return false;
        }
        return desktop.getWebApp() != null && desktop.getFirstPage() != null;
    }

    public static void showWarning(String message) {
        if (!validateDesktop()) {
            return;
        }
        try {
            Clients.clearBusy();
            Messagebox.show(message, "Atención", Messagebox.OK, Messagebox.EXCLAMATION);
        } catch (UiException e) {
            //IKernel.getInfodiggLogger(null).warn("Error showing WARN message to disconnected client:" + message);
        }
    }

    public static void showError(String message) {
        if (!validateDesktop()) {
            return;
        }
        try {
            Clients.clearBusy();
            Messagebox.show(message, "Upps!!", Messagebox.OK, Messagebox.ERROR);
        } catch (UiException e) {
            //IKernel.getInfodiggLogger(null).warn("Error showing ERR message to disconnected client:" + message);
        }
    }

    public static CompletableFuture<Boolean> showConfirmDialog(String message, String title) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Messagebox.show(message, title,
                Messagebox.YES | Messagebox.NO,
                Messagebox.QUESTION,
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) {
                        future.complete(Messagebox.ON_YES.equals(event.getName()));
                    }
                });

        return future;
    }

    public static boolean showQuestion(String value) {
        if (!validateDesktop()) {
            return false;
        }
        try {
            Clients.clearBusy();
            int answer = Messagebox.show(value, "Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION);
            return answer == Messagebox.YES;
        } catch (UiException e) {
            //IKernel.getInfodiggLogger(null).warn("Error showing QUESTION message to disconnected client:" + value);
            return false;
        }
    }

    public static void showShortMessage(String icon, String msg) {
        String scriptJS = String.format(
                "if (typeof Toast === 'undefined') {\n" +
                        "  var Toast = Swal.mixin({\n" +
                        "    toast: true,\n" +
                        "    position: \"top-end\",\n" +
                        "    showConfirmButton: false,\n" +
                        "    timer: 3000,\n" +
                        "    timerProgressBar: true,\n" +
                        "    didOpen: (toast) => {\n" +
                        "      toast.onmouseenter = Swal.stopTimer;\n" +
                        "      toast.onmouseleave = Swal.resumeTimer;\n" +
                        "    }\n" +
                        "  });\n" +
                        "}\n" +
                        "setTimeout(() => {\n" +  // Asegura que el DOM ya esté cargado
                        "  document.querySelectorAll('.swal2-container').forEach(el => el.style.zIndex = '9999');\n" +
                        "}, 100);\n" +
                        "Toast.fire({\n" +
                        "  icon: \"%s\",\n" +
                        "  title: \"%s\"\n" +
                        "});", icon, msg);

        Clients.evalJavaScript(scriptJS);
    }

}
