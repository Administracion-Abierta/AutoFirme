package com.aowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfLiteral.class */
public class PdfLiteral extends PdfObject {
    private int position;

    public PdfLiteral(String text) {
        super(0, text);
    }

    PdfLiteral(byte[] b) {
        super(0, b);
    }

    PdfLiteral(int size) {
        super(0, (byte[]) null);
        this.bytes = new byte[size];
        Arrays.fill(this.bytes, (byte) 32);
    }

    PdfLiteral(int type, String text) {
        super(type, text);
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        if (os instanceof OutputStreamCounter) {
            this.position = ((OutputStreamCounter) os).getCounter();
        }
        super.toPdf(writer, os);
    }

    public int getPosition() {
        return this.position;
    }

    public int getPosLength() {
        if (this.bytes != null) {
            return this.bytes.length;
        }
        return 0;
    }
}
