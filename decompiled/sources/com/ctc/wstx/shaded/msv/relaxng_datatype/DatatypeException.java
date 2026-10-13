package com.ctc.wstx.shaded.msv.relaxng_datatype;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/DatatypeException.class */
public class DatatypeException extends Exception {
    private final int index;
    public static final int UNKNOWN = -1;

    public DatatypeException(int i, String str) {
        super(str);
        this.index = i;
    }

    public DatatypeException(String str) {
        this(-1, str);
    }

    public DatatypeException() {
        this(-1, null);
    }

    public int getIndex() {
        return this.index;
    }
}
