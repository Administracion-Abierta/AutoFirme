package com.sun.jna;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/Callback.class */
public interface Callback {
    public static final String METHOD_NAME = "callback";
    public static final List<String> FORBIDDEN_NAMES = Collections.unmodifiableList(Arrays.asList("hashCode", "equals", "toString"));

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/Callback$UncaughtExceptionHandler.class */
    public interface UncaughtExceptionHandler {
        void uncaughtException(Callback callback, Throwable th);
    }
}
