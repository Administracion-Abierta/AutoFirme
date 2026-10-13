package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/LastErrorException.class */
public class LastErrorException extends RuntimeException {
    private static final long serialVersionUID = 1;
    private int errorCode;

    private static String formatMessage(int code) {
        return Platform.isWindows() ? "GetLastError() returned " + code : "errno was " + code;
    }

    private static String parseMessage(String m) {
        try {
            return formatMessage(Integer.parseInt(m));
        } catch (NumberFormatException e) {
            return m;
        }
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public LastErrorException(String msg) {
        super(parseMessage(msg.trim()));
        try {
            this.errorCode = Integer.parseInt(msg.startsWith("[") ? msg.substring(1, msg.indexOf("]")) : msg);
        } catch (NumberFormatException e) {
            this.errorCode = -1;
        }
    }

    public LastErrorException(int code) {
        this(code, formatMessage(code));
    }

    protected LastErrorException(int code, String msg) {
        super(msg);
        this.errorCode = code;
    }
}
