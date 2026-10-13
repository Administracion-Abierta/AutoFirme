package com.twelvemonkeys.imageio.color;

import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/Int16ComponentColorModel.class */
public final class Int16ComponentColorModel extends ComponentColorModel {
    private final ComponentColorModel delegate;

    public Int16ComponentColorModel(ColorSpace colorSpace, boolean z, boolean z2) {
        super(colorSpace, z, z2, z ? 3 : 1, 2);
        this.delegate = new ComponentColorModel(colorSpace, z, z2, z ? 3 : 1, 1);
    }

    private void remap(short[] sArr, int i) {
        short s = sArr[i];
        if (s < 0) {
            sArr[i] = (short) (s - Short.MIN_VALUE);
        } else {
            sArr[i] = (short) (s - Short.MIN_VALUE);
        }
    }

    public int getRed(Object obj) {
        remap((short[]) obj, 0);
        return this.delegate.getRed(obj);
    }

    public int getGreen(Object obj) {
        remap((short[]) obj, 1);
        return this.delegate.getGreen(obj);
    }

    public int getBlue(Object obj) {
        remap((short[]) obj, 2);
        return this.delegate.getBlue(obj);
    }
}
