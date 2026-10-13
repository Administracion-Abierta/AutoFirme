package org.apache.pdfbox.pdmodel.graphics.color;

import com.aowagie.text.pdf.ColumnText;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/color/PDCalGray.class */
public final class PDCalGray extends PDCIEDictionaryBasedColorSpace {
    private final PDColor initialColor;
    private final Map<Float, float[]> map1;

    public PDCalGray() {
        super(COSName.CALGRAY);
        this.initialColor = new PDColor(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO}, this);
        this.map1 = new HashMap();
    }

    public PDCalGray(COSArray array) {
        super(array);
        this.initialColor = new PDColor(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO}, this);
        this.map1 = new HashMap();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public String getName() {
        return COSName.CALGRAY.getName();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public int getNumberOfComponents() {
        return 1;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f};
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace
    public float[] toRGB(float[] fArr) {
        if (this.wpX == 1.0f && this.wpY == 1.0f && this.wpZ == 1.0f) {
            float f = fArr[0];
            float[] fArr2 = this.map1.get(Float.valueOf(f));
            if (fArr2 != null) {
                return (float[]) fArr2.clone();
            }
            float fPow = (float) Math.pow(f, getGamma());
            float[] fArrConvXYZtoRGB = convXYZtoRGB(fPow, fPow, fPow);
            this.map1.put(Float.valueOf(f), (float[]) fArrConvXYZtoRGB.clone());
            return fArrConvXYZtoRGB;
        }
        return new float[]{fArr[0], fArr[0], fArr[0]};
    }

    public float getGamma() {
        float retval = 1.0f;
        COSNumber gamma = (COSNumber) this.dictionary.getDictionaryObject(COSName.GAMMA);
        if (gamma != null) {
            retval = gamma.floatValue();
        }
        return retval;
    }

    public void setGamma(float value) {
        this.dictionary.setItem(COSName.GAMMA, (COSBase) new COSFloat(value));
    }
}
