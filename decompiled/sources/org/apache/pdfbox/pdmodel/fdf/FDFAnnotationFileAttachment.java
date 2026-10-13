package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationFileAttachment.class */
public class FDFAnnotationFileAttachment extends FDFAnnotation {
    public static final String SUBTYPE = "FileAttachment";

    public FDFAnnotationFileAttachment() {
        this.annot.setName(COSName.SUBTYPE, "FileAttachment");
    }

    public FDFAnnotationFileAttachment(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationFileAttachment(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "FileAttachment");
    }
}
