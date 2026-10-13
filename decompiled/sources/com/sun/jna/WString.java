package com.sun.jna;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/WString.class */
public final class WString implements CharSequence, Comparable {
    private String string;

    public WString(String s) {
        if (s == null) {
            throw new NullPointerException("String initializer must be non-null");
        }
        this.string = s;
    }

    @Override // java.lang.CharSequence
    public String toString() {
        return this.string;
    }

    public boolean equals(Object o) {
        return (o instanceof WString) && toString().equals(o.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }

    @Override // java.lang.CharSequence
    public int length() {
        return toString().length();
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }
}
