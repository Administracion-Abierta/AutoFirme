package com.aowagie.text.pdf.codec;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/TIFFField.class */
class TIFFField implements Comparable, Serializable {
    private static final long serialVersionUID = 9088332901412823834L;
    static final int TIFF_BYTE = 1;
    static final int TIFF_ASCII = 2;
    static final int TIFF_SHORT = 3;
    static final int TIFF_LONG = 4;
    static final int TIFF_RATIONAL = 5;
    static final int TIFF_SBYTE = 6;
    static final int TIFF_UNDEFINED = 7;
    static final int TIFF_SSHORT = 8;
    static final int TIFF_SLONG = 9;
    static final int TIFF_SRATIONAL = 10;
    static final int TIFF_FLOAT = 11;
    static final int TIFF_DOUBLE = 12;
    private int tag;
    private int type;
    private int count;
    private Object data;

    TIFFField() {
    }

    TIFFField(int tag, int type, int count, Object data) {
        this.tag = tag;
        this.type = type;
        this.count = count;
        this.data = data;
    }

    public int getTag() {
        return this.tag;
    }

    public int getType() {
        return this.type;
    }

    public int getCount() {
        return this.count;
    }

    public byte[] getAsBytes() {
        return (byte[]) this.data;
    }

    public char[] getAsChars() {
        return (char[]) this.data;
    }

    public short[] getAsShorts() {
        return (short[]) this.data;
    }

    public int[] getAsInts() {
        return (int[]) this.data;
    }

    public long[] getAsLongs() {
        return (long[]) this.data;
    }

    public float[] getAsFloats() {
        return (float[]) this.data;
    }

    public double[] getAsDoubles() {
        return (double[]) this.data;
    }

    public int[][] getAsSRationals() {
        return (int[][]) this.data;
    }

    public long[][] getAsRationals() {
        return (long[][]) this.data;
    }

    int getAsInt(int index) {
        switch (this.type) {
            case 1:
            case 7:
                return ((byte[]) this.data)[index] & 255;
            case 2:
            case 4:
            case 5:
            default:
                throw new ClassCastException();
            case 3:
                return ((char[]) this.data)[index] & 65535;
            case 6:
                return ((byte[]) this.data)[index];
            case 8:
                return ((short[]) this.data)[index];
            case 9:
                return ((int[]) this.data)[index];
        }
    }

    long getAsLong(int index) {
        switch (this.type) {
            case 1:
            case 7:
                return ((byte[]) this.data)[index] & 255;
            case 2:
            case 5:
            default:
                throw new ClassCastException();
            case 3:
                return ((char[]) this.data)[index] & 65535;
            case 4:
                return ((long[]) this.data)[index];
            case 6:
                return ((byte[]) this.data)[index];
            case 8:
                return ((short[]) this.data)[index];
            case 9:
                return ((int[]) this.data)[index];
        }
    }

    float getAsFloat(int index) {
        switch (this.type) {
            case 1:
                return ((byte[]) this.data)[index] & 255;
            case 2:
            case 7:
            default:
                throw new ClassCastException();
            case 3:
                return ((char[]) this.data)[index] & 65535;
            case 4:
                return ((long[]) this.data)[index];
            case 5:
                long[] lvalue = getAsRational(index);
                return (float) (lvalue[0] / lvalue[1]);
            case 6:
                return ((byte[]) this.data)[index];
            case 8:
                return ((short[]) this.data)[index];
            case 9:
                return ((int[]) this.data)[index];
            case 10:
                int[] ivalue = getAsSRational(index);
                return (float) (((double) ivalue[0]) / ((double) ivalue[1]));
            case 11:
                return ((float[]) this.data)[index];
            case 12:
                return (float) ((double[]) this.data)[index];
        }
    }

    double getAsDouble(int index) {
        switch (this.type) {
            case 1:
                return ((byte[]) this.data)[index] & 255;
            case 2:
            case 7:
            default:
                throw new ClassCastException();
            case 3:
                return ((char[]) this.data)[index] & 65535;
            case 4:
                return ((long[]) this.data)[index];
            case 5:
                long[] lvalue = getAsRational(index);
                return lvalue[0] / lvalue[1];
            case 6:
                return ((byte[]) this.data)[index];
            case 8:
                return ((short[]) this.data)[index];
            case 9:
                return ((int[]) this.data)[index];
            case 10:
                int[] ivalue = getAsSRational(index);
                return ((double) ivalue[0]) / ((double) ivalue[1]);
            case 11:
                return ((float[]) this.data)[index];
            case 12:
                return ((double[]) this.data)[index];
        }
    }

    private int[] getAsSRational(int index) {
        return ((int[][]) this.data)[index];
    }

    long[] getAsRational(int index) {
        if (this.type == 4) {
            return getAsLongs();
        }
        return ((long[][]) this.data)[index];
    }

    @Override // java.lang.Comparable
    public int compareTo(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        int oTag = ((TIFFField) o).getTag();
        if (this.tag < oTag) {
            return -1;
        }
        if (this.tag > oTag) {
            return 1;
        }
        return 0;
    }
}
