package es.gob.jmulticard;

import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/JmcLogger.class */
public final class JmcLogger {
    private static final Logger LOGGER = Logger.getLogger("es.gob.jmulticard");
    private static final boolean DEBUG = false;

    private JmcLogger() {
    }

    public static void warning(String msg) {
        LOGGER.warning(msg);
    }

    public static void severe(String msg) {
        LOGGER.severe(msg);
    }

    public static void info(String className, String methodName, String msg) {
        LOGGER.info(className + "." + methodName + "() : " + msg);
    }

    public static void debug(String className, String methodName, String msg) {
    }

    public static void log(String className, String methodName, Level level, String msg, Throwable e) {
        LOGGER.log(level, className + "." + methodName + "() : " + msg, e);
    }
}
