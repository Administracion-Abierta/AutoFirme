package es.gob.afirma.ui.core.jse.certificateselection;

import es.gob.afirma.core.keystores.NameCertificateBean;
import es.gob.afirma.core.misc.LoggerUtil;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/UtilActions.class */
final class UtilActions {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String HELP_URI;

    static {
        Properties properties = new Properties();
        try {
            properties.load(UtilActions.class.getResourceAsStream("/resources/selectiondialogutil.properties"));
        } catch (Exception e) {
            LOGGER.severe("No se han podido cargar las propiedades del dialogo de seleccion de certificados, se utilizaran los valores por defecto: " + e);
        }
        HELP_URI = properties.getProperty("helpUrl", "http://incidencias-ctt.administracionelectronica.gob.es/wiki/doku.php?id=forja-ctt_wiki:clienteafirma:adenda_-_uso_del_dialogo_grafico_de_seleccion_de_certificados");
    }

    private UtilActions() {
    }

    static void doHelp() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(HELP_URI));
                return;
            } catch (Exception e) {
                LOGGER.severe("No se ha podido abrir la pagina Web de ayuda: " + e);
                return;
            }
        }
        LOGGER.warning("No se soporta la apertura de paginas Web, por lo que no se puede abrir la ayuda");
    }

    static void doRefresh(CertificateSelectionDialog certificateSelectionDialog, Component component) {
        if (component != null) {
            component.setCursor(new Cursor(3));
        }
        certificateSelectionDialog.refreshKeystore();
        if (component != null) {
            component.setCursor(new Cursor(0));
        }
    }

    static void doChangeView(NameCertificateBean[] nameCertificateBeanArr, CertificateLineView certificateLineView, CertificateSelectionDialog certificateSelectionDialog, Component component) {
        if (component != null) {
            component.setCursor(new Cursor(3));
        }
        certificateSelectionDialog.refreshDialog(nameCertificateBeanArr, certificateLineView);
        if (component != null) {
            component.setCursor(new Cursor(0));
        }
    }

    static void doChangeKeyStore(int i, CertificateSelectionDialog certificateSelectionDialog, Component component, String str, String str2) {
        if (component != null) {
            component.setCursor(new Cursor(3));
        }
        LOGGER.info("Se cambia al almacen de certificados " + LoggerUtil.getTrimStr(str));
        certificateSelectionDialog.changeKeyStore(i, str, str2);
        if (component != null) {
            component.setCursor(new Cursor(0));
        }
    }
}
