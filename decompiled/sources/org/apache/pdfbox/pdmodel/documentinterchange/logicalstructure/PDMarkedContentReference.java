package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDMarkedContentReference.class */
public class PDMarkedContentReference implements COSObjectable {
    public static final String TYPE = "MCR";
    private final COSDictionary dictionary;

    public PDMarkedContentReference() {
        this.dictionary = new COSDictionary();
        this.dictionary.setName(COSName.TYPE, TYPE);
    }

    public PDMarkedContentReference(COSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public PDPage getPage() {
        COSDictionary pg = (COSDictionary) getCOSObject().getDictionaryObject(COSName.PG);
        if (pg != null) {
            return new PDPage(pg);
        }
        return null;
    }

    public void setPage(PDPage page) {
        getCOSObject().setItem(COSName.PG, page);
    }

    public int getMCID() {
        return getCOSObject().getInt(COSName.MCID);
    }

    public void setMCID(int mcid) {
        getCOSObject().setInt(COSName.MCID, mcid);
    }

    public String toString() {
        return "mcid=" + getMCID();
    }
}
