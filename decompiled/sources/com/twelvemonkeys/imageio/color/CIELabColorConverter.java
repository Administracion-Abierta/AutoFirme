package com.twelvemonkeys.imageio.color;

import com.aowagie.text.pdf.ColumnText;
import com.twelvemonkeys.lang.Validate;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/CIELabColorConverter.class */
public final class CIELabColorConverter {
    private final float[] whitePoint;

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/CIELabColorConverter$Illuminant.class */
    public enum Illuminant {
        D50(new float[]{96.4212f, 100.0f, 82.5188f}),
        D65(new float[]{95.0429f, 100.0f, 108.89f});

        private final float[] whitePoint;

        Illuminant(float[] fArr) {
            this.whitePoint = (float[]) Validate.isTrue(fArr != null && fArr.length == 3, fArr, "Bad white point definition: %s");
        }

        public float[] getWhitePoint() {
            return this.whitePoint;
        }
    }

    public CIELabColorConverter(Illuminant illuminant) {
        this.whitePoint = ((Illuminant) Validate.notNull(illuminant, "illuminant")).getWhitePoint();
    }

    private float clamp(float f) {
        if (f < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        if (f > 255.0f) {
            return 255.0f;
        }
        return f;
    }

    public void toRGB(float f, float f2, float f3, float[] fArr) {
        XYZtoRGB(LABtoXYZ(f, f2, f3, fArr), fArr);
    }

    private float[] LABtoXYZ(float f, float f2, float f3, float[] fArr) {
        float f4;
        float f5;
        float f6;
        float f7 = (f + 16.0f) / 116.0f;
        float f8 = f7 * f7 * f7;
        float f9 = (f2 / 500.0f) + f7;
        float f10 = f9 * f9 * f9;
        float f11 = f7 - (f3 / 200.0f);
        float f12 = f11 * f11 * f11;
        if (f8 > 0.008856f) {
            f4 = f8;
        } else {
            f4 = (f7 - 0.13793103f) / 7.787f;
        }
        if (f10 > 0.008856f) {
            f5 = f10;
        } else {
            f5 = (f9 - 0.13793103f) / 7.787f;
        }
        if (f12 > 0.008856f) {
            f6 = f12;
        } else {
            f6 = (f11 - 0.13793103f) / 7.787f;
        }
        fArr[0] = f5 * this.whitePoint[0];
        fArr[1] = f4 * this.whitePoint[1];
        fArr[2] = f6 * this.whitePoint[2];
        return fArr;
    }

    private float[] XYZtoRGB(float[] fArr, float[] fArr2) {
        return XYZtoRGB(fArr[0], fArr[1], fArr[2], fArr2);
    }

    private float[] XYZtoRGB(float f, float f2, float f3, float[] fArr) {
        float fPow;
        float fPow2;
        float fPow3;
        float f4 = f / 100.0f;
        float f5 = f2 / 100.0f;
        float f6 = f3 / 100.0f;
        float f7 = (f4 * 3.2406f) + (f5 * (-1.5372f)) + (f6 * (-0.4986f));
        float f8 = (f4 * (-0.9689f)) + (f5 * 1.8758f) + (f6 * 0.0415f);
        float f9 = (f4 * 0.0557f) + (f5 * (-0.204f)) + (f6 * 1.057f);
        if (f7 > 0.0031308f) {
            fPow = (1.055f * ((float) pow(f7, 0.4166666666666667d))) - 0.055f;
        } else {
            fPow = f7 * 12.92f;
        }
        if (f8 > 0.0031308f) {
            fPow2 = (1.055f * ((float) pow(f8, 0.4166666666666667d))) - 0.055f;
        } else {
            fPow2 = f8 * 12.92f;
        }
        if (f9 > 0.0031308f) {
            fPow3 = (1.055f * ((float) pow(f9, 0.4166666666666667d))) - 0.055f;
        } else {
            fPow3 = f9 * 12.92f;
        }
        fArr[0] = clamp(fPow * 255.0f);
        fArr[1] = clamp(fPow2 * 255.0f);
        fArr[2] = clamp(fPow3 * 255.0f);
        return fArr;
    }

    static double pow(double d, double d2) {
        return Double.longBitsToDouble(((long) (d2 * (Double.doubleToLongBits(d) - 4606921280493453312L))) + 4606921280493453312L);
    }
}
