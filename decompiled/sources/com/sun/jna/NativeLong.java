package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/NativeLong.class */
public class NativeLong extends IntegerType {
    private static final long serialVersionUID = 1;
    public static final int SIZE = Native.LONG_SIZE;

    public NativeLong() {
        this(0L);
    }

    public NativeLong(long value) {
        this(value, false);
    }

    public NativeLong(long value, boolean unsigned) {
        super(SIZE, value, unsigned);
    }
}
