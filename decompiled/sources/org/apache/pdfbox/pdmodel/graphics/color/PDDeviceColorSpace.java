package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDDeviceColorSpace.class */
public abstract class PDDeviceColorSpace extends PDColorSpace {
    public String toString() {
        return getName();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace, org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return COSName.getPDFName(getName());
    }
}
