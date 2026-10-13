package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/PDSeedValueMDP.class */
public class PDSeedValueMDP {
    private final COSDictionary dictionary;

    public PDSeedValueMDP() {
        this.dictionary = new COSDictionary();
        this.dictionary.setDirect(true);
    }

    public PDSeedValueMDP(COSDictionary dict) {
        this.dictionary = dict;
        this.dictionary.setDirect(true);
    }

    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public int getP() {
        return this.dictionary.getInt(COSName.P);
    }

    public void setP(int p) {
        if (p < 0 || p > 3) {
            throw new IllegalArgumentException("Only values between 0 and 3 nare allowed.");
        }
        this.dictionary.setInt(COSName.P, p);
    }
}
