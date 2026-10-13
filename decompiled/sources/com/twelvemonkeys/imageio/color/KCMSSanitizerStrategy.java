package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.lang.Validate;
import java.awt.color.ICC_Profile;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/KCMSSanitizerStrategy.class */
final class KCMSSanitizerStrategy implements ICCProfileSanitizer {
    private static final int CORBIS_RGB_ALTERNATE_XYZ = 396690872;

    KCMSSanitizerStrategy() {
    }

    @Override // com.twelvemonkeys.imageio.color.ICCProfileSanitizer
    public void fixProfile(ICC_Profile iCC_Profile) {
        Validate.notNull(iCC_Profile, "profile");
        byte[] data = iCC_Profile.getData(1751474532);
        if (intFromBigEndian(data, 64) != 0) {
            intToBigEndian(0, data, 64);
            iCC_Profile.setData(1751474532, data);
        }
        if (fixProfileXYZTag(iCC_Profile, 2004119668)) {
            fixProfileXYZTag(iCC_Profile, 1918392666);
            fixProfileXYZTag(iCC_Profile, 1733843290);
            fixProfileXYZTag(iCC_Profile, 1649957210);
        }
    }

    @Override // com.twelvemonkeys.imageio.color.ICCProfileSanitizer
    public boolean validationAltersProfileHeader() {
        return false;
    }

    private static boolean fixProfileXYZTag(ICC_Profile iCC_Profile, int i) {
        byte[] data = iCC_Profile.getData(i);
        if (data != null && intFromBigEndian(data, 0) == CORBIS_RGB_ALTERNATE_XYZ) {
            intToBigEndian(1482250784, data, 0);
            iCC_Profile.setData(i, data);
            return true;
        }
        return false;
    }

    private static int intFromBigEndian(byte[] bArr, int i) {
        return ((bArr[i] & 255) << 24) | ((bArr[i + 1] & 255) << 16) | ((bArr[i + 2] & 255) << 8) | (bArr[i + 3] & 255);
    }

    private static void intToBigEndian(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) (i >> 24);
        bArr[i2 + 1] = (byte) (i >> 16);
        bArr[i2 + 2] = (byte) (i >> 8);
        bArr[i2 + 3] = (byte) i;
    }
}
