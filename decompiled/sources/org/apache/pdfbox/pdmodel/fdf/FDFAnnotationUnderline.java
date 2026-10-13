package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationUnderline.class */
public class FDFAnnotationUnderline extends FDFAnnotationTextMarkup {
    public static final String SUBTYPE = "Underline";

    public FDFAnnotationUnderline() {
        this.annot.setName(COSName.SUBTYPE, "Underline");
    }

    public FDFAnnotationUnderline(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationUnderline(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "Underline");
    }
}
