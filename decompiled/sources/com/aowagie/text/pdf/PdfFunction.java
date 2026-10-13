package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfFunction.class */
class PdfFunction {
    private final PdfWriter writer;
    private PdfIndirectReference reference;
    private PdfDictionary dictionary;

    private PdfFunction(PdfWriter writer) {
        this.writer = writer;
    }

    PdfIndirectReference getReference() {
        try {
            if (this.reference == null) {
                this.reference = this.writer.addToBody(this.dictionary).getIndirectReference();
            }
            return this.reference;
        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    static PdfFunction type2(PdfWriter writer, float[] domain, float[] range, float[] c0, float[] c1, float n) {
        PdfFunction func = new PdfFunction(writer);
        func.dictionary = new PdfDictionary();
        func.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(2));
        func.dictionary.put(PdfName.DOMAIN, new PdfArray(domain));
        if (range != null) {
            func.dictionary.put(PdfName.RANGE, new PdfArray(range));
        }
        if (c0 != null) {
            func.dictionary.put(PdfName.C0, new PdfArray(c0));
        }
        if (c1 != null) {
            func.dictionary.put(PdfName.C1, new PdfArray(c1));
        }
        func.dictionary.put(PdfName.N, new PdfNumber(n));
        return func;
    }
}
