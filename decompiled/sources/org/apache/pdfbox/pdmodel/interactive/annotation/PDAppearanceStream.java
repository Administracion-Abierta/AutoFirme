package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAppearanceStream.class */
public class PDAppearanceStream extends PDFormXObject {
    public PDAppearanceStream(COSStream stream) {
        super(stream);
    }

    public PDAppearanceStream(PDDocument document) {
        super(document);
    }
}
