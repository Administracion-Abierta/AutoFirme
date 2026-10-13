package es.gob.afirma.massive;

import java.io.IOException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/massive/LogHandler.class */
public abstract class LogHandler {
    public static final int LEVEL_INFO = 800;
    public static final int LEVEL_WARNING = 900;
    public static final int LEVEL_SEVERE = 1000;

    public abstract void close(Properties properties) throws IOException;

    public abstract void addLog(int i, String str, String str2, String str3) throws IOException;
}
