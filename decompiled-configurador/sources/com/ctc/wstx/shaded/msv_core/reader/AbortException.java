package com.ctc.wstx.shaded.msv_core.reader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/AbortException.class */
public class AbortException extends Exception {
    public static final AbortException theInstance = new AbortException();

    private AbortException() {
        super("aborted. Errors should have been reported");
    }
}
