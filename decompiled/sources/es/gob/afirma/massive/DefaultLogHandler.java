package es.gob.afirma.massive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/massive/DefaultLogHandler.class */
final class DefaultLogHandler extends LogHandler {
    private final OutputStream os;

    DefaultLogHandler(OutputStream outputStream) {
        this.os = outputStream;
    }

    @Override // es.gob.afirma.massive.LogHandler
    public void addLog(int i, String str, String str2, String str3) {
        String str4;
        try {
            OutputStream outputStream = this.os;
            StringBuilder sbAppend = new StringBuilder().append("\r\n").append(getLevel(i)).append(": ").append(str).append(" - ");
            if (str3 != null) {
                str4 = str3;
            } else {
                str4 = str2 != null ? str2 : "";
            }
            outputStream.write(sbAppend.append(str4).toString().getBytes());
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("No se pudo insertar una entrada en el log de la operacion masiva: " + e);
        }
    }

    @Override // es.gob.afirma.massive.LogHandler
    public void close(Properties properties) throws IOException {
        String property = properties != null ? properties.getProperty("warningsCount", "0") : "0";
        String property2 = properties != null ? properties.getProperty("errorsCount", "0") : "0";
        this.os.write(("\r\n\r\n" + MassiveSignMessages.getString("DirectorySignatureHelper.25") + ": " + property).getBytes());
        this.os.write(("\r\n" + MassiveSignMessages.getString("DirectorySignatureHelper.26") + ": " + property2).getBytes());
    }

    private static String getLevel(int i) {
        if (i == Level.INFO.intValue()) {
            return Level.INFO.getName();
        }
        if (i == Level.WARNING.intValue()) {
            return Level.WARNING.getName();
        }
        return Level.SEVERE.getName();
    }
}
