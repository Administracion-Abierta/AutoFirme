package org.apache.pdfbox.pdmodel.font;

import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDPanose.class */
public class PDPanose {
    public static final int LENGTH = 12;
    private final byte[] bytes;

    public PDPanose(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getFamilyClass() {
        return (this.bytes[0] << 8) | this.bytes[1];
    }

    public PDPanoseClassification getPanose() {
        byte[] panose = Arrays.copyOfRange(this.bytes, 2, 12);
        return new PDPanoseClassification(panose);
    }
}
