package org.apache.pdfbox.cos;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSInteger.class */
public final class COSInteger extends COSNumber {
    private static final int LOW = -100;
    private static final int HIGH = 256;
    private static final COSInteger[] STATIC = new COSInteger[357];
    public static final COSInteger ZERO = get(0);
    public static final COSInteger ONE = get(1);
    public static final COSInteger TWO = get(2);
    public static final COSInteger THREE = get(3);
    protected static final COSInteger OUT_OF_RANGE_MAX = getInvalid(true);
    protected static final COSInteger OUT_OF_RANGE_MIN = getInvalid(false);
    private final long value;
    private final boolean isValid;

    public static COSInteger get(long val) {
        if (-100 <= val && val <= 256) {
            int index = ((int) val) - LOW;
            if (STATIC[index] == null) {
                STATIC[index] = new COSInteger(val, true);
            }
            return STATIC[index];
        }
        return new COSInteger(val, true);
    }

    private static COSInteger getInvalid(boolean maxValue) {
        return maxValue ? new COSInteger(Long.MAX_VALUE, false) : new COSInteger(Long.MIN_VALUE, false);
    }

    private COSInteger(long val, boolean valid) {
        this.value = val;
        this.isValid = valid;
    }

    public boolean equals(Object o) {
        return (o instanceof COSInteger) && ((COSInteger) o).intValue() == intValue();
    }

    public int hashCode() {
        return (int) (this.value ^ (this.value >> 32));
    }

    public String toString() {
        return "COSInt{" + this.value + "}";
    }

    @Override // org.apache.pdfbox.cos.COSNumber
    public float floatValue() {
        return this.value;
    }

    @Override // org.apache.pdfbox.cos.COSNumber
    public double doubleValue() {
        return this.value;
    }

    @Override // org.apache.pdfbox.cos.COSNumber
    public int intValue() {
        return (int) this.value;
    }

    @Override // org.apache.pdfbox.cos.COSNumber
    public long longValue() {
        return this.value;
    }

    public boolean isValid() {
        return this.isValid;
    }

    @Override // org.apache.pdfbox.cos.COSBase
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromInt(this);
    }

    public void writePDF(OutputStream output) throws IOException {
        output.write(String.valueOf(this.value).getBytes("ISO-8859-1"));
    }
}
