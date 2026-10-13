package org.apache.pdfbox.pdmodel.common;

import com.aowagie.text.pdf.ColumnText;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSNumber;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDRange.class */
public class PDRange implements COSObjectable {
    private COSArray rangeArray;
    private int startingIndex;

    public PDRange() {
        this.rangeArray = new COSArray();
        this.rangeArray.add((COSBase) new COSFloat(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
        this.rangeArray.add((COSBase) new COSFloat(1.0f));
        this.startingIndex = 0;
    }

    public PDRange(COSArray range) {
        this.rangeArray = range;
    }

    public PDRange(COSArray range, int index) {
        this.rangeArray = range;
        this.startingIndex = index;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.rangeArray;
    }

    public COSArray getCOSArray() {
        return this.rangeArray;
    }

    public float getMin() {
        COSNumber min = (COSNumber) this.rangeArray.getObject(this.startingIndex * 2);
        return min.floatValue();
    }

    public void setMin(float min) {
        this.rangeArray.set(this.startingIndex * 2, (COSBase) new COSFloat(min));
    }

    public float getMax() {
        COSNumber max = (COSNumber) this.rangeArray.getObject((this.startingIndex * 2) + 1);
        return max.floatValue();
    }

    public void setMax(float max) {
        this.rangeArray.set((this.startingIndex * 2) + 1, (COSBase) new COSFloat(max));
    }

    public String toString() {
        return "PDRange{" + getMin() + ", " + getMax() + '}';
    }
}
