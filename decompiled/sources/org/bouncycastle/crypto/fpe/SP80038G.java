package org.bouncycastle.crypto.fpe;

import java.math.BigInteger;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.util.RadixConverter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/fpe/SP80038G.class */
class SP80038G {
    static final String FPE_DISABLED = "org.bouncycastle.fpe.disable";
    static final String FF1_DISABLED = "org.bouncycastle.fpe.disable_ff1";
    protected static final int BLOCK_SIZE = 16;
    protected static final double LOG2 = Math.log(2.0d);
    protected static final double TWO_TO_96 = Math.pow(2.0d, 96.0d);

    SP80038G() {
    }

    static byte[] decryptFF1(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, byte[] bArr2, int i, int i2) {
        checkArgs(blockCipher, true, radixConverter.getRadix(), bArr2, i, i2);
        int i3 = i2 / 2;
        int i4 = i2 - i3;
        return toByte(decFF1(blockCipher, radixConverter, bArr, i2, i3, i4, toShort(bArr2, i, i3), toShort(bArr2, i + i3, i4)));
    }

    static short[] decryptFF1w(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, short[] sArr, int i, int i2) {
        checkArgs(blockCipher, true, radixConverter.getRadix(), sArr, i, i2);
        int i3 = i2 / 2;
        int i4 = i2 - i3;
        short[] sArr2 = new short[i3];
        short[] sArr3 = new short[i4];
        System.arraycopy(sArr, i, sArr2, 0, i3);
        System.arraycopy(sArr, i + i3, sArr3, 0, i4);
        return decFF1(blockCipher, radixConverter, bArr, i2, i3, i4, sArr2, sArr3);
    }

    static short[] decFF1(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, int i, int i2, int i3, short[] sArr, short[] sArr2) {
        int radix = radixConverter.getRadix();
        int length = bArr.length;
        int iCalculateB_FF1 = calculateB_FF1(radix, i3);
        int i4 = (iCalculateB_FF1 + 7) & (-4);
        byte[] bArrCalculateP_FF1 = calculateP_FF1(radix, (byte) i2, i, length);
        BigInteger[] bigIntegerArrCalculateModUV = calculateModUV(BigInteger.valueOf(radix), i2, i3);
        int i5 = i2;
        for (int i6 = 9; i6 >= 0; i6--) {
            i5 = i - i5;
            BigInteger bigIntegerMod = radixConverter.fromEncoding(sArr2).subtract(calculateY_FF1(blockCipher, bArr, iCalculateB_FF1, i4, i6, bArrCalculateP_FF1, sArr, radixConverter)).mod(bigIntegerArrCalculateModUV[i6 & 1]);
            short[] sArr3 = sArr2;
            sArr2 = sArr;
            sArr = sArr3;
            radixConverter.toEncoding(bigIntegerMod, i5, sArr3);
        }
        return Arrays.concatenate(sArr, sArr2);
    }

    static byte[] decryptFF3(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, byte[] bArr2, int i, int i2) {
        checkArgs(blockCipher, false, radixConverter.getRadix(), bArr2, i, i2);
        if (bArr.length != 8) {
            throw new IllegalArgumentException();
        }
        return implDecryptFF3(blockCipher, radixConverter, bArr, bArr2, i, i2);
    }

    static byte[] decryptFF3_1(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, byte[] bArr2, int i, int i2) {
        checkArgs(blockCipher, false, radixConverter.getRadix(), bArr2, i, i2);
        if (bArr.length != 7) {
            throw new IllegalArgumentException("tweak should be 56 bits");
        }
        return implDecryptFF3(blockCipher, radixConverter, calculateTweak64_FF3_1(bArr), bArr2, i, i2);
    }

    static short[] decryptFF3_1w(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, short[] sArr, int i, int i2) {
        checkArgs(blockCipher, false, radixConverter.getRadix(), sArr, i, i2);
        if (bArr.length != 7) {
            throw new IllegalArgumentException("tweak should be 56 bits");
        }
        return implDecryptFF3w(blockCipher, radixConverter, calculateTweak64_FF3_1(bArr), sArr, i, i2);
    }

    static byte[] encryptFF1(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, byte[] bArr2, int i, int i2) {
        checkArgs(blockCipher, true, radixConverter.getRadix(), bArr2, i, i2);
        int i3 = i2 / 2;
        int i4 = i2 - i3;
        return toByte(encFF1(blockCipher, radixConverter, bArr, i2, i3, i4, toShort(bArr2, i, i3), toShort(bArr2, i + i3, i4)));
    }

    static short[] encryptFF1w(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, short[] sArr, int i, int i2) {
        checkArgs(blockCipher, true, radixConverter.getRadix(), sArr, i, i2);
        int i3 = i2 / 2;
        int i4 = i2 - i3;
        short[] sArr2 = new short[i3];
        short[] sArr3 = new short[i4];
        System.arraycopy(sArr, i, sArr2, 0, i3);
        System.arraycopy(sArr, i + i3, sArr3, 0, i4);
        return encFF1(blockCipher, radixConverter, bArr, i2, i3, i4, sArr2, sArr3);
    }

    private static short[] encFF1(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, int i, int i2, int i3, short[] sArr, short[] sArr2) {
        int radix = radixConverter.getRadix();
        int length = bArr.length;
        int iCalculateB_FF1 = calculateB_FF1(radix, i3);
        int i4 = (iCalculateB_FF1 + 7) & (-4);
        byte[] bArrCalculateP_FF1 = calculateP_FF1(radix, (byte) i2, i, length);
        BigInteger[] bigIntegerArrCalculateModUV = calculateModUV(BigInteger.valueOf(radix), i2, i3);
        int i5 = i3;
        for (int i6 = 0; i6 < 10; i6++) {
            i5 = i - i5;
            BigInteger bigIntegerMod = radixConverter.fromEncoding(sArr).add(calculateY_FF1(blockCipher, bArr, iCalculateB_FF1, i4, i6, bArrCalculateP_FF1, sArr2, radixConverter)).mod(bigIntegerArrCalculateModUV[i6 & 1]);
            short[] sArr3 = sArr;
            sArr = sArr2;
            sArr2 = sArr3;
            radixConverter.toEncoding(bigIntegerMod, i5, sArr3);
        }
        return Arrays.concatenate(sArr, sArr2);
    }

    static byte[] encryptFF3(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, byte[] bArr2, int i, int i2) {
        checkArgs(blockCipher, false, radixConverter.getRadix(), bArr2, i, i2);
        if (bArr.length != 8) {
            throw new IllegalArgumentException();
        }
        return implEncryptFF3(blockCipher, radixConverter, bArr, bArr2, i, i2);
    }

    static short[] encryptFF3w(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, short[] sArr, int i, int i2) {
        checkArgs(blockCipher, false, radixConverter.getRadix(), sArr, i, i2);
        if (bArr.length != 8) {
            throw new IllegalArgumentException();
        }
        return implEncryptFF3w(blockCipher, radixConverter, bArr, sArr, i, i2);
    }

    static short[] encryptFF3_1w(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, short[] sArr, int i, int i2) {
        checkArgs(blockCipher, false, radixConverter.getRadix(), sArr, i, i2);
        if (bArr.length != 7) {
            throw new IllegalArgumentException("tweak should be 56 bits");
        }
        return encryptFF3w(blockCipher, radixConverter, calculateTweak64_FF3_1(bArr), sArr, i, i2);
    }

    static byte[] encryptFF3_1(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, byte[] bArr2, int i, int i2) {
        checkArgs(blockCipher, false, radixConverter.getRadix(), bArr2, i, i2);
        if (bArr.length != 7) {
            throw new IllegalArgumentException("tweak should be 56 bits");
        }
        return encryptFF3(blockCipher, radixConverter, calculateTweak64_FF3_1(bArr), bArr2, i, i2);
    }

    protected static int calculateB_FF1(int i, int i2) {
        int iNumberOfTrailingZeros = Integers.numberOfTrailingZeros(i);
        int iBitLength = iNumberOfTrailingZeros * i2;
        int i3 = i >>> iNumberOfTrailingZeros;
        if (i3 != 1) {
            iBitLength += BigInteger.valueOf(i3).pow(i2).bitLength();
        }
        return (iBitLength + 7) / 8;
    }

    protected static BigInteger[] calculateModUV(BigInteger bigInteger, int i, int i2) {
        BigInteger[] bigIntegerArr = new BigInteger[2];
        bigIntegerArr[0] = bigInteger.pow(i);
        bigIntegerArr[1] = bigIntegerArr[0];
        if (i2 != i) {
            bigIntegerArr[1] = bigIntegerArr[1].multiply(bigInteger);
        }
        return bigIntegerArr;
    }

    protected static byte[] calculateP_FF1(int i, byte b, int i2, int i3) {
        byte[] bArr = {1, 2, 1, 0, (byte) (i >> 8), (byte) i, 10, b, 0, 0, 0, 0, 0, 0, 0, 0};
        Pack.intToBigEndian(i2, bArr, 8);
        Pack.intToBigEndian(i3, bArr, 12);
        return bArr;
    }

    protected static byte[] calculateTweak64_FF3_1(byte[] bArr) {
        return new byte[]{bArr[0], bArr[1], bArr[2], (byte) (bArr[3] & 240), bArr[4], bArr[5], bArr[6], (byte) (bArr[3] << 4)};
    }

    protected static BigInteger calculateY_FF1(BlockCipher blockCipher, byte[] bArr, int i, int i2, int i3, byte[] bArr2, short[] sArr, RadixConverter radixConverter) {
        int length = bArr.length;
        byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray(radixConverter.fromEncoding(sArr));
        int i4 = (-(length + i + 1)) & 15;
        byte[] bArr3 = new byte[length + i4 + 1 + i];
        System.arraycopy(bArr, 0, bArr3, 0, length);
        bArr3[length + i4] = (byte) i3;
        System.arraycopy(bArrAsUnsignedByteArray, 0, bArr3, bArr3.length - bArrAsUnsignedByteArray.length, bArrAsUnsignedByteArray.length);
        byte[] bArrPrf = prf(blockCipher, Arrays.concatenate(bArr2, bArr3));
        byte[] bArr4 = bArrPrf;
        if (i2 > 16) {
            int i5 = ((i2 + 16) - 1) / 16;
            bArr4 = new byte[i5 * 16];
            int iBigEndianToInt = Pack.bigEndianToInt(bArrPrf, 12);
            System.arraycopy(bArrPrf, 0, bArr4, 0, 16);
            for (int i6 = 1; i6 < i5; i6++) {
                int i7 = i6 * 16;
                System.arraycopy(bArrPrf, 0, bArr4, i7, 12);
                Pack.intToBigEndian(iBigEndianToInt ^ i6, bArr4, (i7 + 16) - 4);
                blockCipher.processBlock(bArr4, i7, bArr4, i7);
            }
        }
        return num(bArr4, 0, i2);
    }

    protected static BigInteger calculateY_FF3(BlockCipher blockCipher, byte[] bArr, int i, int i2, short[] sArr, RadixConverter radixConverter) {
        byte[] bArr2 = new byte[16];
        Pack.intToBigEndian(Pack.bigEndianToInt(bArr, i) ^ i2, bArr2, 0);
        BigIntegers.asUnsignedByteArray(radixConverter.fromEncoding(sArr), bArr2, 4, 12);
        Arrays.reverseInPlace(bArr2);
        blockCipher.processBlock(bArr2, 0, bArr2, 0);
        Arrays.reverseInPlace(bArr2);
        return num(bArr2, 0, bArr2.length);
    }

    protected static void checkArgs(BlockCipher blockCipher, boolean z, int i, short[] sArr, int i2, int i3) {
        checkCipher(blockCipher);
        if (i < 2 || i > 65536) {
            throw new IllegalArgumentException();
        }
        checkData(z, i, sArr, i2, i3);
    }

    protected static void checkArgs(BlockCipher blockCipher, boolean z, int i, byte[] bArr, int i2, int i3) {
        checkCipher(blockCipher);
        if (i < 2 || i > 256) {
            throw new IllegalArgumentException();
        }
        checkData(z, i, bArr, i2, i3);
    }

    protected static void checkCipher(BlockCipher blockCipher) {
        if (16 != blockCipher.getBlockSize()) {
            throw new IllegalArgumentException();
        }
    }

    protected static void checkData(boolean z, int i, short[] sArr, int i2, int i3) {
        checkLength(z, i, i3);
        for (int i4 = 0; i4 < i3; i4++) {
            if ((sArr[i2 + i4] & 65535) >= i) {
                throw new IllegalArgumentException("input data outside of radix");
            }
        }
    }

    protected static void checkData(boolean z, int i, byte[] bArr, int i2, int i3) {
        checkLength(z, i, i3);
        for (int i4 = 0; i4 < i3; i4++) {
            if ((bArr[i2 + i4] & 255) >= i) {
                throw new IllegalArgumentException("input data outside of radix");
            }
        }
    }

    private static void checkLength(boolean z, int i, int i2) {
        int iFloor;
        if (i2 < 2 || Math.pow(i, i2) < 1000000.0d) {
            throw new IllegalArgumentException("input too short");
        }
        if (!z && i2 > (iFloor = 2 * ((int) Math.floor(Math.log(TWO_TO_96) / Math.log(i))))) {
            throw new IllegalArgumentException("maximum input length is " + iFloor);
        }
    }

    protected static byte[] implDecryptFF3(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, byte[] bArr2, int i, int i2) {
        int i3 = i2 / 2;
        int i4 = i2 - i3;
        return toByte(decFF3_1(blockCipher, radixConverter, bArr, i2, i3, i4, toShort(bArr2, i, i4), toShort(bArr2, i + i4, i3)));
    }

    protected static short[] implDecryptFF3w(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, short[] sArr, int i, int i2) {
        int i3 = i2 / 2;
        int i4 = i2 - i3;
        short[] sArr2 = new short[i4];
        short[] sArr3 = new short[i3];
        System.arraycopy(sArr, i, sArr2, 0, i4);
        System.arraycopy(sArr, i + i4, sArr3, 0, i3);
        return decFF3_1(blockCipher, radixConverter, bArr, i2, i3, i4, sArr2, sArr3);
    }

    private static short[] decFF3_1(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, int i, int i2, int i3, short[] sArr, short[] sArr2) {
        BigInteger[] bigIntegerArrCalculateModUV = calculateModUV(BigInteger.valueOf(radixConverter.getRadix()), i2, i3);
        int i4 = i3;
        Arrays.reverseInPlace(sArr);
        Arrays.reverseInPlace(sArr2);
        for (int i5 = 7; i5 >= 0; i5--) {
            i4 = i - i4;
            BigInteger bigIntegerMod = radixConverter.fromEncoding(sArr2).subtract(calculateY_FF3(blockCipher, bArr, 4 - ((i5 & 1) * 4), i5, sArr, radixConverter)).mod(bigIntegerArrCalculateModUV[1 - (i5 & 1)]);
            short[] sArr3 = sArr2;
            sArr2 = sArr;
            sArr = sArr3;
            radixConverter.toEncoding(bigIntegerMod, i4, sArr3);
        }
        Arrays.reverseInPlace(sArr);
        Arrays.reverseInPlace(sArr2);
        return Arrays.concatenate(sArr, sArr2);
    }

    protected static byte[] implEncryptFF3(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, byte[] bArr2, int i, int i2) {
        int i3 = i2 / 2;
        int i4 = i2 - i3;
        return toByte(encFF3_1(blockCipher, radixConverter, bArr, i2, i3, i4, toShort(bArr2, i, i4), toShort(bArr2, i + i4, i3)));
    }

    protected static short[] implEncryptFF3w(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, short[] sArr, int i, int i2) {
        int i3 = i2 / 2;
        int i4 = i2 - i3;
        short[] sArr2 = new short[i4];
        short[] sArr3 = new short[i3];
        System.arraycopy(sArr, i, sArr2, 0, i4);
        System.arraycopy(sArr, i + i4, sArr3, 0, i3);
        return encFF3_1(blockCipher, radixConverter, bArr, i2, i3, i4, sArr2, sArr3);
    }

    private static short[] encFF3_1(BlockCipher blockCipher, RadixConverter radixConverter, byte[] bArr, int i, int i2, int i3, short[] sArr, short[] sArr2) {
        BigInteger[] bigIntegerArrCalculateModUV = calculateModUV(BigInteger.valueOf(radixConverter.getRadix()), i2, i3);
        int i4 = i2;
        Arrays.reverseInPlace(sArr);
        Arrays.reverseInPlace(sArr2);
        for (int i5 = 0; i5 < 8; i5++) {
            i4 = i - i4;
            BigInteger bigIntegerMod = radixConverter.fromEncoding(sArr).add(calculateY_FF3(blockCipher, bArr, 4 - ((i5 & 1) * 4), i5, sArr2, radixConverter)).mod(bigIntegerArrCalculateModUV[1 - (i5 & 1)]);
            short[] sArr3 = sArr;
            sArr = sArr2;
            sArr2 = sArr3;
            radixConverter.toEncoding(bigIntegerMod, i4, sArr3);
        }
        Arrays.reverseInPlace(sArr);
        Arrays.reverseInPlace(sArr2);
        return Arrays.concatenate(sArr, sArr2);
    }

    protected static BigInteger num(byte[] bArr, int i, int i2) {
        return new BigInteger(1, Arrays.copyOfRange(bArr, i, i + i2));
    }

    protected static byte[] prf(BlockCipher blockCipher, byte[] bArr) {
        if (bArr.length % 16 != 0) {
            throw new IllegalArgumentException();
        }
        int length = bArr.length / 16;
        byte[] bArr2 = new byte[16];
        for (int i = 0; i < length; i++) {
            Bytes.xorTo(16, bArr, i * 16, bArr2, 0);
            blockCipher.processBlock(bArr2, 0, bArr2, 0);
        }
        return bArr2;
    }

    private static byte[] toByte(short[] sArr) {
        byte[] bArr = new byte[sArr.length];
        for (int i = 0; i != bArr.length; i++) {
            bArr[i] = (byte) sArr[i];
        }
        return bArr;
    }

    private static short[] toShort(byte[] bArr, int i, int i2) {
        short[] sArr = new short[i2];
        for (int i3 = 0; i3 != sArr.length; i3++) {
            sArr[i3] = (short) (bArr[i + i3] & 255);
        }
        return sArr;
    }
}
