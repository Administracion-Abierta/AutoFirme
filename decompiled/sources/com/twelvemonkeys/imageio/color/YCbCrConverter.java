package com.twelvemonkeys.imageio.color;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/YCbCrConverter.class */
public final class YCbCrConverter {
    private static final int SCALEBITS = 16;
    private static final int MAXJSAMPLE = 255;
    private static final int CENTERJSAMPLE = 128;
    private static final int ONE_HALF = 32768;

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/YCbCrConverter$JPEG.class */
    private static final class JPEG {
        private static final int[] Cr_R_LUT = new int[256];
        private static final int[] Cb_B_LUT = new int[256];
        private static final int[] Cr_G_LUT = new int[256];
        private static final int[] Cb_G_LUT = new int[256];

        private JPEG() {
        }

        static {
            buildYCCtoRGBtable();
        }

        private static void buildYCCtoRGBtable() {
            if (ColorSpaces.DEBUG) {
                System.err.println("Building JPEG YCbCr conversion table");
            }
            int i = 0;
            int i2 = -128;
            while (i <= 255) {
                Cr_R_LUT[i] = ((int) ((91881.972d * ((double) i2)) + 32768.0d)) >> 16;
                Cb_B_LUT[i] = ((int) ((116130.292d * ((double) i2)) + 32768.0d)) >> 16;
                Cr_G_LUT[i] = (-46802) * i2;
                Cb_G_LUT[i] = ((-22554) * i2) + 32768;
                i++;
                i2++;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/YCbCrConverter$ITU_R_601.class */
    private static final class ITU_R_601 {
        private static final int[] Cr_R_LUT = new int[256];
        private static final int[] Cb_B_LUT = new int[256];
        private static final int[] Cr_G_LUT = new int[256];
        private static final int[] Cb_G_LUT = new int[256];
        private static final int[] Y_LUT = new int[256];

        private ITU_R_601() {
        }

        static {
            buildYCCtoRGBtable();
        }

        private static void buildYCCtoRGBtable() {
            if (ColorSpaces.DEBUG) {
                System.err.println("Building ITU-R REC.601 YCbCr conversion table");
            }
            int i = 0;
            int i2 = -128;
            while (i <= 255) {
                Cr_R_LUT[i] = ((104597 * i2) + 32768) >> 16;
                Cb_B_LUT[i] = ((132201 * i2) + 32768) >> 16;
                Cr_G_LUT[i] = (-53279) * i2;
                Cb_G_LUT[i] = ((-25674) * i2) + 32768;
                Y_LUT[i] = ((76309 * (i - 16)) + 32768) >> 16;
                i++;
                i2++;
            }
        }
    }

    public static void convertYCbCr2RGB(byte[] bArr, byte[] bArr2, double[] dArr, double[] dArr2, int i) {
        double d;
        double d2;
        double d3;
        if (dArr2 == null) {
            d = bArr[i] & 255;
            d2 = (bArr[i + 1] & 255) - 128;
            d3 = (bArr[i + 2] & 255) - 128;
        } else {
            d = ((((double) (bArr[i] & 255)) - dArr2[0]) * 255.0d) / (dArr2[1] - dArr2[0]);
            d2 = ((((double) (bArr[i + 1] & 255)) - dArr2[2]) * 127.0d) / (dArr2[3] - dArr2[2]);
            d3 = ((((double) (bArr[i + 2] & 255)) - dArr2[4]) * 127.0d) / (dArr2[5] - dArr2[4]);
        }
        double d4 = dArr[0];
        double d5 = dArr[1];
        double d6 = dArr[2];
        int iRound = (int) Math.round((d3 * (2.0d - (2.0d * d4))) + d);
        int iRound2 = (int) Math.round((d2 * (2.0d - (2.0d * d6))) + d);
        int iRound3 = (int) Math.round(((d - (d4 * ((double) iRound))) - (d6 * ((double) iRound2))) / d5);
        bArr2[i] = clamp(iRound);
        bArr2[i + 2] = clamp(iRound2);
        bArr2[i + 1] = clamp(iRound3);
    }

    public static void convertJPEGYCbCr2RGB(byte[] bArr, byte[] bArr2, int i) {
        int i2 = bArr[i] & 255;
        int i3 = bArr[i + 1] & 255;
        int i4 = bArr[i + 2] & 255;
        bArr2[i] = clamp(i2 + JPEG.Cr_R_LUT[i4]);
        bArr2[i + 1] = clamp(i2 + ((JPEG.Cb_G_LUT[i3] + JPEG.Cr_G_LUT[i4]) >> 16));
        bArr2[i + 2] = clamp(i2 + JPEG.Cb_B_LUT[i3]);
    }

    public static void convertRec601YCbCr2RGB(byte[] bArr, byte[] bArr2, int i) {
        int i2 = bArr[i] & 255;
        int i3 = bArr[i + 1] & 255;
        int i4 = bArr[i + 2] & 255;
        bArr2[i] = clamp(ITU_R_601.Y_LUT[i2] + ITU_R_601.Cr_R_LUT[i4]);
        bArr2[i + 1] = clamp(ITU_R_601.Y_LUT[i2] + ((ITU_R_601.Cr_G_LUT[i4] + ITU_R_601.Cb_G_LUT[i3]) >> 16));
        bArr2[i + 2] = clamp(ITU_R_601.Y_LUT[i2] + ITU_R_601.Cb_B_LUT[i3]);
    }

    private static byte clamp(int i) {
        return (byte) Math.max(0, Math.min(255, i));
    }
}
