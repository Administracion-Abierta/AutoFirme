package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfBorderArray.class */
class PdfBorderArray extends PdfArray {
    public PdfBorderArray(float hRadius, float vRadius, float width) {
        this(hRadius, vRadius, width, null);
    }

    private PdfBorderArray(float hRadius, float vRadius, float width, PdfDashPattern dash) {
        super(new PdfNumber(hRadius));
        add(new PdfNumber(vRadius));
        add(new PdfNumber(width));
        if (dash != null) {
            add(dash);
        }
    }
}
