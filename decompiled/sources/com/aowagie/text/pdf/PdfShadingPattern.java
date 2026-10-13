package com.aowagie.text.pdf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfShadingPattern.class */
public class PdfShadingPattern extends PdfDictionary {
    private final PdfShading shading;
    private final PdfWriter writer;
    private float[] matrix = {1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO};
    private PdfName patternName;
    private PdfIndirectReference patternReference;

    PdfShadingPattern(PdfShading shading) {
        this.writer = shading.getWriter();
        put(PdfName.PATTERNTYPE, new PdfNumber(2));
        this.shading = shading;
    }

    PdfName getPatternName() {
        return this.patternName;
    }

    PdfIndirectReference getPatternReference() {
        if (this.patternReference == null) {
            this.patternReference = this.writer.getPdfIndirectReference();
        }
        return this.patternReference;
    }

    private PdfIndirectReference getShadingReference() {
        return this.shading.getShadingReference();
    }

    void setName(int number) {
        this.patternName = new PdfName("P" + number);
    }

    void addToBody() throws IOException {
        put(PdfName.SHADING, getShadingReference());
        put(PdfName.MATRIX, new PdfArray(this.matrix));
        this.writer.addToBody(this, getPatternReference());
    }

    public void setMatrix(float[] matrix) {
        if (matrix.length != 6) {
            throw new RuntimeException("The matrix size must be 6.");
        }
        this.matrix = matrix;
    }

    public float[] getMatrix() {
        return this.matrix;
    }

    public PdfShading getShading() {
        return this.shading;
    }

    ColorDetails getColorDetails() {
        return this.shading.getColorDetails();
    }
}
