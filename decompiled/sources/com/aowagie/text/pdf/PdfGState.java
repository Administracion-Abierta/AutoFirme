package com.aowagie.text.pdf;

import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDLayoutAttributeObject;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfGState.class */
public class PdfGState extends PdfDictionary {
    public static final PdfName BM_NORMAL = new PdfName(PDLayoutAttributeObject.LINE_HEIGHT_NORMAL);
    public static final PdfName BM_COMPATIBLE = new PdfName("Compatible");

    public void setOverPrintStroking(boolean ov) {
        put(PdfName.OP, ov ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setOverPrintNonStroking(boolean ov) {
        put(PdfName.op, ov ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setOverPrintMode(int ov) {
        put(PdfName.OPM, new PdfNumber(ov == 0 ? 0 : 1));
    }

    public void setStrokeOpacity(float n) {
        put(PdfName.CA, new PdfNumber(n));
    }

    public void setFillOpacity(float n) {
        put(PdfName.ca, new PdfNumber(n));
    }

    public void setAlphaIsShape(boolean v) {
        put(PdfName.AIS, v ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setTextKnockout(boolean v) {
        put(PdfName.TK, v ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
    }

    public void setBlendMode(PdfName bm) {
        put(PdfName.BM, bm);
    }
}
