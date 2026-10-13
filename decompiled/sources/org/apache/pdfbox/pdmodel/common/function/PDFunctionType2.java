package org.apache.pdfbox.pdmodel.common.function;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/function/PDFunctionType2.class */
public class PDFunctionType2 extends PDFunction {
    private final COSArray c0;
    private final COSArray c1;
    private final float exponent;

    public PDFunctionType2(COSBase function) {
        super(function);
        COSArray cosArray0 = getCOSObject().getCOSArray(COSName.C0);
        if (cosArray0 != null) {
            this.c0 = cosArray0;
        } else {
            this.c0 = new COSArray();
        }
        if (this.c0.size() == 0) {
            this.c0.add((COSBase) new COSFloat(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
        }
        COSArray cosArray1 = getCOSObject().getCOSArray(COSName.C1);
        if (cosArray1 != null) {
            this.c1 = cosArray1;
        } else {
            this.c1 = new COSArray();
        }
        if (this.c1.size() == 0) {
            this.c1.add((COSBase) new COSFloat(1.0f));
        }
        this.exponent = getCOSObject().getFloat(COSName.N);
    }

    @Override // org.apache.pdfbox.pdmodel.common.function.PDFunction
    public int getFunctionType() {
        return 2;
    }

    @Override // org.apache.pdfbox.pdmodel.common.function.PDFunction
    public float[] eval(float[] input) throws IOException {
        float xToN = (float) Math.pow(input[0], this.exponent);
        float[] result = new float[Math.min(this.c0.size(), this.c1.size())];
        for (int j = 0; j < result.length; j++) {
            float c0j = ((COSNumber) this.c0.get(j)).floatValue();
            float c1j = ((COSNumber) this.c1.get(j)).floatValue();
            result[j] = c0j + (xToN * (c1j - c0j));
        }
        return clipToRange(result);
    }

    public COSArray getC0() {
        return this.c0;
    }

    public COSArray getC1() {
        return this.c1;
    }

    public float getN() {
        return this.exponent;
    }

    @Override // org.apache.pdfbox.pdmodel.common.function.PDFunction
    public String toString() {
        return "FunctionType2{C0: " + getC0() + " C1: " + getC1() + " N: " + getN() + "}";
    }
}
