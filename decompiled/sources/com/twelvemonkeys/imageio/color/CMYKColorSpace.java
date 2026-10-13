package com.twelvemonkeys.imageio.color;

import java.awt.color.ColorSpace;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/CMYKColorSpace.class */
final class CMYKColorSpace extends ColorSpace {
    static final ColorSpace INSTANCE = new CMYKColorSpace();
    final ColorSpace sRGB;

    private CMYKColorSpace() {
        super(9, 4);
        this.sRGB = getInstance(1000);
    }

    public static ColorSpace getInstance() {
        return INSTANCE;
    }

    public float[] toRGB(float[] fArr) {
        return new float[]{(1.0f - fArr[0]) * (1.0f - fArr[3]), (1.0f - fArr[1]) * (1.0f - fArr[3]), (1.0f - fArr[2]) * (1.0f - fArr[3])};
    }

    public float[] fromRGB(float[] fArr) {
        float f = 1.0f - fArr[0];
        float f2 = 1.0f - fArr[1];
        float f3 = 1.0f - fArr[2];
        float fMin = Math.min(f, Math.min(f2, f3));
        return new float[]{f - fMin, f2 - fMin, f3 - fMin, fMin};
    }

    public float[] toCIEXYZ(float[] fArr) {
        return this.sRGB.toCIEXYZ(toRGB(fArr));
    }

    public float[] fromCIEXYZ(float[] fArr) {
        return this.sRGB.fromCIEXYZ(fromRGB(fArr));
    }
}
