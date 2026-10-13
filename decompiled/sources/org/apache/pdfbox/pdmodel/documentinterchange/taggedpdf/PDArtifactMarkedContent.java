package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import com.aowagie.text.ElementTags;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/taggedpdf/PDArtifactMarkedContent.class */
public class PDArtifactMarkedContent extends PDMarkedContent {
    public PDArtifactMarkedContent(COSDictionary properties) {
        super(COSName.ARTIFACT, properties);
    }

    public String getType() {
        return getProperties().getNameAsString(COSName.TYPE);
    }

    public PDRectangle getBBox() {
        PDRectangle retval = null;
        COSArray a = (COSArray) getProperties().getDictionaryObject(COSName.BBOX);
        if (a != null) {
            retval = new PDRectangle(a);
        }
        return retval;
    }

    public boolean isTopAttached() {
        return isAttached("Top");
    }

    public boolean isBottomAttached() {
        return isAttached("Bottom");
    }

    public boolean isLeftAttached() {
        return isAttached(ElementTags.ALIGN_LEFT);
    }

    public boolean isRightAttached() {
        return isAttached(ElementTags.ALIGN_RIGHT);
    }

    public String getSubtype() {
        return getProperties().getNameAsString(COSName.SUBTYPE);
    }

    private boolean isAttached(String edge) {
        COSArray a = (COSArray) getProperties().getDictionaryObject(COSName.ATTACHED);
        if (a != null) {
            for (int i = 0; i < a.size(); i++) {
                if (edge.equals(a.getName(i))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
