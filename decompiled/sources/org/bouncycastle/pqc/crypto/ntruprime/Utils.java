package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/Utils.class */
class Utils {
    Utils() {
    }

    protected static int getRandomUnsignedInteger(SecureRandom secureRandom) {
        byte[] bArr = new byte[4];
        secureRandom.nextBytes(bArr);
        return bToUnsignedInt(bArr[0]) + (bToUnsignedInt(bArr[1]) << 8) + (bToUnsignedInt(bArr[2]) << 16) + (bToUnsignedInt(bArr[3]) << 24);
    }

    protected static void getRandomSmallPolynomial(SecureRandom secureRandom, byte[] bArr) {
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) ((((getRandomUnsignedInteger(secureRandom) & 1073741823) * 3) >>> 30) - 1);
        }
    }

    protected static int getModFreeze(int i, int i2) {
        return getSignedDivMod(i + ((i2 - 1) / 2), i2)[1] - ((i2 - 1) / 2);
    }

    protected static boolean isInvertiblePolynomialInR3(byte[] bArr, byte[] bArr2, int i) {
        byte[] bArr3 = new byte[i + 1];
        byte[] bArr4 = new byte[i + 1];
        byte[] bArr5 = new byte[i + 1];
        byte[] bArr6 = new byte[i + 1];
        bArr5[0] = 1;
        bArr3[0] = 1;
        bArr3[i - 1] = -1;
        bArr3[i] = -1;
        for (int i2 = 0; i2 < i; i2++) {
            bArr4[(i - 1) - i2] = bArr[i2];
        }
        bArr4[i] = 0;
        int i3 = 1;
        for (int i4 = 0; i4 < (2 * i) - 1; i4++) {
            System.arraycopy(bArr6, 0, bArr6, 1, i);
            bArr6[0] = 0;
            int i5 = (-bArr4[0]) * bArr3[0];
            int iCheckLessThanZero = checkLessThanZero(-i3) & checkNotEqualToZero(bArr4[0]);
            i3 = (i3 ^ (iCheckLessThanZero & (i3 ^ (-i3)))) + 1;
            for (int i6 = 0; i6 < i + 1; i6++) {
                int i7 = iCheckLessThanZero & (bArr3[i6] ^ bArr4[i6]);
                int i8 = i6;
                bArr3[i8] = (byte) (bArr3[i8] ^ i7);
                int i9 = i6;
                bArr4[i9] = (byte) (bArr4[i9] ^ i7);
                int i10 = iCheckLessThanZero & (bArr6[i6] ^ bArr5[i6]);
                int i11 = i6;
                bArr6[i11] = (byte) (bArr6[i11] ^ i10);
                int i12 = i6;
                bArr5[i12] = (byte) (bArr5[i12] ^ i10);
            }
            for (int i13 = 0; i13 < i + 1; i13++) {
                bArr4[i13] = (byte) getModFreeze(bArr4[i13] + (i5 * bArr3[i13]), 3);
            }
            for (int i14 = 0; i14 < i + 1; i14++) {
                bArr5[i14] = (byte) getModFreeze(bArr5[i14] + (i5 * bArr6[i14]), 3);
            }
            for (int i15 = 0; i15 < i; i15++) {
                bArr4[i15] = bArr4[i15 + 1];
            }
            bArr4[i] = 0;
        }
        byte b = bArr3[0];
        for (int i16 = 0; i16 < i; i16++) {
            bArr2[i16] = (byte) (b * bArr6[(i - 1) - i16]);
        }
        return i3 == 0;
    }

    protected static void minmax(int[] iArr, int i, int i2) {
        int i3 = iArr[i];
        int i4 = iArr[i2];
        int i5 = i3 ^ i4;
        int i6 = i4 - i3;
        int i7 = (-((i6 ^ (i5 & ((i6 ^ i4) ^ Integer.MIN_VALUE))) >>> 31)) & i5;
        iArr[i] = i3 ^ i7;
        iArr[i2] = i4 ^ i7;
    }

    protected static void cryptoSort(int[] iArr, int i) {
        int i2;
        if (i < 2) {
            return;
        }
        int i3 = 1;
        while (true) {
            i2 = i3;
            if (i2 >= i - i2) {
                break;
            } else {
                i3 = i2 + i2;
            }
        }
        int i4 = i2;
        while (true) {
            int i5 = i4;
            if (i5 <= 0) {
                return;
            }
            for (int i6 = 0; i6 < i - i5; i6++) {
                if ((i6 & i5) == 0) {
                    minmax(iArr, i6, i6 + i5);
                }
            }
            int i7 = i2;
            while (true) {
                int i8 = i7;
                if (i8 > i5) {
                    for (int i9 = 0; i9 < i - i8; i9++) {
                        if ((i9 & i5) == 0) {
                            minmax(iArr, i9 + i5, i9 + i8);
                        }
                    }
                    i7 = i8 >>> 1;
                }
            }
            i4 = i5 >>> 1;
        }
    }

    protected static void sortGenerateShortPolynomial(byte[] bArr, int[] iArr, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            iArr[i3] = iArr[i3] & (-2);
        }
        for (int i4 = i2; i4 < i; i4++) {
            iArr[i4] = (iArr[i4] & (-3)) | 1;
        }
        cryptoSort(iArr, i);
        for (int i5 = 0; i5 < i; i5++) {
            bArr[i5] = (byte) ((iArr[i5] & 3) - 1);
        }
    }

    protected static void getRandomShortPolynomial(SecureRandom secureRandom, byte[] bArr, int i, int i2) {
        int[] iArr = new int[i];
        for (int i3 = 0; i3 < i; i3++) {
            iArr[i3] = getRandomUnsignedInteger(secureRandom);
        }
        sortGenerateShortPolynomial(bArr, iArr, i, i2);
    }

    protected static int getInverseInRQ(int i, int i2) {
        int modFreeze = i;
        for (int i3 = 1; i3 < i2 - 2; i3++) {
            modFreeze = getModFreeze(i * modFreeze, i2);
        }
        return modFreeze;
    }

    protected static void getOneThirdInverseInRQ(short[] sArr, byte[] bArr, int i, int i2) {
        short[] sArr2 = new short[i + 1];
        short[] sArr3 = new short[i + 1];
        short[] sArr4 = new short[i + 1];
        short[] sArr5 = new short[i + 1];
        sArr4[0] = (short) getInverseInRQ(3, i2);
        sArr2[0] = 1;
        sArr2[i - 1] = -1;
        sArr2[i] = -1;
        for (int i3 = 0; i3 < i; i3++) {
            sArr3[(i - 1) - i3] = bArr[i3];
        }
        sArr3[i] = 0;
        int i4 = 1;
        for (int i5 = 0; i5 < (2 * i) - 1; i5++) {
            System.arraycopy(sArr5, 0, sArr5, 1, i);
            sArr5[0] = 0;
            int iCheckLessThanZero = checkLessThanZero(-i4) & checkNotEqualToZero(sArr3[0]);
            i4 = (i4 ^ (iCheckLessThanZero & (i4 ^ (-i4)))) + 1;
            for (int i6 = 0; i6 < i + 1; i6++) {
                int i7 = iCheckLessThanZero & (sArr2[i6] ^ sArr3[i6]);
                int i8 = i6;
                sArr2[i8] = (short) (sArr2[i8] ^ i7);
                int i9 = i6;
                sArr3[i9] = (short) (sArr3[i9] ^ i7);
                int i10 = iCheckLessThanZero & (sArr5[i6] ^ sArr4[i6]);
                int i11 = i6;
                sArr5[i11] = (short) (sArr5[i11] ^ i10);
                int i12 = i6;
                sArr4[i12] = (short) (sArr4[i12] ^ i10);
            }
            short s = sArr2[0];
            short s2 = sArr3[0];
            for (int i13 = 0; i13 < i + 1; i13++) {
                sArr3[i13] = (short) getModFreeze((s * sArr3[i13]) - (s2 * sArr2[i13]), i2);
            }
            for (int i14 = 0; i14 < i + 1; i14++) {
                sArr4[i14] = (short) getModFreeze((s * sArr4[i14]) - (s2 * sArr5[i14]), i2);
            }
            for (int i15 = 0; i15 < i; i15++) {
                sArr3[i15] = sArr3[i15 + 1];
            }
            sArr3[i] = 0;
        }
        int inverseInRQ = getInverseInRQ(sArr2[0], i2);
        for (int i16 = 0; i16 < i; i16++) {
            sArr[i16] = (short) getModFreeze(inverseInRQ * sArr5[(i - 1) - i16], i2);
        }
    }

    protected static void multiplicationInRQ(short[] sArr, short[] sArr2, byte[] bArr, int i, int i2) {
        short[] sArr3 = new short[(i + i) - 1];
        for (int i3 = 0; i3 < i; i3++) {
            short modFreeze = 0;
            for (int i4 = 0; i4 <= i3; i4++) {
                modFreeze = (short) getModFreeze(modFreeze + (sArr2[i4] * bArr[i3 - i4]), i2);
            }
            sArr3[i3] = modFreeze;
        }
        for (int i5 = i; i5 < (i + i) - 1; i5++) {
            short modFreeze2 = 0;
            for (int i6 = (i5 - i) + 1; i6 < i; i6++) {
                modFreeze2 = (short) getModFreeze(modFreeze2 + (sArr2[i6] * bArr[i5 - i6]), i2);
            }
            sArr3[i5] = modFreeze2;
        }
        for (int i7 = (i + i) - 2; i7 >= i; i7--) {
            sArr3[i7 - i] = (short) getModFreeze(sArr3[i7 - i] + sArr3[i7], i2);
            sArr3[(i7 - i) + 1] = (short) getModFreeze(sArr3[(i7 - i) + 1] + sArr3[i7], i2);
        }
        for (int i8 = 0; i8 < i; i8++) {
            sArr[i8] = sArr3[i8];
        }
    }

    private static void encode(byte[] bArr, short[] sArr, short[] sArr2, int i, int i2) {
        int i3;
        if (i == 1) {
            short s = sArr[0];
            short s2 = sArr2[0];
            while (true) {
                short s3 = s2;
                if (s3 <= 1) {
                    break;
                }
                int i4 = i2;
                i2++;
                bArr[i4] = (byte) s;
                s = (short) (s >>> 8);
                s2 = (short) ((s3 + 255) >>> 8);
            }
        }
        if (i > 1) {
            short[] sArr3 = new short[(i + 1) / 2];
            short[] sArr4 = new short[(i + 1) / 2];
            int i5 = 0;
            while (i5 < i - 1) {
                short s4 = sArr2[i5];
                int i6 = sArr[i5] + (sArr[i5 + 1] * s4);
                int i7 = sArr2[i5 + 1] * s4;
                while (true) {
                    i3 = i7;
                    if (i3 >= 16384) {
                        int i8 = i2;
                        i2++;
                        bArr[i8] = (byte) i6;
                        i6 >>>= 8;
                        i7 = (i3 + 255) >>> 8;
                    }
                }
                sArr3[i5 / 2] = (short) i6;
                sArr4[i5 / 2] = (short) i3;
                i5 += 2;
            }
            if (i5 < i) {
                sArr3[i5 / 2] = sArr[i5];
                sArr4[i5 / 2] = sArr2[i5];
            }
            encode(bArr, sArr3, sArr4, (i + 1) / 2, i2);
        }
    }

    protected static void getEncodedPolynomial(byte[] bArr, short[] sArr, int i, int i2) {
        short[] sArr2 = new short[i];
        short[] sArr3 = new short[i];
        for (int i3 = 0; i3 < i; i3++) {
            sArr2[i3] = (short) (sArr[i3] + ((i2 - 1) / 2));
        }
        for (int i4 = 0; i4 < i; i4++) {
            sArr3[i4] = (short) i2;
        }
        encode(bArr, sArr2, sArr3, i, 0);
    }

    protected static void getEncodedSmallPolynomial(byte[] bArr, byte[] bArr2, int i) {
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < i / 4; i4++) {
            int i5 = i2;
            int i6 = i2 + 1;
            int i7 = i6 + 1;
            byte b = (byte) (((byte) (bArr2[i5] + 1)) + (((byte) (bArr2[i6] + 1)) << 2));
            int i8 = i7 + 1;
            byte b2 = (byte) (b + (((byte) (bArr2[i7] + 1)) << 4));
            i2 = i8 + 1;
            int i9 = i3;
            i3++;
            bArr[i9] = (byte) (b2 + (((byte) (bArr2[i8] + 1)) << 6));
        }
        bArr[i3] = (byte) (bArr2[i2] + 1);
    }

    private static void generateAES256CTRStream(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        CTRModeCipher cTRModeCipherNewInstance = SICBlockCipher.newInstance(AESEngine.newInstance());
        cTRModeCipherNewInstance.init(true, new ParametersWithIV(new KeyParameter(bArr4), bArr3));
        cTRModeCipherNewInstance.processBytes(bArr, 0, bArr2.length, bArr2, 0);
    }

    protected static void expand(int[] iArr, byte[] bArr) {
        byte[] bArr2 = new byte[iArr.length * 4];
        byte[] bArr3 = new byte[iArr.length * 4];
        generateAES256CTRStream(bArr2, bArr3, new byte[16], bArr);
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = bToUnsignedInt(bArr3[i * 4]) + (bToUnsignedInt(bArr3[(i * 4) + 1]) << 8) + (bToUnsignedInt(bArr3[(i * 4) + 2]) << 16) + (bToUnsignedInt(bArr3[(i * 4) + 3]) << 24);
        }
    }

    private static int getUnsignedMod(int i, int i2) {
        return getUnsignedDivMod(i, i2)[1];
    }

    protected static void generatePolynomialInRQFromSeed(short[] sArr, byte[] bArr, int i, int i2) {
        int[] iArr = new int[i];
        expand(iArr, bArr);
        for (int i3 = 0; i3 < i; i3++) {
            sArr[i3] = (short) (getUnsignedMod(iArr[i3], i2) - ((i2 - 1) / 2));
        }
    }

    protected static void roundPolynomial(short[] sArr, short[] sArr2) {
        for (int i = 0; i < sArr.length; i++) {
            sArr[i] = (short) (sArr2[i] - getModFreeze(sArr2[i], 3));
        }
    }

    protected static void getRoundedEncodedPolynomial(byte[] bArr, short[] sArr, int i, int i2) {
        short[] sArr2 = new short[i];
        short[] sArr3 = new short[i];
        for (int i3 = 0; i3 < i; i3++) {
            sArr2[i3] = (short) (((sArr[i3] + ((i2 - 1) / 2)) * 10923) >>> 15);
            sArr3[i3] = (short) ((i2 + 2) / 3);
        }
        encode(bArr, sArr2, sArr3, i, 0);
    }

    protected static byte[] getHashWithPrefix(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[64];
        byte[] bArr4 = new byte[bArr.length + bArr2.length];
        System.arraycopy(bArr, 0, bArr4, 0, bArr.length);
        System.arraycopy(bArr2, 0, bArr4, bArr.length, bArr2.length);
        SHA512Digest sHA512Digest = new SHA512Digest();
        sHA512Digest.update(bArr4, 0, bArr4.length);
        sHA512Digest.doFinal(bArr3, 0);
        return bArr3;
    }

    private static void decode(short[] sArr, byte[] bArr, short[] sArr2, int i, int i2, int i3) {
        if (i == 1) {
            if (sArr2[0] == 1) {
                sArr[i2] = 0;
            } else if (sArr2[0] <= 256) {
                sArr[i2] = (short) getUnsignedMod(bToUnsignedInt(bArr[i3]), sArr2[0]);
            } else {
                sArr[i2] = (short) getUnsignedMod(bToUnsignedInt(bArr[i3]) + (bArr[i3 + 1] << 8), sArr2[0]);
            }
        }
        if (i > 1) {
            short[] sArr3 = new short[(i + 1) / 2];
            short[] sArr4 = new short[(i + 1) / 2];
            short[] sArr5 = new short[i / 2];
            int[] iArr = new int[i / 2];
            int i4 = 0;
            while (i4 < i - 1) {
                int i5 = sArr2[i4] * sArr2[i4 + 1];
                if (i5 > 4194048) {
                    iArr[i4 / 2] = 65536;
                    sArr5[i4 / 2] = (short) (bToUnsignedInt(bArr[i3]) + (256 * bToUnsignedInt(bArr[i3 + 1])));
                    i3 += 2;
                    sArr4[i4 / 2] = (short) ((((i5 + 255) >>> 8) + 255) >>> 8);
                } else if (i5 >= 16384) {
                    iArr[i4 / 2] = 256;
                    sArr5[i4 / 2] = (short) bToUnsignedInt(bArr[i3]);
                    i3++;
                    sArr4[i4 / 2] = (short) ((i5 + 255) >>> 8);
                } else {
                    iArr[i4 / 2] = 1;
                    sArr5[i4 / 2] = 0;
                    sArr4[i4 / 2] = (short) i5;
                }
                i4 += 2;
            }
            if (i4 < i) {
                sArr4[i4 / 2] = sArr2[i4];
            }
            decode(sArr3, bArr, sArr4, (i + 1) / 2, i2, i3);
            int i6 = 0;
            while (i6 < i - 1) {
                int[] unsignedDivMod = getUnsignedDivMod(sToUnsignedInt(sArr5[i6 / 2]) + (iArr[i6 / 2] * sToUnsignedInt(sArr3[i6 / 2])), sArr2[i6]);
                int i7 = i2;
                int i8 = i2 + 1;
                sArr[i7] = (short) unsignedDivMod[1];
                i2 = i8 + 1;
                sArr[i8] = (short) getUnsignedMod(unsignedDivMod[0], sArr2[i6 + 1]);
                i6 += 2;
            }
            if (i6 < i) {
                sArr[i2] = sArr3[i6 / 2];
            }
        }
    }

    protected static void getDecodedPolynomial(short[] sArr, byte[] bArr, int i, int i2) {
        short[] sArr2 = new short[i];
        short[] sArr3 = new short[i];
        for (int i3 = 0; i3 < i; i3++) {
            sArr3[i3] = (short) i2;
        }
        decode(sArr2, bArr, sArr3, i, 0, 0);
        for (int i4 = 0; i4 < i; i4++) {
            sArr[i4] = (short) (sArr2[i4] - ((i2 - 1) / 2));
        }
    }

    protected static void getRandomInputs(SecureRandom secureRandom, byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length / 8];
        secureRandom.nextBytes(bArr2);
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) (1 & (bArr2[i >>> 3] >>> (i & 7)));
        }
    }

    protected static void getEncodedInputs(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < bArr2.length; i++) {
            int i2 = i >>> 3;
            bArr[i2] = (byte) (bArr[i2] | (bArr2[i] << (i & 7)));
        }
    }

    protected static void getRoundedDecodedPolynomial(short[] sArr, byte[] bArr, int i, int i2) {
        short[] sArr2 = new short[i];
        short[] sArr3 = new short[i];
        for (int i3 = 0; i3 < i; i3++) {
            sArr3[i3] = (short) ((i2 + 2) / 3);
        }
        decode(sArr2, bArr, sArr3, i, 0, 0);
        for (int i4 = 0; i4 < i; i4++) {
            sArr[i4] = (short) ((sArr2[i4] * 3) - ((i2 - 1) / 2));
        }
    }

    protected static void top(byte[] bArr, short[] sArr, byte[] bArr2, int i, int i2, int i3) {
        for (int i4 = 0; i4 < bArr.length; i4++) {
            bArr[i4] = (byte) (((i3 * (getModFreeze(sArr[i4] + (bArr2[i4] * ((i - 1) / 2)), i) + i2)) + 16384) >>> 15);
        }
    }

    protected static void getTopEncodedPolynomial(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) (bArr2[2 * i] + (bArr2[(2 * i) + 1] << 4));
        }
    }

    protected static void getDecodedSmallPolynomial(byte[] bArr, byte[] bArr2, int i) {
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < i / 4; i4++) {
            int i5 = i3;
            i3++;
            byte b = bArr2[i5];
            int i6 = i2;
            int i7 = i2 + 1;
            bArr[i6] = (byte) ((bToUnsignedInt(b) & 3) - 1);
            byte b2 = (byte) (b >>> 2);
            int i8 = i7 + 1;
            bArr[i7] = (byte) ((bToUnsignedInt(b2) & 3) - 1);
            byte b3 = (byte) (b2 >>> 2);
            int i9 = i8 + 1;
            bArr[i8] = (byte) ((bToUnsignedInt(b3) & 3) - 1);
            i2 = i9 + 1;
            bArr[i9] = (byte) ((bToUnsignedInt((byte) (b3 >>> 2)) & 3) - 1);
        }
        bArr[i2] = (byte) ((bToUnsignedInt(bArr2[i3]) & 3) - 1);
    }

    protected static void scalarMultiplicationInRQ(short[] sArr, short[] sArr2, int i, int i2) {
        for (int i3 = 0; i3 < sArr2.length; i3++) {
            sArr[i3] = (short) getModFreeze(i * sArr2[i3], i2);
        }
    }

    protected static void transformRQToR3(byte[] bArr, short[] sArr) {
        for (int i = 0; i < sArr.length; i++) {
            bArr[i] = (byte) getModFreeze(sArr[i], 3);
        }
    }

    protected static void multiplicationInR3(byte[] bArr, byte[] bArr2, byte[] bArr3, int i) {
        byte[] bArr4 = new byte[(i + i) - 1];
        for (int i2 = 0; i2 < i; i2++) {
            byte modFreeze = 0;
            for (int i3 = 0; i3 <= i2; i3++) {
                modFreeze = (byte) getModFreeze(modFreeze + (bArr2[i3] * bArr3[i2 - i3]), 3);
            }
            bArr4[i2] = modFreeze;
        }
        for (int i4 = i; i4 < (i + i) - 1; i4++) {
            byte modFreeze2 = 0;
            for (int i5 = (i4 - i) + 1; i5 < i; i5++) {
                modFreeze2 = (byte) getModFreeze(modFreeze2 + (bArr2[i5] * bArr3[i4 - i5]), 3);
            }
            bArr4[i4] = modFreeze2;
        }
        for (int i6 = (i + i) - 2; i6 >= i; i6--) {
            bArr4[i6 - i] = (byte) getModFreeze(bArr4[i6 - i] + bArr4[i6], 3);
            bArr4[(i6 - i) + 1] = (byte) getModFreeze(bArr4[(i6 - i) + 1] + bArr4[i6], 3);
        }
        for (int i7 = 0; i7 < i; i7++) {
            bArr[i7] = bArr4[i7];
        }
    }

    protected static void checkForSmallPolynomial(byte[] bArr, byte[] bArr2, int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 != bArr2.length; i4++) {
            i3 += bArr2[i4] & 1;
        }
        int iCheckNotEqualToZero = checkNotEqualToZero(i3 - i2);
        for (int i5 = 0; i5 < i2; i5++) {
            bArr[i5] = (byte) (((bArr2[i5] ^ 1) & (iCheckNotEqualToZero ^ (-1))) ^ 1);
        }
        for (int i6 = i2; i6 < i; i6++) {
            bArr[i6] = (byte) (bArr2[i6] & (iCheckNotEqualToZero ^ (-1)));
        }
    }

    protected static void updateDiffMask(byte[] bArr, byte[] bArr2, int i) {
        for (int i2 = 0; i2 < bArr.length; i2++) {
            int i3 = i2;
            bArr[i3] = (byte) (bArr[i3] ^ (i & (bArr[i2] ^ bArr2[i2])));
        }
    }

    protected static void getTopDecodedPolynomial(byte[] bArr, byte[] bArr2) {
        for (int i = 0; i < bArr2.length; i++) {
            bArr[2 * i] = (byte) (bArr2[i] & 15);
            bArr[(2 * i) + 1] = (byte) (bArr2[i] >>> 4);
        }
    }

    protected static void right(byte[] bArr, short[] sArr, byte[] bArr2, int i, int i2, int i3, int i4) {
        for (int i5 = 0; i5 < bArr.length; i5++) {
            bArr[i5] = (byte) (-checkLessThanZero(getModFreeze((getModFreeze((i4 * bArr2[i5]) - i3, i) - sArr[i5]) + (4 * i2) + 1, i)));
        }
    }

    private static int[] getUnsignedDivMod(int i, int i2) {
        long jIToUnsignedLong = iToUnsignedLong(i);
        long jIToUnsignedLong2 = iToUnsignedLong(Integer.MIN_VALUE) / ((long) i2);
        long j = (jIToUnsignedLong * jIToUnsignedLong2) >>> 31;
        long j2 = jIToUnsignedLong - (j * ((long) i2));
        long j3 = 0 + j;
        long j4 = (j2 * jIToUnsignedLong2) >>> 31;
        long j5 = j2 - (j4 * ((long) i2));
        long j6 = j3 + j4;
        long j7 = j5 - ((long) i2);
        long j8 = j6 + 1;
        long j9 = -(j7 >>> 63);
        return new int[]{toIntExact(j8 + j9), toIntExact(j7 + (j9 & ((long) i2)))};
    }

    private static int[] getSignedDivMod(int i, int i2) {
        int[] unsignedDivMod = getUnsignedDivMod(toIntExact((-2147483648L) + iToUnsignedLong(i)), i2);
        int[] unsignedDivMod2 = getUnsignedDivMod(Integer.MIN_VALUE, i2);
        int intExact = toIntExact(iToUnsignedLong(unsignedDivMod[0]) - iToUnsignedLong(unsignedDivMod2[0]));
        int intExact2 = toIntExact(iToUnsignedLong(unsignedDivMod[1]) - iToUnsignedLong(unsignedDivMod2[1]));
        int i3 = -(intExact2 >>> 31);
        return new int[]{intExact + i3, intExact2 + (i3 & i2)};
    }

    private static int checkLessThanZero(int i) {
        return -(i >>> 31);
    }

    private static int checkNotEqualToZero(int i) {
        return -((int) ((-iToUnsignedLong(i)) >>> 63));
    }

    static int bToUnsignedInt(byte b) {
        return b & 255;
    }

    static int sToUnsignedInt(short s) {
        return s & 65535;
    }

    static long iToUnsignedLong(int i) {
        return ((long) i) & 4294967295L;
    }

    static int toIntExact(long j) {
        int i = (int) j;
        if (i != j) {
            throw new IllegalStateException("value out of integer range");
        }
        return i;
    }
}
