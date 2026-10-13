package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/NativeString.class */
class NativeString implements CharSequence, Comparable {
    static final String WIDE_STRING = "--WIDE-STRING--";
    private Pointer pointer;
    private String encoding;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/NativeString$StringMemory.class */
    private class StringMemory extends Memory {
        public StringMemory(long size) {
            super(size);
        }

        @Override // com.sun.jna.Memory, com.sun.jna.Pointer
        public String toString() {
            return NativeString.this.toString();
        }
    }

    public NativeString(String string) {
        this(string, Native.getDefaultStringEncoding());
    }

    public NativeString(String string, boolean wide) {
        this(string, wide ? WIDE_STRING : Native.getDefaultStringEncoding());
    }

    public NativeString(WString string) {
        this(string.toString(), WIDE_STRING);
    }

    public NativeString(String string, String encoding) {
        if (string == null) {
            throw new NullPointerException("String must not be null");
        }
        this.encoding = encoding;
        if (WIDE_STRING.equals(this.encoding)) {
            int len = (string.length() + 1) * Native.WCHAR_SIZE;
            this.pointer = new StringMemory(len);
            this.pointer.setWideString(0L, string);
        } else {
            byte[] data = Native.getBytes(string, encoding);
            this.pointer = new StringMemory(data.length + 1);
            this.pointer.write(0L, data, 0, data.length);
            this.pointer.setByte(data.length, (byte) 0);
        }
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object other) {
        return (other instanceof CharSequence) && compareTo(other) == 0;
    }

    @Override // java.lang.CharSequence
    public String toString() {
        boolean wide = WIDE_STRING.equals(this.encoding);
        return wide ? this.pointer.getWideString(0L) : this.pointer.getString(0L, this.encoding);
    }

    public Pointer getPointer() {
        return this.pointer;
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override // java.lang.CharSequence
    public int length() {
        return toString().length();
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override // java.lang.Comparable
    public int compareTo(Object other) {
        if (other == null) {
            return 1;
        }
        return toString().compareTo(other.toString());
    }
}
