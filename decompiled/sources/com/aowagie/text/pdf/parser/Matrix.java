package com.aowagie.text.pdf.parser;

import com.aowagie.text.pdf.ColumnText;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/Matrix.class */
class Matrix {
    private static final int I11 = 0;
    private static final int I12 = 1;
    private static final int I13 = 2;
    private static final int I21 = 3;
    private static final int I22 = 4;
    private static final int I23 = 5;
    public static final int I31 = 6;
    public static final int I32 = 7;
    private static final int I33 = 8;
    private final float[] vals = {1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f};

    public Matrix() {
    }

    public Matrix(float tx, float ty) {
        this.vals[6] = tx;
        this.vals[7] = ty;
    }

    public Matrix(float a, float b, float c, float d, float e, float f) {
        this.vals[0] = a;
        this.vals[1] = b;
        this.vals[2] = 0.0f;
        this.vals[3] = c;
        this.vals[4] = d;
        this.vals[5] = 0.0f;
        this.vals[6] = e;
        this.vals[7] = f;
        this.vals[8] = 1.0f;
    }

    public float get(int index) {
        return this.vals[index];
    }

    public Matrix multiply(Matrix by) {
        Matrix rslt = new Matrix();
        float[] a = this.vals;
        float[] b = by.vals;
        float[] c = rslt.vals;
        c[0] = (a[0] * b[0]) + (a[1] * b[3]) + (a[2] * b[6]);
        c[1] = (a[0] * b[1]) + (a[1] * b[4]) + (a[2] * b[7]);
        c[2] = (a[0] * b[2]) + (a[1] * b[5]) + (a[2] * b[8]);
        c[3] = (a[3] * b[0]) + (a[4] * b[3]) + (a[5] * b[6]);
        c[4] = (a[3] * b[1]) + (a[4] * b[4]) + (a[5] * b[7]);
        c[5] = (a[3] * b[2]) + (a[4] * b[5]) + (a[5] * b[8]);
        c[6] = (a[6] * b[0]) + (a[7] * b[3]) + (a[8] * b[6]);
        c[7] = (a[6] * b[1]) + (a[7] * b[4]) + (a[8] * b[7]);
        c[8] = (a[6] * b[2]) + (a[7] * b[5]) + (a[8] * b[8]);
        return rslt;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Matrix)) {
            return false;
        }
        return Arrays.equals(this.vals, ((Matrix) obj).vals);
    }

    public int hashCode() {
        int result = 1;
        for (float val : this.vals) {
            result = (31 * result) + Float.floatToIntBits(val);
        }
        return result;
    }

    public String toString() {
        return this.vals[0] + TlbBase.TAB + this.vals[1] + TlbBase.TAB + this.vals[2] + "\n" + this.vals[3] + TlbBase.TAB + this.vals[4] + TlbBase.TAB + this.vals[2] + "\n" + this.vals[6] + TlbBase.TAB + this.vals[7] + TlbBase.TAB + this.vals[8];
    }
}
