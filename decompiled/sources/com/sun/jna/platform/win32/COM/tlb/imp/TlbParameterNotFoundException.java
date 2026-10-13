package com.sun.jna.platform.win32.COM.tlb.imp;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/imp/TlbParameterNotFoundException.class */
public class TlbParameterNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public TlbParameterNotFoundException() {
    }

    public TlbParameterNotFoundException(String msg) {
        super(msg);
    }

    public TlbParameterNotFoundException(Throwable cause) {
        super(cause);
    }

    public TlbParameterNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
