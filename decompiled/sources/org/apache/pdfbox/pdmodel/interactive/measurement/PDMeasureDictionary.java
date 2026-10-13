package org.apache.pdfbox.pdmodel.interactive.measurement;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/measurement/PDMeasureDictionary.class */
public class PDMeasureDictionary implements COSObjectable {
    public static final String TYPE = "Measure";
    private final COSDictionary measureDictionary;

    protected PDMeasureDictionary() {
        this.measureDictionary = new COSDictionary();
        getCOSObject().setName(COSName.TYPE, TYPE);
    }

    public PDMeasureDictionary(COSDictionary dictionary) {
        this.measureDictionary = dictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.measureDictionary;
    }

    public String getType() {
        return TYPE;
    }

    public String getSubtype() {
        return getCOSObject().getNameAsString(COSName.SUBTYPE, PDRectlinearMeasureDictionary.SUBTYPE);
    }

    protected void setSubtype(String subtype) {
        getCOSObject().setName(COSName.SUBTYPE, subtype);
    }
}
