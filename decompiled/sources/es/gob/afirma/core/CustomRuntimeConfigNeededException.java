package es.gob.afirma.core;

import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/CustomRuntimeConfigNeededException.class */
public abstract class CustomRuntimeConfigNeededException extends RuntimeConfigNeededException {
    private static final long serialVersionUID = -42502407809095471L;

    public abstract void prepareOperationWithConfirmation(Properties properties);

    public CustomRuntimeConfigNeededException(String str, RuntimeConfigNeededException.RequestType requestType, String str2, String str3) {
        super(str, requestType, str2, str3);
    }

    public CustomRuntimeConfigNeededException(String str, RuntimeConfigNeededException.RequestType requestType, String str2, String str3, Throwable th) {
        super(str, requestType, str2, str3, th);
    }
}
