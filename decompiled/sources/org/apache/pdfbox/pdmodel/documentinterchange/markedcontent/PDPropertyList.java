package org.apache.pdfbox.pdmodel.documentinterchange.markedcontent;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentMembershipDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/markedcontent/PDPropertyList.class */
public class PDPropertyList implements COSObjectable {
    protected final COSDictionary dict;

    public static PDPropertyList create(COSDictionary dict) {
        if (COSName.OCG.equals(dict.getItem(COSName.TYPE))) {
            return new PDOptionalContentGroup(dict);
        }
        if (COSName.OCMD.equals(dict.getItem(COSName.TYPE))) {
            return new PDOptionalContentMembershipDictionary(dict);
        }
        return new PDPropertyList(dict);
    }

    protected PDPropertyList() {
        this.dict = new COSDictionary();
    }

    protected PDPropertyList(COSDictionary dict) {
        this.dict = dict;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dict;
    }
}
