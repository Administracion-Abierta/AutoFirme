package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationSquiggly.class */
public class FDFAnnotationSquiggly extends FDFAnnotationTextMarkup {
    public static final String SUBTYPE = "Squiggly";

    public FDFAnnotationSquiggly() {
        this.annot.setName(COSName.SUBTYPE, "Squiggly");
    }

    public FDFAnnotationSquiggly(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationSquiggly(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "Squiggly");
    }
}
