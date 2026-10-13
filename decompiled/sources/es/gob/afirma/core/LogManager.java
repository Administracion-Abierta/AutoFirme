package es.gob.afirma.core;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.Platform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/LogManager.class */
public final class LogManager {
    public static final String SUBDIR = ".afirma";
    private static final String LOG_FILE_NAME = "%a.afirma.log.xml";
    private static final String LOG_FILE_PATH = "%h/.afirma";
    private static final int LOG_MAX_SIZE = 2097152;
    private static final Logger LOGGER = Logger.getLogger("es.gob");
    private static boolean installed = false;
    private static String logFile = null;
    private static App application = App.OTHER;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/LogManager$App.class */
    public enum App {
        MINIAPPLET,
        APPLET,
        AUTOFIRMA,
        AUTOFIRMA_CONFIGURATOR,
        STANDALONE,
        ANDROID,
        HANDWRITTEN,
        DNINB,
        OTHER
    }

    private LogManager() {
    }

    public static void install(App app) throws IOException {
        install(app, null);
    }

    public static void install(App app, String str) throws IOException {
        if (app == null) {
            application = App.OTHER;
        } else {
            application = app;
        }
        if (str == null) {
            logFile = LOG_FILE_PATH.replace("%h", Platform.getUserHome()) + "/" + LOG_FILE_NAME.replace("%a", application.toString());
        } else {
            logFile = (str.replace("\\", "/") + (str.endsWith("/") ? "" : "/") + application + ".afirma.log.xml").replace("%h", Platform.getUserHome());
        }
        File parentFile = new File(logFile).getParentFile();
        if (!parentFile.exists()) {
            LOGGER.info("La ruta para el fichero de registro ('" + LoggerUtil.getCleanUserHomePath(parentFile.getAbsolutePath()) + "') no existe, se creara");
            if (!parentFile.mkdirs()) {
                LOGGER.severe("No se ha podido crear la ruta para el fichero de registro ('" + LoggerUtil.getCleanUserHomePath(parentFile.getAbsolutePath()) + "')");
            }
        }
        LOGGER.addHandler(createFileHandler(logFile));
        installed = true;
    }

    private static FileHandler createFileHandler(String str) throws IOException {
        return new FileHandler(str, 2097152, 1, false);
    }

    public static String getLogFile() throws IOException {
        if (!installed || logFile == null) {
            throw new IOException("No esta instalado el manejador de fichero");
        }
        for (Handler handler : LOGGER.getHandlers()) {
            if (handler instanceof FileHandler) {
                handler.close();
                LOGGER.info("Cerrado el manejador de fichero para permitir que sea procesado");
                LOGGER.removeHandler(handler);
            }
        }
        FileInputStream fileInputStream = new FileInputStream(new File(logFile.replace("%h", Platform.getUserHome())));
        Throwable th = null;
        try {
            try {
                String str = new String(AOUtil.getDataFromInputStream(fileInputStream));
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                LOGGER.addHandler(createFileHandler(logFile));
                return str;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (fileInputStream != null) {
                if (th != null) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th4;
        }
    }
}
