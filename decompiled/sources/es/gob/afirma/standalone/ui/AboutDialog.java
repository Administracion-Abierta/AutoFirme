package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/AboutDialog.class */
public class AboutDialog {
    public static void showAbout(Component component) {
        AOUIFactory.showMessageDialog(component, SimpleAfirmaMessages.getString("MainMenu.14", SimpleAfirma.getVersion(), System.getProperty("java.version"), Platform.getJavaArch()), SimpleAfirmaMessages.getString("MainMenu.15"), -1);
    }
}
