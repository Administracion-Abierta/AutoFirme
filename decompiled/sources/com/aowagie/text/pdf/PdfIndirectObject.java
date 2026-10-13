package com.aowagie.text.pdf;

import com.aowagie.text.DocWriter;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfIndirectObject.class */
public final class PdfIndirectObject {
    private final int number;
    private int generation;
    private static final byte[] STARTOBJ = DocWriter.getISOBytes(" obj\n");
    private static final byte[] ENDOBJ = DocWriter.getISOBytes("\nendobj\n");
    private final PdfObject object;
    private final PdfWriter writer;

    PdfIndirectObject(int number, PdfObject object, PdfWriter writer) {
        this(number, 0, object, writer);
    }

    private PdfIndirectObject(int number, int generation, PdfObject object, PdfWriter writer) {
        this.generation = 0;
        this.writer = writer;
        this.number = number;
        this.generation = generation;
        this.object = object;
        PdfEncryption crypto = null;
        crypto = writer != null ? writer.getEncryption() : crypto;
        if (crypto != null) {
            crypto.setHashKey(number, generation);
        }
    }

    public PdfIndirectReference getIndirectReference() {
        return new PdfIndirectReference(this.object.type(), this.number, this.generation);
    }

    void writeTo(OutputStream os) throws IOException {
        os.write(DocWriter.getISOBytes(String.valueOf(this.number)));
        os.write(32);
        os.write(DocWriter.getISOBytes(String.valueOf(this.generation)));
        os.write(STARTOBJ);
        this.object.toPdf(this.writer, os);
        os.write(ENDOBJ);
    }
}
