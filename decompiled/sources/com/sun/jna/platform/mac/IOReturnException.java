package com.sun.jna.platform.mac;

import com.sun.jna.platform.win32.Advapi32;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/IOReturnException.class */
public class IOReturnException extends RuntimeException {
    private static final long serialVersionUID = 1;
    private int ioReturn;

    public IOReturnException(int kr) {
        this(kr, formatMessage(kr));
    }

    protected IOReturnException(int kr, String msg) {
        super(msg);
        this.ioReturn = kr;
    }

    public int getIOReturnCode() {
        return this.ioReturn;
    }

    public static int getSystem(int kr) {
        return (kr >> 26) & 63;
    }

    public static int getSubSystem(int kr) {
        return (kr >> 14) & 4095;
    }

    public static int getCode(int kr) {
        return kr & Advapi32.MAX_VALUE_NAME;
    }

    private static String formatMessage(int kr) {
        return "IOReturn error code: " + kr + " (system=" + getSystem(kr) + ", subSystem=" + getSubSystem(kr) + ", code=" + getCode(kr) + ")";
    }
}
