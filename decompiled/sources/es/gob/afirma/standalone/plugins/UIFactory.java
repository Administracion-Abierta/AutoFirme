package es.gob.afirma.standalone.plugins;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.io.IOException;
import javax.swing.JDialog;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/UIFactory.class */
public class UIFactory {
    public static JDialog getWaitingDialog(Component component, String str, String str2) throws IOException {
        try {
            return (JDialog) Class.forName("es.gob.afirma.standalone.ui.CommonWaitDialog").getConstructor(Frame.class, String.class, String.class).newInstance(component, str, str2);
        } catch (Exception e) {
            throw new IOException("No se ha podido cargar el dialogo de espera", e);
        }
    }

    public static Image getDefaultDialogIcon() throws IOException {
        try {
            return (Image) Class.forName("es.gob.afirma.standalone.DesktopUtil").getMethod("getDefaultDialogsIcon", new Class[0]).invoke(null, new Object[0]);
        } catch (Exception e) {
            throw new IOException("No se ha podido cargar el icono para los dialogos", e);
        }
    }
}
