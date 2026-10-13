package org.java_websocket.exceptions;

import com.sun.jna.platform.win32.WinNT;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/exceptions/LimitExceededException.class */
public class LimitExceededException extends InvalidDataException {
    private static final long serialVersionUID = 6908339749836826785L;
    private final int limit;

    public LimitExceededException() {
        this(WinNT.MAXLONG);
    }

    public LimitExceededException(int limit) {
        super(1009);
        this.limit = limit;
    }

    public LimitExceededException(String s, int limit) {
        super(1009, s);
        this.limit = limit;
    }

    public LimitExceededException(String s) {
        this(s, WinNT.MAXLONG);
    }

    public int getLimit() {
        return this.limit;
    }
}
