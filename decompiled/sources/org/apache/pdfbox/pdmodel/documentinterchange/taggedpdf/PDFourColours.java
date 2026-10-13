package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/taggedpdf/PDFourColours.class */
public class PDFourColours implements COSObjectable {
    private final COSArray array;

    public PDFourColours() {
        this.array = new COSArray();
        this.array.add((COSBase) COSNull.NULL);
        this.array.add((COSBase) COSNull.NULL);
        this.array.add((COSBase) COSNull.NULL);
        this.array.add((COSBase) COSNull.NULL);
    }

    public PDFourColours(COSArray array) {
        this.array = array;
        if (this.array.size() < 4) {
            for (int i = this.array.size() - 1; i < 4; i++) {
                this.array.add((COSBase) COSNull.NULL);
            }
        }
    }

    public PDGamma getBeforeColour() {
        return getColourByIndex(0);
    }

    public void setBeforeColour(PDGamma colour) {
        setColourByIndex(0, colour);
    }

    public PDGamma getAfterColour() {
        return getColourByIndex(1);
    }

    public void setAfterColour(PDGamma colour) {
        setColourByIndex(1, colour);
    }

    public PDGamma getStartColour() {
        return getColourByIndex(2);
    }

    public void setStartColour(PDGamma colour) {
        setColourByIndex(2, colour);
    }

    public PDGamma getEndColour() {
        return getColourByIndex(3);
    }

    public void setEndColour(PDGamma colour) {
        setColourByIndex(3, colour);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.array;
    }

    private PDGamma getColourByIndex(int index) {
        PDGamma retval = null;
        COSBase item = this.array.getObject(index);
        if (item instanceof COSArray) {
            retval = new PDGamma((COSArray) item);
        }
        return retval;
    }

    private void setColourByIndex(int index, PDGamma colour) {
        COSBase base;
        if (colour == null) {
            base = COSNull.NULL;
        } else {
            base = colour.getCOSArray();
        }
        this.array.set(index, base);
    }
}
