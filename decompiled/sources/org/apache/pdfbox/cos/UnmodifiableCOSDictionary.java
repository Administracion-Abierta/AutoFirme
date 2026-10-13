package org.apache.pdfbox.cos;

import java.util.Calendar;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/UnmodifiableCOSDictionary.class */
final class UnmodifiableCOSDictionary extends COSDictionary {
    UnmodifiableCOSDictionary(COSDictionary dict) {
        this.items = dict.items;
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setItem(COSName key, COSBase value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setItem(COSName key, COSObjectable value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setItem(String key, COSObjectable value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setBoolean(String key, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setBoolean(COSName key, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setItem(String key, COSBase value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setName(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setName(COSName key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setDate(String key, Calendar date) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setDate(COSName key, Calendar date) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setEmbeddedDate(String embedded, String key, Calendar date) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setEmbeddedDate(String embedded, COSName key, Calendar date) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setString(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setString(COSName key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setEmbeddedString(String embedded, String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setEmbeddedString(String embedded, COSName key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setInt(String key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setInt(COSName key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setLong(String key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setLong(COSName key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setEmbeddedInt(String embeddedDictionary, String key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setEmbeddedInt(String embeddedDictionary, COSName key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setFloat(String key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setFloat(COSName key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void removeItem(COSName key) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void addAll(COSDictionary dic) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void mergeInto(COSDictionary dic) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary
    public void setFlag(COSName field, int bitFlag, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.cos.COSDictionary, org.apache.pdfbox.cos.COSUpdateInfo
    public void setNeedToBeUpdated(boolean flag) {
        throw new UnsupportedOperationException();
    }
}
