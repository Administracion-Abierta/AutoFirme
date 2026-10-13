package org.apache.pdfbox.pdmodel.font;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDPanoseClassification.class */
public class PDPanoseClassification {
    public static final int LENGTH = 10;
    private final byte[] bytes;

    public PDPanoseClassification(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getFamilyKind() {
        return this.bytes[0];
    }

    public int getSerifStyle() {
        return this.bytes[1];
    }

    public int getWeight() {
        return this.bytes[2];
    }

    public int getProportion() {
        return this.bytes[3];
    }

    public int getContrast() {
        return this.bytes[4];
    }

    public int getStrokeVariation() {
        return this.bytes[5];
    }

    public int getArmStyle() {
        return this.bytes[6];
    }

    public int getLetterform() {
        return this.bytes[7];
    }

    public int getMidline() {
        return this.bytes[8];
    }

    public int getXHeight() {
        return this.bytes[9];
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public String toString() {
        return "{ FamilyKind = " + getFamilyKind() + ", SerifStyle = " + getSerifStyle() + ", Weight = " + getWeight() + ", Proportion = " + getProportion() + ", Contrast = " + getContrast() + ", StrokeVariation = " + getStrokeVariation() + ", ArmStyle = " + getArmStyle() + ", Letterform = " + getLetterform() + ", Midline = " + getMidline() + ", XHeight = " + getXHeight() + "}";
    }
}
