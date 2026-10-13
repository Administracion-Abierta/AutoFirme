package com.aowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfDashPattern.class */
class PdfDashPattern extends PdfArray {
    private float dash;
    private final float gap = -1.0f;
    private final float phase = -1.0f;

    public PdfDashPattern() {
        this.dash = -1.0f;
        this.gap = -1.0f;
        this.phase = -1.0f;
    }

    PdfDashPattern(float dash) {
        super(new PdfNumber(dash));
        this.dash = -1.0f;
        this.gap = -1.0f;
        this.phase = -1.0f;
        this.dash = dash;
    }

    @Override // com.aowagie.text.pdf.PdfArray, com.aowagie.text.pdf.PdfObject
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        os.write(91);
        if (this.dash >= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            new PdfNumber(this.dash).toPdf(writer, os);
            getClass();
            if (-1.0f >= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                os.write(32);
                getClass();
                new PdfNumber(-1.0f).toPdf(writer, os);
            }
        }
        os.write(93);
        getClass();
        if (-1.0f >= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            os.write(32);
            getClass();
            new PdfNumber(-1.0f).toPdf(writer, os);
        }
    }
}
