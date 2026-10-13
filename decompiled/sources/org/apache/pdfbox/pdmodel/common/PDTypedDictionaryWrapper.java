package org.apache.pdfbox.pdmodel.common;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDTypedDictionaryWrapper.class */
public class PDTypedDictionaryWrapper extends PDDictionaryWrapper {
    public PDTypedDictionaryWrapper(String type) {
        getCOSObject().setName(COSName.TYPE, type);
    }

    public PDTypedDictionaryWrapper(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getType() {
        return getCOSObject().getNameAsString(COSName.TYPE);
    }
}
