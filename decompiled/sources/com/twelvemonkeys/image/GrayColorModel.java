package com.twelvemonkeys.image;

import java.awt.image.IndexColorModel;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/GrayColorModel.class */
public class GrayColorModel extends IndexColorModel {
    private static final byte[] sGrays = createGrayScale();

    public GrayColorModel() {
        super(8, sGrays.length, sGrays, sGrays, sGrays);
    }

    private static byte[] createGrayScale() {
        byte[] bArr = new byte[256];
        for (int i = 0; i < 256; i++) {
            bArr[i] = (byte) i;
        }
        return bArr;
    }
}
