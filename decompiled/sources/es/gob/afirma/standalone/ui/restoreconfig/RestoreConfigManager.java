package es.gob.afirma.standalone.ui.restoreconfig;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/RestoreConfigManager.class */
public class RestoreConfigManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private RestoreConfig configurator;

    public RestoreConfigManager() {
        if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
            this.configurator = new RestoreConfigWindows();
            return;
        }
        if (Platform.OS.LINUX == Platform.getOS()) {
            this.configurator = new RestoreConfigLinux();
        } else if (Platform.OS.MACOSX == Platform.getOS()) {
            this.configurator = new RestoreConfigMacOSX();
        } else {
            LOGGER.warning("El sistema operativo '" + Platform.getOS() + "' no tiene definida una secuencia de configuracion/desinstalacion");
            this.configurator = null;
        }
    }

    public void restoreConfig(RestoreConfigPanel restoreConfigPanel) {
        if (this.configurator == null) {
            LOGGER.warning("No se realizara ninguna accion");
            return;
        }
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreApplication.13"));
        LOGGER.info("Inicio de la restauracion de la instalacion");
        this.configurator.restore(restoreConfigPanel);
        restoreConfigPanel.appendMessage(SimpleAfirmaMessages.getString("RestoreApplication.14"));
        LOGGER.info("Fin de la restauracion de la instalacion");
    }
}
