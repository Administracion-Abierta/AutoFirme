package es.gob.afirma.standalone.configurator;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/ConsoleManager.class */
final class ConsoleManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final boolean headless = getHeadLess();
    private static final java.io.Console con = System.console();

    ConsoleManager() {
    }

    private static boolean getHeadLess() {
        if (GraphicsEnvironment.isHeadless() || Platform.OS.LINUX.equals(Platform.getOS())) {
            return true;
        }
        return false;
    }

    static Console getConsole(ConsoleListener consoleListener) {
        if (headless || consoleListener == null) {
            if (con != null) {
                LOGGER.info("Se utilizara la consola de tipo I/O");
                return new IoConsole(con);
            }
            LOGGER.info("Se utilizara la consola del sistema");
            return new PrintConsole();
        }
        LOGGER.info("Se utilizara la consola grafica");
        return new GraphicConfiguratorConsole(consoleListener);
    }

    static void showErrorMessage(String str, Throwable th) {
        if (!headless) {
            AOUIFactory.showErrorMessage(str, Messages.getString("AutofirmaConfigurator.2"), 0, th);
        } else if (con != null) {
            con.printf(str, new Object[0]);
        } else {
            System.out.println(str);
        }
    }
}
