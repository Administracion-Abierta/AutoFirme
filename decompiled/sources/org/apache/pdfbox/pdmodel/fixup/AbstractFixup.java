package org.apache.pdfbox.pdmodel.fixup;

import org.apache.pdfbox.pdmodel.PDDocument;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fixup/AbstractFixup.class */
public abstract class AbstractFixup implements PDDocumentFixup {
    protected PDDocument document;

    protected AbstractFixup(PDDocument document) {
        this.document = document;
    }
}
