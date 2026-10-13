package com.aowagie.text.pdf.collection;

import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfString;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/collection/PdfCollection.class */
public class PdfCollection extends PdfDictionary {
    private static final int TILE = 1;
    private static final int HIDDEN = 2;

    public void setInitialDocument(String description) {
        put(PdfName.D, new PdfString(description, null));
    }

    public void setSchema(PdfCollectionSchema schema) {
        put(PdfName.SCHEMA, schema);
    }

    public PdfCollectionSchema getSchema() {
        return (PdfCollectionSchema) get(PdfName.SCHEMA);
    }

    public void setSort(PdfCollectionSort sort) {
        put(PdfName.SORT, sort);
    }
}
