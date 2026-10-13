package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDSpecialColorSpace.class */
public abstract class PDSpecialColorSpace extends PDColorSpace {
    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace, org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.array;
    }
}
