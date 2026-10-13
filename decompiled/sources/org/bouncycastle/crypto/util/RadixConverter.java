package org.bouncycastle.crypto.util;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/util/RadixConverter.class */
public class RadixConverter {
    private static final double LOG_LONG_MAX_VALUE = Math.log(9.223372036854776E18d);
    private static final int DEFAULT_POWERS_TO_CACHE = 10;
    private final int digitsGroupLength;
    private final BigInteger digitsGroupSpaceSize;
    private final int radix;
    private final BigInteger[] digitsGroupSpacePowers;

    public RadixConverter(int i, int i2) {
        this.radix = i;
        this.digitsGroupLength = (int) Math.floor(LOG_LONG_MAX_VALUE / Math.log(i));
        this.digitsGroupSpaceSize = BigInteger.valueOf(i).pow(this.digitsGroupLength);
        this.digitsGroupSpacePowers = precomputeDigitsGroupPowers(i2, this.digitsGroupSpaceSize);
    }

    public RadixConverter(int i) {
        this(i, 10);
    }

    public int getRadix() {
        return this.radix;
    }

    public void toEncoding(BigInteger bigInteger, int i, short[] sArr) {
        if (bigInteger.signum() < 0) {
            throw new IllegalArgumentException();
        }
        int encoding = i - 1;
        do {
            if (bigInteger.equals(BigInteger.ZERO)) {
                int i2 = encoding;
                encoding--;
                sArr[i2] = 0;
            } else {
                BigInteger[] bigIntegerArrDivideAndRemainder = bigInteger.divideAndRemainder(this.digitsGroupSpaceSize);
                bigInteger = bigIntegerArrDivideAndRemainder[0];
                encoding = toEncoding(bigIntegerArrDivideAndRemainder[1].longValue(), encoding, sArr);
            }
        } while (encoding >= 0);
        if (bigInteger.signum() != 0) {
            throw new IllegalArgumentException();
        }
    }

    private int toEncoding(long j, int i, short[] sArr) {
        for (int i2 = 0; i2 < this.digitsGroupLength && i >= 0; i2++) {
            if (j == 0) {
                int i3 = i;
                i--;
                sArr[i3] = 0;
            } else {
                int i4 = i;
                i--;
                sArr[i4] = (short) (j % ((long) this.radix));
                j /= (long) this.radix;
            }
        }
        if (j != 0) {
            throw new IllegalStateException("Failed to convert decimal number");
        }
        return i;
    }

    public BigInteger fromEncoding(short[] sArr) {
        BigInteger bigIntegerAdd;
        BigInteger bigIntegerMultiply = BigIntegers.ONE;
        BigInteger bigInteger = null;
        int i = 0;
        int length = sArr.length;
        int i2 = length;
        int i3 = this.digitsGroupLength;
        while (true) {
            int i4 = i2 - i3;
            if (i4 <= (-this.digitsGroupLength)) {
                return bigInteger;
            }
            int i5 = this.digitsGroupLength;
            if (i4 < 0) {
                i5 = this.digitsGroupLength + i4;
                i4 = 0;
            }
            BigInteger bigIntegerValueOf = BigInteger.valueOf(fromEncoding(i4, Math.min(i4 + i5, length), sArr));
            if (i == 0) {
                bigIntegerAdd = bigIntegerValueOf;
            } else {
                bigIntegerMultiply = i <= this.digitsGroupSpacePowers.length ? this.digitsGroupSpacePowers[i - 1] : bigIntegerMultiply.multiply(this.digitsGroupSpaceSize);
                bigIntegerAdd = bigInteger.add(bigIntegerValueOf.multiply(bigIntegerMultiply));
            }
            bigInteger = bigIntegerAdd;
            i++;
            i2 = i4;
            i3 = this.digitsGroupLength;
        }
    }

    public int getDigitsGroupLength() {
        return this.digitsGroupLength;
    }

    private long fromEncoding(int i, int i2, short[] sArr) {
        long j = 0;
        for (int i3 = i; i3 < i2; i3++) {
            j = (j * ((long) this.radix)) + ((long) (sArr[i3] & 65535));
        }
        return j;
    }

    private BigInteger[] precomputeDigitsGroupPowers(int i, BigInteger bigInteger) {
        BigInteger[] bigIntegerArr = new BigInteger[i];
        BigInteger bigIntegerMultiply = bigInteger;
        for (int i2 = 0; i2 < i; i2++) {
            bigIntegerArr[i2] = bigIntegerMultiply;
            bigIntegerMultiply = bigIntegerMultiply.multiply(bigInteger);
        }
        return bigIntegerArr;
    }
}
