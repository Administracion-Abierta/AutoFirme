package es.gob.afirma.standalone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/HDPIManager.class */
final class HDPIManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    HDPIManager() {
    }

    private static String executeWmicCommand() {
        String[] strArr = {"wmic", "csproduct", "get", "name"};
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(strArr).getInputStream()));
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    if (!line.equals("Name") && line.length() > 0) {
                        return line.trim();
                    }
                } else {
                    return null;
                }
            }
        } catch (IOException e) {
            LOGGER.warning("Error en la ejecucion del comando " + strArr.toString() + ": " + e);
            return null;
        }
    }

    public static boolean isHDPIDevice() {
        String strExecuteWmicCommand = executeWmicCommand();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(HDPIManager.class.getResourceAsStream("/lookandfeel/hdpi_devices")));
        try {
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                if (line.equals(strExecuteWmicCommand)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            LOGGER.warning("No se ha podido abrir el fichero de dispositivos HDPI: " + e);
            return false;
        }
    }
}
