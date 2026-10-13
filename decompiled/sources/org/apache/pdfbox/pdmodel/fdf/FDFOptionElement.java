package org.apache.pdfbox.pdmodel.fdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFOptionElement.class */
public class FDFOptionElement implements COSObjectable {
    private final COSArray option;

    public FDFOptionElement() {
        this.option = new COSArray();
        this.option.add((COSBase) new COSString(""));
        this.option.add((COSBase) new COSString(""));
    }

    public FDFOptionElement(COSArray o) {
        this.option = o;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.option;
    }

    public COSArray getCOSArray() {
        return this.option;
    }

    public String getOption() {
        return ((COSString) this.option.getObject(0)).getString();
    }

    public void setOption(String opt) {
        this.option.set(0, (COSBase) new COSString(opt));
    }

    public String getDefaultAppearanceString() {
        return ((COSString) this.option.getObject(1)).getString();
    }

    public void setDefaultAppearanceString(String da) {
        this.option.set(1, (COSBase) new COSString(da));
    }
}
