package org.apache.pdfbox.pdmodel.graphics;

import java.util.Arrays;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/PDLineDashPattern.class */
public final class PDLineDashPattern implements COSObjectable {
    private final int phase;
    private final float[] array;

    public PDLineDashPattern() {
        this.array = new float[0];
        this.phase = 0;
    }

    public PDLineDashPattern(COSArray array, int phase) {
        this.array = array.toFloatArray();
        this.phase = phase;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        COSArray cos = new COSArray();
        COSArray patternArray = new COSArray();
        patternArray.setFloatArray(this.array);
        cos.add((COSBase) patternArray);
        cos.add(COSInteger.get(this.phase));
        return cos;
    }

    public int getPhase() {
        return this.phase;
    }

    public float[] getDashArray() {
        return (float[]) this.array.clone();
    }

    public String toString() {
        return "PDLineDashPattern{array=" + Arrays.toString(this.array) + ", phase=" + this.phase + "}";
    }
}
