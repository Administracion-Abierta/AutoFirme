package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfNull.class */
class PdfNull extends PdfObject {
    static final PdfNull PDFNULL = new PdfNull();
    private static final String CONTENT = "null";

    public PdfNull() {
        super(8, CONTENT);
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public String toString() {
        return CONTENT;
    }
}
