package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.Image;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfImportedPage.class */
public final class PdfImportedPage extends PdfTemplate {
    private final PdfReaderInstance readerInstance;
    private final int pageNumber;

    PdfImportedPage(PdfReaderInstance readerInstance, PdfWriter writer, int pageNumber) {
        this.readerInstance = readerInstance;
        this.pageNumber = pageNumber;
        this.writer = writer;
        this.bBox = readerInstance.getReader().getPageSize(pageNumber);
        setMatrix(1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, -this.bBox.getLeft(), -this.bBox.getBottom());
        this.type = 2;
    }

    public PdfImportedPage getFromReader() {
        return this;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void addImage(Image image, float a, float b, float c, float d, float e, float f) throws DocumentException {
        throwError();
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void addTemplate(PdfTemplate template, float a, float b, float c, float d, float e, float f) {
        throwError();
    }

    @Override // com.aowagie.text.pdf.PdfTemplate, com.aowagie.text.pdf.PdfContentByte
    public PdfContentByte getDuplicate() {
        throwError();
        return null;
    }

    @Override // com.aowagie.text.pdf.PdfTemplate
    PdfStream getFormXObject(int compressionLevel) throws IOException {
        return this.readerInstance.getFormXObject(this.pageNumber, compressionLevel);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setColorFill(PdfSpotColor sp, float tint) {
        throwError();
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setColorStroke(PdfSpotColor sp, float tint) {
        throwError();
    }

    @Override // com.aowagie.text.pdf.PdfTemplate
    PdfObject getResources() {
        return this.readerInstance.getResources(this.pageNumber);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setFontAndSize(BaseFont bf, float size) {
        throwError();
    }

    @Override // com.aowagie.text.pdf.PdfTemplate
    public void setGroup(PdfTransparencyGroup group) {
        throwError();
    }

    private void throwError() {
        throw new RuntimeException("Content can not be added to a PdfImportedPage.");
    }

    PdfReaderInstance getPdfReaderInstance() {
        return this.readerInstance;
    }
}
