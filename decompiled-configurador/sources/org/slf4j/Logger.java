package org.slf4j;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/slf4j/Logger.class */
public interface Logger {
    public static final String ROOT_LOGGER_NAME = "ROOT";

    String getName();

    boolean isTraceEnabled();

    void trace(String str);

    void trace(String str, Object obj);

    void trace(String str, Object obj, Object obj2);

    void trace(String str, Object... objArr);

    void trace(String str, Throwable th);

    boolean isTraceEnabled(Marker marker);

    void trace(Marker marker, String str);

    void trace(Marker marker, String str, Object obj);

    void trace(Marker marker, String str, Object obj, Object obj2);

    void trace(Marker marker, String str, Object... objArr);

    void trace(Marker marker, String str, Throwable th);

    boolean isDebugEnabled();

    void debug(String str);

    void debug(String str, Object obj);

    void debug(String str, Object obj, Object obj2);

    void debug(String str, Object... objArr);

    void debug(String str, Throwable th);

    boolean isDebugEnabled(Marker marker);

    void debug(Marker marker, String str);

    void debug(Marker marker, String str, Object obj);

    void debug(Marker marker, String str, Object obj, Object obj2);

    void debug(Marker marker, String str, Object... objArr);

    void debug(Marker marker, String str, Throwable th);

    boolean isInfoEnabled();

    void info(String str);

    void info(String str, Object obj);

    void info(String str, Object obj, Object obj2);

    void info(String str, Object... objArr);

    void info(String str, Throwable th);

    boolean isInfoEnabled(Marker marker);

    void info(Marker marker, String str);

    void info(Marker marker, String str, Object obj);

    void info(Marker marker, String str, Object obj, Object obj2);

    void info(Marker marker, String str, Object... objArr);

    void info(Marker marker, String str, Throwable th);

    boolean isWarnEnabled();

    void warn(String str);

    void warn(String str, Object obj);

    void warn(String str, Object... objArr);

    void warn(String str, Object obj, Object obj2);

    void warn(String str, Throwable th);

    boolean isWarnEnabled(Marker marker);

    void warn(Marker marker, String str);

    void warn(Marker marker, String str, Object obj);

    void warn(Marker marker, String str, Object obj, Object obj2);

    void warn(Marker marker, String str, Object... objArr);

    void warn(Marker marker, String str, Throwable th);

    boolean isErrorEnabled();

    void error(String str);

    void error(String str, Object obj);

    void error(String str, Object obj, Object obj2);

    void error(String str, Object... objArr);

    void error(String str, Throwable th);

    boolean isErrorEnabled(Marker marker);

    void error(Marker marker, String str);

    void error(Marker marker, String str, Object obj);

    void error(Marker marker, String str, Object obj, Object obj2);

    void error(Marker marker, String str, Object... objArr);

    void error(Marker marker, String str, Throwable th);
}
