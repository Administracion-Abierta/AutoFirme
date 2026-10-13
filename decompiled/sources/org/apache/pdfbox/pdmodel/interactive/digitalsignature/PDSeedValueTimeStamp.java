package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/PDSeedValueTimeStamp.class */
public class PDSeedValueTimeStamp {
    private final COSDictionary dictionary;

    public PDSeedValueTimeStamp() {
        this.dictionary = new COSDictionary();
        this.dictionary.setDirect(true);
    }

    public PDSeedValueTimeStamp(COSDictionary dict) {
        this.dictionary = dict;
        this.dictionary.setDirect(true);
    }

    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public String getURL() {
        return this.dictionary.getString(COSName.URL);
    }

    public void setURL(String url) {
        this.dictionary.setString(COSName.URL, url);
    }

    public boolean isTimestampRequired() {
        return this.dictionary.getInt(COSName.FT, 0) != 0;
    }

    public void setTimestampRequired(boolean flag) {
        this.dictionary.setInt(COSName.FT, flag ? 1 : 0);
    }
}
