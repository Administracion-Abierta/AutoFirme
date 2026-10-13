package es.gob.afirma.core;

import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/RuntimePasswordNeededException.class */
public abstract class RuntimePasswordNeededException extends RuntimeConfigNeededException {
    private static final long serialVersionUID = 8852303435406903975L;

    public abstract void configure(Properties properties, char[] cArr);

    public RuntimePasswordNeededException(String str, String str2) {
        super(str, RuntimeConfigNeededException.RequestType.PASSWORD, str2, null);
    }

    public RuntimePasswordNeededException(String str, String str2, Throwable th) {
        super(str, RuntimeConfigNeededException.RequestType.PASSWORD, str2, null, th);
    }

    public RuntimePasswordNeededException(String str, String str2, String str3, Throwable th) {
        super(str, RuntimeConfigNeededException.RequestType.PASSWORD, str2, str3, th);
    }
}
