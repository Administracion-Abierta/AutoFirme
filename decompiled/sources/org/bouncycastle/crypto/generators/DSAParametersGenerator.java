package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAValidationParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/DSAParametersGenerator.class */
public class DSAParametersGenerator {
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private Digest digest;
    private int L;
    private int N;
    private int certainty;
    private int iterations;
    private SecureRandom random;
    private boolean use186_3;
    private int usageIndex;

    public DSAParametersGenerator() {
        this(DigestFactory.createSHA1());
    }

    public DSAParametersGenerator(Digest digest) {
        this.digest = digest;
    }

    public void init(int i, int i2, SecureRandom secureRandom) {
        this.L = i;
        this.N = getDefaultN(i);
        this.certainty = i2;
        this.iterations = Math.max(getMinimumIterations(this.L), (i2 + 1) / 2);
        this.random = secureRandom;
        this.use186_3 = false;
        this.usageIndex = -1;
    }

    public void init(DSAParameterGenerationParameters dSAParameterGenerationParameters) {
        int l = dSAParameterGenerationParameters.getL();
        int n = dSAParameterGenerationParameters.getN();
        if (l < 1024 || l > 3072 || l % 1024 != 0) {
            throw new IllegalArgumentException("L values must be between 1024 and 3072 and a multiple of 1024");
        }
        if (l == 1024 && n != 160) {
            throw new IllegalArgumentException("N must be 160 for L = 1024");
        }
        if (l == 2048 && n != 224 && n != 256) {
            throw new IllegalArgumentException("N must be 224 or 256 for L = 2048");
        }
        if (l == 3072 && n != 256) {
            throw new IllegalArgumentException("N must be 256 for L = 3072");
        }
        if (this.digest.getDigestSize() * 8 < n) {
            throw new IllegalStateException("Digest output size too small for value of N");
        }
        this.L = l;
        this.N = n;
        this.certainty = dSAParameterGenerationParameters.getCertainty();
        this.iterations = Math.max(getMinimumIterations(l), (this.certainty + 1) / 2);
        this.random = dSAParameterGenerationParameters.getRandom();
        this.use186_3 = true;
        this.usageIndex = dSAParameterGenerationParameters.getUsageIndex();
    }

    public DSAParameters generateParameters() {
        return this.use186_3 ? generateParameters_FIPS186_3() : generateParameters_FIPS186_2();
    }

    private DSAParameters generateParameters_FIPS186_2() {
        byte[] bArr = new byte[20];
        byte[] bArr2 = new byte[20];
        byte[] bArr3 = new byte[20];
        byte[] bArr4 = new byte[20];
        int i = (this.L - 1) / 160;
        byte[] bArr5 = new byte[this.L / 8];
        if (!(this.digest instanceof SHA1Digest)) {
            throw new IllegalStateException("can only use SHA-1 for generating FIPS 186-2 parameters");
        }
        while (true) {
            this.random.nextBytes(bArr);
            hash(this.digest, bArr, bArr2, 0);
            System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
            inc(bArr3);
            hash(this.digest, bArr3, bArr3, 0);
            for (int i2 = 0; i2 != bArr4.length; i2++) {
                bArr4[i2] = (byte) (bArr2[i2] ^ bArr3[i2]);
            }
            bArr4[0] = (byte) (bArr4[0] | (-128));
            bArr4[19] = (byte) (bArr4[19] | 1);
            BigInteger bigInteger = new BigInteger(1, bArr4);
            if (isProbablePrime(bigInteger)) {
                byte[] bArrClone = Arrays.clone(bArr);
                inc(bArrClone);
                for (int i3 = 0; i3 < 4096; i3++) {
                    for (int i4 = 1; i4 <= i; i4++) {
                        inc(bArrClone);
                        hash(this.digest, bArrClone, bArr5, bArr5.length - (i4 * bArr2.length));
                    }
                    int length = bArr5.length - (i * bArr2.length);
                    inc(bArrClone);
                    hash(this.digest, bArrClone, bArr2, 0);
                    System.arraycopy(bArr2, bArr2.length - length, bArr5, 0, length);
                    bArr5[0] = (byte) (bArr5[0] | (-128));
                    BigInteger bigInteger2 = new BigInteger(1, bArr5);
                    BigInteger bigIntegerSubtract = bigInteger2.subtract(bigInteger2.mod(bigInteger.shiftLeft(1)).subtract(ONE));
                    if (bigIntegerSubtract.bitLength() == this.L && isProbablePrime(bigIntegerSubtract)) {
                        return new DSAParameters(bigIntegerSubtract, bigInteger, calculateGenerator_FIPS186_2(bigIntegerSubtract, bigInteger, this.random), new DSAValidationParameters(bArr, i3));
                    }
                }
            }
        }
    }

    private static BigInteger calculateGenerator_FIPS186_2(BigInteger bigInteger, BigInteger bigInteger2, SecureRandom secureRandom) {
        BigInteger bigIntegerModPow;
        BigInteger bigIntegerDivide = bigInteger.subtract(ONE).divide(bigInteger2);
        BigInteger bigIntegerSubtract = bigInteger.subtract(TWO);
        do {
            bigIntegerModPow = BigIntegers.createRandomInRange(TWO, bigIntegerSubtract, secureRandom).modPow(bigIntegerDivide, bigInteger);
        } while (bigIntegerModPow.bitLength() <= 1);
        return bigIntegerModPow;
    }

    private DSAParameters generateParameters_FIPS186_3() {
        BigInteger bit;
        int i;
        BigInteger bigIntegerSubtract;
        BigInteger bigIntegerCalculateGenerator_FIPS186_3_Verifiable;
        Digest digest = this.digest;
        int digestSize = digest.getDigestSize() * 8;
        byte[] bArr = new byte[this.N / 8];
        int i2 = (this.L - 1) / digestSize;
        int i3 = (this.L - 1) % digestSize;
        byte[] bArr2 = new byte[this.L / 8];
        byte[] bArr3 = new byte[digest.getDigestSize()];
        loop0: while (true) {
            this.random.nextBytes(bArr);
            hash(digest, bArr, bArr3, 0);
            bit = new BigInteger(1, bArr3).mod(ONE.shiftLeft(this.N - 1)).setBit(0).setBit(this.N - 1);
            if (isProbablePrime(bit)) {
                byte[] bArrClone = Arrays.clone(bArr);
                int i4 = 4 * this.L;
                i = 0;
                while (i < i4) {
                    for (int i5 = 1; i5 <= i2; i5++) {
                        inc(bArrClone);
                        hash(digest, bArrClone, bArr2, bArr2.length - (i5 * bArr3.length));
                    }
                    int length = bArr2.length - (i2 * bArr3.length);
                    inc(bArrClone);
                    hash(digest, bArrClone, bArr3, 0);
                    System.arraycopy(bArr3, bArr3.length - length, bArr2, 0, length);
                    bArr2[0] = (byte) (bArr2[0] | (-128));
                    BigInteger bigInteger = new BigInteger(1, bArr2);
                    bigIntegerSubtract = bigInteger.subtract(bigInteger.mod(bit.shiftLeft(1)).subtract(ONE));
                    if (bigIntegerSubtract.bitLength() == this.L && isProbablePrime(bigIntegerSubtract)) {
                        break loop0;
                    }
                    i++;
                }
            }
        }
        return (this.usageIndex < 0 || (bigIntegerCalculateGenerator_FIPS186_3_Verifiable = calculateGenerator_FIPS186_3_Verifiable(digest, bigIntegerSubtract, bit, bArr, this.usageIndex)) == null) ? new DSAParameters(bigIntegerSubtract, bit, calculateGenerator_FIPS186_3_Unverifiable(bigIntegerSubtract, bit, this.random), new DSAValidationParameters(bArr, i)) : new DSAParameters(bigIntegerSubtract, bit, bigIntegerCalculateGenerator_FIPS186_3_Verifiable, new DSAValidationParameters(bArr, i, this.usageIndex));
    }

    private boolean isProbablePrime(BigInteger bigInteger) {
        return bigInteger.isProbablePrime(this.certainty);
    }

    private static BigInteger calculateGenerator_FIPS186_3_Unverifiable(BigInteger bigInteger, BigInteger bigInteger2, SecureRandom secureRandom) {
        return calculateGenerator_FIPS186_2(bigInteger, bigInteger2, secureRandom);
    }

    private static BigInteger calculateGenerator_FIPS186_3_Verifiable(Digest digest, BigInteger bigInteger, BigInteger bigInteger2, byte[] bArr, int i) {
        BigInteger bigIntegerDivide = bigInteger.subtract(ONE).divide(bigInteger2);
        byte[] bArrDecodeStrict = Hex.decodeStrict("6767656E");
        byte[] bArr2 = new byte[bArr.length + bArrDecodeStrict.length + 1 + 2];
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        System.arraycopy(bArrDecodeStrict, 0, bArr2, bArr.length, bArrDecodeStrict.length);
        bArr2[bArr2.length - 3] = (byte) i;
        byte[] bArr3 = new byte[digest.getDigestSize()];
        for (int i2 = 1; i2 < 65536; i2++) {
            inc(bArr2);
            hash(digest, bArr2, bArr3, 0);
            BigInteger bigIntegerModPow = new BigInteger(1, bArr3).modPow(bigIntegerDivide, bigInteger);
            if (bigIntegerModPow.compareTo(TWO) >= 0) {
                return bigIntegerModPow;
            }
        }
        return null;
    }

    private static void hash(Digest digest, byte[] bArr, byte[] bArr2, int i) {
        digest.update(bArr, 0, bArr.length);
        digest.doFinal(bArr2, i);
    }

    private static int getDefaultN(int i) {
        return i > 1024 ? 256 : 160;
    }

    private static int getMinimumIterations(int i) {
        if (i <= 1024) {
            return 40;
        }
        return 48 + (8 * ((i - 1) / 1024));
    }

    private static void inc(byte[] bArr) {
        for (int length = bArr.length - 1; length >= 0; length--) {
            byte b = (byte) ((bArr[length] + 1) & 255);
            bArr[length] = b;
            if (b != 0) {
                return;
            }
        }
    }
}
