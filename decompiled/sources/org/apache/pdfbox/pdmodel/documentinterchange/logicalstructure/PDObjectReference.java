package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationUnknown;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDObjectReference.class */
public class PDObjectReference implements COSObjectable {
    public static final String TYPE = "OBJR";
    private final COSDictionary dictionary;

    public PDObjectReference() {
        this.dictionary = new COSDictionary();
        this.dictionary.setName(COSName.TYPE, TYPE);
    }

    public PDObjectReference(COSDictionary theDictionary) {
        this.dictionary = theDictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public COSObjectable getReferencedObject() {
        PDXObject xobject;
        COSBase obj = getCOSObject().getDictionaryObject(COSName.OBJ);
        if (!(obj instanceof COSDictionary)) {
            return null;
        }
        try {
            if ((obj instanceof COSStream) && (xobject = PDXObject.createXObject(obj, null)) != null) {
                return xobject;
            }
            COSDictionary objDictionary = (COSDictionary) obj;
            PDAnnotation annotation = PDAnnotation.createAnnotation(obj);
            if (!(annotation instanceof PDAnnotationUnknown) || COSName.ANNOT.equals(objDictionary.getDictionaryObject(COSName.TYPE))) {
                return annotation;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public void setReferencedObject(PDAnnotation annotation) {
        getCOSObject().setItem(COSName.OBJ, annotation);
    }

    public void setReferencedObject(PDXObject xobject) {
        getCOSObject().setItem(COSName.OBJ, xobject);
    }
}
