package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAppearanceDictionary.class */
public class PDAppearanceDictionary implements COSObjectable {
    private final COSDictionary dictionary;

    public PDAppearanceDictionary() {
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.N, (COSBase) new COSDictionary());
    }

    public PDAppearanceDictionary(COSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public PDAppearanceEntry getNormalAppearance() {
        COSBase entry = this.dictionary.getDictionaryObject(COSName.N);
        if (entry instanceof COSDictionary) {
            return new PDAppearanceEntry(entry);
        }
        return null;
    }

    public void setNormalAppearance(PDAppearanceEntry entry) {
        this.dictionary.setItem(COSName.N, entry);
    }

    public void setNormalAppearance(PDAppearanceStream ap) {
        this.dictionary.setItem(COSName.N, ap);
    }

    public PDAppearanceEntry getRolloverAppearance() {
        COSBase entry = this.dictionary.getDictionaryObject(COSName.R);
        if (entry instanceof COSDictionary) {
            return new PDAppearanceEntry(entry);
        }
        return getNormalAppearance();
    }

    public void setRolloverAppearance(PDAppearanceEntry entry) {
        this.dictionary.setItem(COSName.R, entry);
    }

    public void setRolloverAppearance(PDAppearanceStream ap) {
        this.dictionary.setItem(COSName.R, ap);
    }

    public PDAppearanceEntry getDownAppearance() {
        COSBase entry = this.dictionary.getDictionaryObject(COSName.D);
        if (entry instanceof COSDictionary) {
            return new PDAppearanceEntry(entry);
        }
        return getNormalAppearance();
    }

    public void setDownAppearance(PDAppearanceEntry entry) {
        this.dictionary.setItem(COSName.D, entry);
    }

    public void setDownAppearance(PDAppearanceStream ap) {
        this.dictionary.setItem(COSName.D, ap);
    }
}
