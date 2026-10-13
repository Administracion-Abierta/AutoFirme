package com.twelvemonkeys.image;

import com.aowagie.text.pdf.ColumnText;
import com.sun.jna.platform.win32.Ddeml;
import java.awt.image.RGBImageFilter;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/BrightnessContrastFilter.class */
public class BrightnessContrastFilter extends RGBImageFilter {
    private final int[] LUT;

    public BrightnessContrastFilter() {
        this(0.3f, 0.3f);
    }

    public BrightnessContrastFilter(float f, float f2) {
        this.canFilterIndexColorModel = true;
        this.LUT = createLUT(f, f2);
    }

    private static int[] createLUT(float f, float f2) {
        int[] iArr = new int[256];
        double dPow = f2 > ColumnText.GLOBAL_SPACE_CHAR_RATIO ? Math.pow(f2, 7.0d) * 127.0d : f2;
        double d = ((double) f) + 1.0d;
        for (int i = 0; i < 256; i++) {
            iArr[i] = clamp((int) ((127.5d * d) + (((double) (i - 127)) * (dPow + 1.0d))));
        }
        if (f2 == 1.0f) {
            iArr[127] = iArr[126];
        }
        return iArr;
    }

    private static int clamp(int i) {
        if (i < 0) {
            return 0;
        }
        if (i > 255) {
            return 255;
        }
        return i;
    }

    public int filterRGB(int i, int i2, int i3) {
        int i4 = this.LUT[(i3 >> 16) & 255];
        int i5 = this.LUT[(i3 >> 8) & 255];
        return (i3 & Ddeml.MF_MASK) | (i4 << 16) | (i5 << 8) | this.LUT[i3 & 255];
    }
}
