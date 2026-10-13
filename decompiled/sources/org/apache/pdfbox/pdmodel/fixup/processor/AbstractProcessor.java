package org.apache.pdfbox.pdmodel.fixup.processor;

import org.apache.pdfbox.pdmodel.PDDocument;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fixup/processor/AbstractProcessor.class */
public abstract class AbstractProcessor implements PDDocumentProcessor {
    protected PDDocument document;

    protected AbstractProcessor(PDDocument document) {
        this.document = document;
    }
}
