package com.twelvemonkeys.image;

import com.aowagie.text.pdf.ColumnText;
import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.WinError;
import java.awt.image.RGBImageFilter;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/GrayFilter.class */
public class GrayFilter extends RGBImageFilter {
    private int low;
    private float range;

    public GrayFilter() {
        this.canFilterIndexColorModel = true;
        this.low = 0;
        this.range = 1.0f;
    }

    public GrayFilter(float f, float f2) {
        this.canFilterIndexColorModel = true;
        this.low = 0;
        this.range = 1.0f;
        f = f > f2 ? 0.0f : f;
        if (f < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        if (f2 < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            f2 = 0.0f;
        } else if (f2 > 1.0f) {
            f2 = 1.0f;
        }
        this.low = (int) (f * 255.0f);
        this.range = f2 - f;
    }

    public GrayFilter(int i, int i2) {
        this(i / 255.0f, i2 / 255.0f);
    }

    public int filterRGB(int i, int i2, int i3) {
        int i4 = (((WinError.ERROR_BAD_FILE_TYPE * ((i3 >> 16) & 255)) + (WinError.ERROR_RECEIVE_PARTIAL * ((i3 >> 8) & 255))) + (71 * (i3 & 255))) / 1000;
        if (this.range != 1.0f) {
            i4 = this.low + ((int) (i4 * this.range));
        }
        return (i3 & Ddeml.MF_MASK) | (i4 << 16) | (i4 << 8) | i4;
    }
}
