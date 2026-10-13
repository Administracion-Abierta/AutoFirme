package org.apache.pdfbox.pdmodel.font;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDCIDSystemInfo.class */
public final class PDCIDSystemInfo implements COSObjectable {
    private final COSDictionary dictionary;

    PDCIDSystemInfo(String registry, String ordering, int supplement) {
        this.dictionary = new COSDictionary();
        this.dictionary.setString(COSName.REGISTRY, registry);
        this.dictionary.setString(COSName.ORDERING, ordering);
        this.dictionary.setInt(COSName.SUPPLEMENT, supplement);
    }

    PDCIDSystemInfo(COSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public String getRegistry() {
        return this.dictionary.getNameAsString(COSName.REGISTRY);
    }

    public String getOrdering() {
        return this.dictionary.getNameAsString(COSName.ORDERING);
    }

    public int getSupplement() {
        return this.dictionary.getInt(COSName.SUPPLEMENT);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.dictionary;
    }

    public String toString() {
        return getRegistry() + "-" + getOrdering() + "-" + getSupplement();
    }
}
