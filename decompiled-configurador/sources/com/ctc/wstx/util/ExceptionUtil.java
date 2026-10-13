package com.ctc.wstx.util;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/ExceptionUtil.class */
public final class ExceptionUtil {
    private ExceptionUtil() {
    }

    public static void throwRuntimeException(Throwable t) {
        throwIfUnchecked(t);
        throw new RuntimeException("[was " + t.getClass() + "] " + t.getMessage(), t);
    }

    public static IOException constructIOException(String msg, Throwable src) {
        IOException e = new IOException(msg);
        setInitCause(e, src);
        return e;
    }

    public static void throwAsIllegalArgument(Throwable t) {
        throwIfUnchecked(t);
        IllegalArgumentException rex = new IllegalArgumentException("[was " + t.getClass() + "] " + t.getMessage());
        setInitCause(rex, t);
        throw rex;
    }

    public static void throwIfUnchecked(Throwable t) {
        if (t instanceof RuntimeException) {
            throw ((RuntimeException) t);
        }
        if (t instanceof Error) {
            throw ((Error) t);
        }
    }

    public static void throwGenericInternal() {
        throwInternal(null);
    }

    public static void throwInternal(String msg) {
        if (msg == null) {
            msg = "[no description]";
        }
        throw new RuntimeException("Internal error: " + msg);
    }

    public static void setInitCause(Throwable newT, Throwable rootT) {
        if (newT.getCause() == null) {
            newT.initCause(rootT);
        }
    }
}
