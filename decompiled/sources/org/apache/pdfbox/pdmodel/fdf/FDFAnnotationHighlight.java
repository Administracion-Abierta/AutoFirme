package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationHighlight.class */
public class FDFAnnotationHighlight extends FDFAnnotationTextMarkup {
    public static final String SUBTYPE = "Highlight";

    public FDFAnnotationHighlight() {
        this.annot.setName(COSName.SUBTYPE, "Highlight");
    }

    public FDFAnnotationHighlight(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationHighlight(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "Highlight");
    }
}
