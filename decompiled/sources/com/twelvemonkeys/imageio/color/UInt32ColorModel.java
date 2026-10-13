package com.twelvemonkeys.imageio.color;

import com.aowagie.text.pdf.ColumnText;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/UInt32ColorModel.class */
public final class UInt32ColorModel extends ComponentColorModel {
    public UInt32ColorModel(ColorSpace colorSpace, boolean z, boolean z2) {
        super(colorSpace, z, z2, z ? 3 : 1, 3);
    }

    public float[] getNormalizedComponents(Object obj, float[] fArr, int i) {
        int numComponents = getNumComponents();
        if (fArr == null) {
            fArr = new float[numComponents + i];
        }
        int[] iArr = (int[]) obj;
        int i2 = 0;
        int i3 = i;
        while (i2 < numComponents) {
            fArr[i3] = (((long) iArr[i2]) & 4294967295L) / ((1 << getComponentSize(i2)) - 1);
            i2++;
            i3++;
        }
        int numColorComponents = getNumColorComponents();
        if (hasAlpha() && isAlphaPremultiplied()) {
            float f = fArr[numColorComponents + i];
            if (f != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                float f2 = 1.0f / f;
                for (int i4 = i; i4 < numColorComponents + i; i4++) {
                    float[] fArr2 = fArr;
                    int i5 = i4;
                    fArr2[i5] = fArr2[i5] * f2;
                }
            }
        }
        return fArr;
    }
}
