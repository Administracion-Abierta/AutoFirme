package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfBoolean.class */
public class PdfBoolean extends PdfObject {
    public static final PdfBoolean PDFTRUE = new PdfBoolean(true);
    static final PdfBoolean PDFFALSE = new PdfBoolean(false);
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private boolean value;

    public PdfBoolean(boolean value) {
        super(1);
        if (value) {
            setContent(TRUE);
        } else {
            setContent(FALSE);
        }
        this.value = value;
    }

    PdfBoolean(String value) throws BadPdfFormatException {
        super(1, value);
        if (value.equals(TRUE)) {
            this.value = true;
        } else {
            if (value.equals(FALSE)) {
                this.value = false;
                return;
            }
            throw new BadPdfFormatException("The value has to be 'true' of 'false', instead of '" + value + "'.");
        }
    }

    boolean booleanValue() {
        return this.value;
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public String toString() {
        return this.value ? TRUE : FALSE;
    }
}
