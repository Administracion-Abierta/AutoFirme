package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfBorderDictionary.class */
class PdfBorderDictionary extends PdfDictionary {
    static final int STYLE_SOLID = 0;
    static final int STYLE_DASHED = 1;
    static final int STYLE_BEVELED = 2;
    static final int STYLE_INSET = 3;
    static final int STYLE_UNDERLINE = 4;

    public PdfBorderDictionary(float borderWidth, int borderStyle, PdfDashPattern dashes) {
        put(PdfName.W, new PdfNumber(borderWidth));
        switch (borderStyle) {
            case 0:
                put(PdfName.S, PdfName.S);
                return;
            case 1:
                if (dashes != null) {
                    put(PdfName.D, dashes);
                }
                put(PdfName.S, PdfName.D);
                return;
            case 2:
                put(PdfName.S, PdfName.B);
                return;
            case 3:
                put(PdfName.S, PdfName.I);
                return;
            case 4:
                put(PdfName.S, PdfName.U);
                return;
            default:
                throw new IllegalArgumentException("Invalid border style.");
        }
    }

    public PdfBorderDictionary(float borderWidth, int borderStyle) {
        this(borderWidth, borderStyle, null);
    }
}
