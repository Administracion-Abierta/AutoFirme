package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.WinNT;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/COMException.class */
public class COMException extends RuntimeException {
    private static final long serialVersionUID = 1;
    private final WinNT.HRESULT hresult;

    public COMException() {
        this("", (Throwable) null);
    }

    public COMException(String message) {
        this(message, (Throwable) null);
    }

    public COMException(Throwable cause) {
        this((String) null, cause);
    }

    public COMException(String message, Throwable cause) {
        super(message, cause);
        this.hresult = null;
    }

    public COMException(String message, WinNT.HRESULT hresult) {
        super(message);
        this.hresult = hresult;
    }

    public WinNT.HRESULT getHresult() {
        return this.hresult;
    }

    public boolean matchesErrorCode(int errorCode) {
        return this.hresult != null && this.hresult.intValue() == errorCode;
    }
}
