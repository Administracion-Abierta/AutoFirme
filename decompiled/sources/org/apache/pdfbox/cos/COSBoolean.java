package org.apache.pdfbox.cos;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSBoolean.class */
public final class COSBoolean extends COSBase {
    public static final byte[] TRUE_BYTES = {116, 114, 117, 101};
    public static final byte[] FALSE_BYTES = {102, 97, 108, 115, 101};
    public static final COSBoolean TRUE = new COSBoolean(true);
    public static final COSBoolean FALSE = new COSBoolean(false);
    private final boolean value;

    private COSBoolean(boolean aValue) {
        this.value = aValue;
    }

    public boolean getValue() {
        return this.value;
    }

    public Boolean getValueAsObject() {
        return this.value ? Boolean.TRUE : Boolean.FALSE;
    }

    public static COSBoolean getBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static COSBoolean getBoolean(Boolean value) {
        return getBoolean(value.booleanValue());
    }

    @Override // org.apache.pdfbox.cos.COSBase
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromBoolean(this);
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public void writePDF(OutputStream output) throws IOException {
        if (this.value) {
            output.write(TRUE_BYTES);
        } else {
            output.write(FALSE_BYTES);
        }
    }
}
