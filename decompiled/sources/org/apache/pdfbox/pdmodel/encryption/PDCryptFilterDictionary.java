package org.apache.pdfbox.pdmodel.encryption;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/PDCryptFilterDictionary.class */
public class PDCryptFilterDictionary implements COSObjectable {
    protected COSDictionary cryptFilterDictionary;

    public PDCryptFilterDictionary() {
        this.cryptFilterDictionary = null;
        this.cryptFilterDictionary = new COSDictionary();
    }

    public PDCryptFilterDictionary(COSDictionary d) {
        this.cryptFilterDictionary = null;
        this.cryptFilterDictionary = d;
    }

    @Deprecated
    public COSDictionary getCOSDictionary() {
        return this.cryptFilterDictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.cryptFilterDictionary;
    }

    public void setLength(int length) {
        this.cryptFilterDictionary.setInt(COSName.LENGTH, length);
    }

    public int getLength() {
        return this.cryptFilterDictionary.getInt(COSName.LENGTH, 40);
    }

    public void setCryptFilterMethod(COSName cfm) {
        this.cryptFilterDictionary.setItem(COSName.CFM, (COSBase) cfm);
    }

    public COSName getCryptFilterMethod() {
        return (COSName) this.cryptFilterDictionary.getDictionaryObject(COSName.CFM);
    }

    public boolean isEncryptMetaData() {
        COSBase value = getCOSObject().getDictionaryObject(COSName.ENCRYPT_META_DATA);
        if (value instanceof COSBoolean) {
            return ((COSBoolean) value).getValue();
        }
        return true;
    }

    public void setEncryptMetaData(boolean encryptMetaData) {
        getCOSObject().setBoolean(COSName.ENCRYPT_META_DATA, encryptMetaData);
    }
}
