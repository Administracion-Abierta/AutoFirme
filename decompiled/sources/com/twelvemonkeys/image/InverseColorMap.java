package com.twelvemonkeys.image;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/image/InverseColorMap.class */
class InverseColorMap {
    static final int QUANTBITS = 5;
    static final int TRUNCBITS = 3;
    static final int QUANTMASK_BLUE = 31;
    static final int QUANTMASK_GREEN = 992;
    static final int QUANTMASK_RED = 31744;
    static final int MAXQUANTVAL = 32;
    byte[] rgbMapByte;
    int[] rgbMapInt;
    int numColors;
    int maxColor;
    byte[] inverseRGB;
    int transparentIndex;

    InverseColorMap(byte[] bArr) {
        this(bArr, -1);
    }

    InverseColorMap(int[] iArr) {
        this(iArr, -1);
    }

    InverseColorMap(byte[] bArr, int i) {
        this.transparentIndex = -1;
        this.rgbMapByte = bArr;
        this.numColors = this.rgbMapByte.length / 4;
        this.transparentIndex = i;
        this.inverseRGB = new byte[32768];
        initIRGB(new int[32768]);
    }

    InverseColorMap(int[] iArr, int i) {
        this.transparentIndex = -1;
        this.rgbMapInt = iArr;
        this.numColors = this.rgbMapInt.length;
        this.transparentIndex = i;
        this.inverseRGB = new byte[32768];
        initIRGB(new int[32768]);
    }

    void initIRGB(int[] iArr) {
        int i;
        int i2;
        int i3;
        for (int i4 = 0; i4 < this.numColors; i4++) {
            if (i4 != this.transparentIndex) {
                if (this.rgbMapByte != null) {
                    i = this.rgbMapByte[i4 * 4] & 255;
                    i2 = this.rgbMapByte[(i4 * 4) + 1] & 255;
                    i3 = this.rgbMapByte[(i4 * 4) + 2] & 255;
                } else if (this.rgbMapInt != null) {
                    i = (this.rgbMapInt[i4] >> 16) & 255;
                    i2 = (this.rgbMapInt[i4] >> 8) & 255;
                    i3 = this.rgbMapInt[i4] & 255;
                } else {
                    throw new IllegalStateException("colormap == null");
                }
                int i5 = i - 4;
                int i6 = i2 - 4;
                int i7 = i3 - 4;
                int i8 = (i5 * i5) + (i6 * i6) + (i7 * i7);
                int i9 = 2 * (64 - (i2 << 3));
                int i10 = 2 * (64 - (i3 << 3));
                int i11 = 0;
                int i12 = 0;
                int i13 = 2 * (64 - (i << 3));
                while (i12 < 32) {
                    int i14 = 0;
                    int i15 = i8;
                    int i16 = i9;
                    while (i14 < 32) {
                        int i17 = 0;
                        int i18 = i15;
                        int i19 = i10;
                        while (i17 < 32) {
                            if (i4 == 0 || iArr[i11] > i18) {
                                iArr[i11] = i18;
                                this.inverseRGB[i11] = (byte) i4;
                            }
                            i18 += i19;
                            i17++;
                            i11++;
                            i19 += 128;
                        }
                        i15 += i16;
                        i14++;
                        i16 += 128;
                    }
                    i8 += i13;
                    i12++;
                    i13 += 128;
                }
            }
        }
    }

    public final int getIndexNearest(int i) {
        return this.inverseRGB[((i >> 9) & QUANTMASK_RED) + ((i >> 6) & QUANTMASK_GREEN) + ((i >> 3) & 31)] & 255;
    }

    public final int getIndexNearest(int i, int i2, int i3) {
        return this.inverseRGB[((i << 7) & QUANTMASK_RED) + ((i2 << 2) & QUANTMASK_GREEN) + ((i3 >> 3) & 31)] & 255;
    }
}
