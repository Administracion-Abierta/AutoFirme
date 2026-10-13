package com.aowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PRIndirectReference.class */
public class PRIndirectReference extends PdfIndirectReference {
    private final PdfReader reader;

    PRIndirectReference(PdfReader reader, int number, int generation) {
        this.type = 10;
        this.number = number;
        this.generation = generation;
        this.reader = reader;
    }

    PRIndirectReference(PdfReader reader, int number) {
        this(reader, number, 0);
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        int n = writer.getNewObjectNumber(this.reader, this.number, this.generation);
        os.write(PdfEncodings.convertToBytes(new StringBuffer().append(n).append(" 0 R").toString(), (String) null));
    }

    public PdfReader getReader() {
        return this.reader;
    }

    void setNumber(int number, int generation) {
        this.number = number;
        this.generation = generation;
    }
}
