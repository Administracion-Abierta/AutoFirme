package org.apache.pdfbox.pdmodel.interactive.measurement;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/measurement/PDViewportDictionary.class */
public class PDViewportDictionary implements COSObjectable {
    public static final String TYPE = "Viewport";
    private final COSDictionary viewportDictionary;

    public PDViewportDictionary() {
        this.viewportDictionary = new COSDictionary();
    }

    public PDViewportDictionary(COSDictionary dictionary) {
        this.viewportDictionary = dictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.viewportDictionary;
    }

    public String getType() {
        return TYPE;
    }

    public PDRectangle getBBox() {
        COSBase bbox = getCOSObject().getDictionaryObject(COSName.BBOX);
        if (bbox instanceof COSArray) {
            return new PDRectangle((COSArray) bbox);
        }
        return null;
    }

    public void setBBox(PDRectangle rectangle) {
        getCOSObject().setItem(COSName.BBOX, rectangle);
    }

    public String getName() {
        return getCOSObject().getNameAsString(COSName.NAME);
    }

    public void setName(String name) {
        getCOSObject().setName(COSName.NAME, name);
    }

    public PDMeasureDictionary getMeasure() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.MEASURE);
        if (base instanceof COSDictionary) {
            return new PDMeasureDictionary((COSDictionary) base);
        }
        return null;
    }

    public void setMeasure(PDMeasureDictionary measure) {
        getCOSObject().setItem(COSName.MEASURE, measure);
    }
}
