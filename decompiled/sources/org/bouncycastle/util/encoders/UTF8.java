package org.bouncycastle.util.encoders;

import com.sun.jna.platform.win32.WinUser;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/encoders/UTF8.class */
public class UTF8 {
    private static final byte C_ILL = 0;
    private static final byte C_CR1 = 1;
    private static final byte C_CR2 = 2;
    private static final byte C_CR3 = 3;
    private static final byte C_L2A = 4;
    private static final byte C_L3A = 5;
    private static final byte C_L3B = 6;
    private static final byte C_L3C = 7;
    private static final byte C_L4A = 8;
    private static final byte C_L4B = 9;
    private static final byte C_L4C = 10;
    private static final byte S_ERR = -2;
    private static final byte S_END = -1;
    private static final byte S_CS1 = 0;
    private static final byte S_CS2 = 16;
    private static final byte S_CS3 = 32;
    private static final byte S_P3A = 48;
    private static final byte S_P3B = 64;
    private static final byte S_P4A = 80;
    private static final byte S_P4B = 96;
    private static final short[] firstUnitTable = new short[128];
    private static final byte[] transitionTable = new byte[112];

    private static void fill(byte[] bArr, int i, int i2, byte b) {
        for (int i3 = i; i3 <= i2; i3++) {
            bArr[i3] = b;
        }
    }

    public static int transcodeToUTF16(byte[] bArr, char[] cArr) {
        return transcodeToUTF16(bArr, 0, bArr.length, cArr);
    }

    public static int transcodeToUTF16(byte[] bArr, int i, int i2, char[] cArr) {
        int i3 = i;
        int i4 = 0;
        int i5 = i + i2;
        while (i3 < i5) {
            int i6 = i3;
            i3++;
            byte b = bArr[i6];
            if (b < 0) {
                short s = firstUnitTable[b & 127];
                int i7 = s >>> 8;
                byte b2 = (byte) s;
                while (true) {
                    byte b3 = b2;
                    if (b3 < 0) {
                        if (b3 != -2) {
                            if (i7 > 65535) {
                                if (i4 < cArr.length - 1) {
                                    int i8 = i4;
                                    int i9 = i4 + 1;
                                    cArr[i8] = (char) (55232 + (i7 >>> 10));
                                    i4 = i9 + 1;
                                    cArr[i9] = (char) (56320 | (i7 & WinUser.CF_GDIOBJLAST));
                                    break;
                                }
                                return -1;
                            }
                            if (i4 < cArr.length) {
                                int i10 = i4;
                                i4++;
                                cArr[i10] = (char) i7;
                                break;
                            }
                            return -1;
                        }
                        return -1;
                    }
                    if (i3 >= i5) {
                        return -1;
                    }
                    int i11 = i3;
                    i3++;
                    byte b4 = bArr[i11];
                    i7 = (i7 << 6) | (b4 & 63);
                    b2 = transitionTable[b3 + ((b4 & 255) >>> 4)];
                }
            } else {
                if (i4 >= cArr.length) {
                    return -1;
                }
                int i12 = i4;
                i4++;
                cArr[i12] = (char) b;
            }
        }
        return i4;
    }

    static {
        byte[] bArr = new byte[128];
        fill(bArr, 0, 15, (byte) 1);
        fill(bArr, 16, 31, (byte) 2);
        fill(bArr, 32, 63, (byte) 3);
        fill(bArr, 64, 65, (byte) 0);
        fill(bArr, 66, 95, (byte) 4);
        fill(bArr, 96, 96, (byte) 5);
        fill(bArr, 97, 108, (byte) 6);
        fill(bArr, 109, 109, (byte) 7);
        fill(bArr, 110, 111, (byte) 6);
        fill(bArr, 112, 112, (byte) 8);
        fill(bArr, 113, 115, (byte) 9);
        fill(bArr, 116, 116, (byte) 10);
        fill(bArr, 117, 127, (byte) 0);
        fill(transitionTable, 0, transitionTable.length - 1, (byte) -2);
        fill(transitionTable, 8, 11, (byte) -1);
        fill(transitionTable, 24, 27, (byte) 0);
        fill(transitionTable, 40, 43, (byte) 16);
        fill(transitionTable, 58, 59, (byte) 0);
        fill(transitionTable, 72, 73, (byte) 0);
        fill(transitionTable, 89, 91, (byte) 16);
        fill(transitionTable, 104, 104, (byte) 16);
        byte[] bArr2 = {0, 0, 0, 0, 31, 15, 15, 15, 7, 7, 7};
        byte[] bArr3 = {-2, -2, -2, -2, 0, 48, 16, 64, 80, 32, 96};
        for (int i = 0; i < 128; i++) {
            byte b = bArr[i];
            firstUnitTable[i] = (short) (((i & bArr2[b]) << 8) | bArr3[b]);
        }
    }
}
