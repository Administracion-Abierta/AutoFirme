package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAnnotationPopup.class */
public class PDAnnotationPopup extends PDAnnotation {
    public static final String SUB_TYPE = "Popup";

    public PDAnnotationPopup() {
        getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
    }

    public PDAnnotationPopup(COSDictionary field) {
        super(field);
    }

    public void setOpen(boolean open) {
        getCOSObject().setBoolean("Open", open);
    }

    public boolean getOpen() {
        return getCOSObject().getBoolean("Open", false);
    }

    public void setParent(PDAnnotationMarkup annot) {
        getCOSObject().setItem(COSName.PARENT, (COSBase) annot.getCOSObject());
    }

    public PDAnnotationMarkup getParent() {
        PDAnnotationMarkup am = null;
        try {
            am = (PDAnnotationMarkup) PDAnnotation.createAnnotation(getCOSObject().getDictionaryObject(COSName.PARENT, COSName.P));
        } catch (IOException e) {
        }
        return am;
    }
}
