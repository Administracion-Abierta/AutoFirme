package org.apache.pdfbox.pdmodel.font;

import org.apache.fontbox.FontBoxFont;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/FontInfo.class */
public abstract class FontInfo {
    public abstract String getPostScriptName();

    public abstract FontFormat getFormat();

    public abstract CIDSystemInfo getCIDSystemInfo();

    public abstract FontBoxFont getFont();

    public abstract int getFamilyClass();

    public abstract int getWeightClass();

    public abstract int getCodePageRange1();

    public abstract int getCodePageRange2();

    public abstract int getMacStyle();

    public abstract PDPanoseClassification getPanose();

    final int getWeightClassAsPanose() {
        int usWeightClass = getWeightClass();
        switch (usWeightClass) {
            case -1:
                return 0;
            case 0:
                return 0;
            case 100:
                return 2;
            case 200:
                return 3;
            case 300:
                return 4;
            case 400:
                return 5;
            case 500:
                return 6;
            case 600:
                return 7;
            case 700:
                return 8;
            case 800:
                return 9;
            case 900:
                return 10;
            default:
                return 0;
        }
    }

    final long getCodePageRange() {
        long range1 = ((long) getCodePageRange1()) & 4294967295L;
        long range2 = ((long) getCodePageRange2()) & 4294967295L;
        return (range2 << 32) | range1;
    }

    public String toString() {
        return getPostScriptName() + " (" + getFormat() + ", mac: 0x" + Integer.toHexString(getMacStyle()) + ", os/2: 0x" + Integer.toHexString(getFamilyClass()) + ", cid: " + getCIDSystemInfo() + ")";
    }
}
