package com.aowagie.text.pdf.parser;

import com.aowagie.text.pdf.ColumnText;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/SimpleTextExtractingPdfContentStreamProcessor.class */
class SimpleTextExtractingPdfContentStreamProcessor extends PdfContentStreamProcessor {
    private Matrix lastTextLineMatrix = null;
    private Matrix lastEndingTextMatrix = null;
    private StringBuffer result = null;

    @Override // com.aowagie.text.pdf.parser.PdfContentStreamProcessor
    public void reset() {
        super.reset();
        this.lastTextLineMatrix = null;
        this.lastEndingTextMatrix = null;
        this.result = new StringBuffer();
    }

    public String getResultantText() {
        return this.result.toString();
    }

    @Override // com.aowagie.text.pdf.parser.PdfContentStreamProcessor
    public void displayText(String text, Matrix endingTextMatrix) {
        boolean hardReturn = false;
        if (this.lastTextLineMatrix != null && this.lastTextLineMatrix.get(7) != getCurrentTextLineMatrix().get(7)) {
            hardReturn = true;
        }
        float currentX = getCurrentTextMatrix().get(6);
        if (hardReturn) {
            this.result.append('\n');
        } else if (this.lastEndingTextMatrix != null) {
            float lastEndX = this.lastEndingTextMatrix.get(6);
            float spaceGlyphWidth = gs().font.getWidth(32) / 1000.0f;
            float spaceWidth = ((spaceGlyphWidth * gs().fontSize) + gs().characterSpacing + gs().wordSpacing) * gs().horizontalScaling;
            Matrix scaled = new Matrix(spaceWidth, ColumnText.GLOBAL_SPACE_CHAR_RATIO).multiply(getCurrentTextMatrix());
            float scaledSpaceWidth = scaled.get(6) - getCurrentTextMatrix().get(6);
            if (currentX - lastEndX > scaledSpaceWidth / 2.0f) {
                this.result.append(' ');
            }
        }
        this.result.append(text);
        this.lastTextLineMatrix = getCurrentTextLineMatrix();
        this.lastEndingTextMatrix = endingTextMatrix;
    }
}
