package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDExternalDataDictionary.class */
public class PDExternalDataDictionary implements COSObjectable {
    private final COSDictionary dataDictionary;

    public PDExternalDataDictionary() {
        this.dataDictionary = new COSDictionary();
        this.dataDictionary.setName(COSName.TYPE, "ExData");
    }

    public PDExternalDataDictionary(COSDictionary dictionary) {
        this.dataDictionary = dictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dataDictionary;
    }

    public String getType() {
        return getCOSObject().getNameAsString(COSName.TYPE, "ExData");
    }

    public String getSubtype() {
        return getCOSObject().getNameAsString(COSName.SUBTYPE);
    }

    public void setSubtype(String subtype) {
        getCOSObject().setName(COSName.SUBTYPE, subtype);
    }
}
