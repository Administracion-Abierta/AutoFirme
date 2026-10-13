package org.apache.pdfbox.pdmodel.graphics.form;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/form/PDTransparencyGroupAttributes.class */
public final class PDTransparencyGroupAttributes implements COSObjectable {
    private final COSDictionary dictionary;
    private PDColorSpace colorSpace;

    public PDTransparencyGroupAttributes() {
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.S, (COSBase) COSName.TRANSPARENCY);
    }

    public PDTransparencyGroupAttributes(COSDictionary dic) {
        this.dictionary = dic;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public PDColorSpace getColorSpace() throws IOException {
        return getColorSpace(null);
    }

    public PDColorSpace getColorSpace(PDResources resources) throws IOException {
        if (this.colorSpace == null && getCOSObject().containsKey(COSName.CS)) {
            this.colorSpace = PDColorSpace.create(getCOSObject().getDictionaryObject(COSName.CS), resources);
        }
        return this.colorSpace;
    }

    public boolean isIsolated() {
        return getCOSObject().getBoolean(COSName.I, false);
    }

    public boolean isKnockout() {
        return getCOSObject().getBoolean(COSName.K, false);
    }
}
