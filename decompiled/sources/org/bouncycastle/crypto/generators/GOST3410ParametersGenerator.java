package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410ValidationParameters;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/GOST3410ParametersGenerator.class */
public class GOST3410ParametersGenerator {
    private int size;
    private int typeproc;
    private SecureRandom init_random;
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    public void init(int i, int i2, SecureRandom secureRandom) {
        this.size = i;
        this.typeproc = i2;
        this.init_random = secureRandom;
    }

    private int procedure_A(int i, int i2, BigInteger[] bigIntegerArr, int i3) {
        while (true) {
            if (i >= 0 && i <= 65536) {
                break;
            }
            i = this.init_random.nextInt() / 32768;
        }
        while (true) {
            if (i2 >= 0 && i2 <= 65536 && i2 / 2 != 0) {
                break;
            }
            i2 = (this.init_random.nextInt() / 32768) + 1;
        }
        BigInteger bigInteger = new BigInteger(Integer.toString(i2));
        BigInteger bigInteger2 = new BigInteger("19381");
        BigInteger[] bigIntegerArr2 = {new BigInteger(Integer.toString(i))};
        int[] iArr = {i3};
        int i4 = 0;
        for (int i5 = 0; iArr[i5] >= 17; i5++) {
            int[] iArr2 = new int[iArr.length + 1];
            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
            iArr = new int[iArr2.length];
            System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
            iArr[i5 + 1] = iArr[i5] / 2;
            i4 = i5 + 1;
        }
        BigInteger[] bigIntegerArr3 = new BigInteger[i4 + 1];
        bigIntegerArr3[i4] = new BigInteger("8003", 16);
        int i6 = i4 - 1;
        for (int i7 = 0; i7 < i4; i7++) {
            int i8 = iArr[i6] / 16;
            while (true) {
                BigInteger[] bigIntegerArr4 = new BigInteger[bigIntegerArr2.length];
                System.arraycopy(bigIntegerArr2, 0, bigIntegerArr4, 0, bigIntegerArr2.length);
                bigIntegerArr2 = new BigInteger[i8 + 1];
                System.arraycopy(bigIntegerArr4, 0, bigIntegerArr2, 0, bigIntegerArr4.length);
                for (int i9 = 0; i9 < i8; i9++) {
                    bigIntegerArr2[i9 + 1] = bigIntegerArr2[i9].multiply(bigInteger2).add(bigInteger).mod(TWO.pow(16));
                }
                BigInteger bigInteger3 = new BigInteger("0");
                for (int i10 = 0; i10 < i8; i10++) {
                    bigInteger3 = bigInteger3.add(bigIntegerArr2[i10].multiply(TWO.pow(16 * i10)));
                }
                bigIntegerArr2[0] = bigIntegerArr2[i8];
                BigInteger bigIntegerAdd = TWO.pow(iArr[i6] - 1).divide(bigIntegerArr3[i6 + 1]).add(TWO.pow(iArr[i6] - 1).multiply(bigInteger3).divide(bigIntegerArr3[i6 + 1].multiply(TWO.pow(16 * i8))));
                if (bigIntegerAdd.mod(TWO).compareTo(ONE) == 0) {
                    bigIntegerAdd = bigIntegerAdd.add(ONE);
                }
                int i11 = 0;
                while (true) {
                    bigIntegerArr3[i6] = bigIntegerArr3[i6 + 1].multiply(bigIntegerAdd.add(BigInteger.valueOf(i11))).add(ONE);
                    if (bigIntegerArr3[i6].compareTo(TWO.pow(iArr[i6])) != 1) {
                        if (TWO.modPow(bigIntegerArr3[i6 + 1].multiply(bigIntegerAdd.add(BigInteger.valueOf(i11))), bigIntegerArr3[i6]).compareTo(ONE) == 0 && TWO.modPow(bigIntegerAdd.add(BigInteger.valueOf(i11)), bigIntegerArr3[i6]).compareTo(ONE) != 0) {
                            break;
                        }
                        i11 += 2;
                    }
                }
            }
            i6--;
            if (i6 < 0) {
                bigIntegerArr[0] = bigIntegerArr3[0];
                bigIntegerArr[1] = bigIntegerArr3[1];
                return bigIntegerArr2[0].intValue();
            }
        }
        return bigIntegerArr2[0].intValue();
    }

    private long procedure_Aa(long j, long j2, BigInteger[] bigIntegerArr, int i) {
        while (true) {
            if (j >= 0 && j <= 4294967296L) {
                break;
            }
            j = this.init_random.nextInt() * 2;
        }
        while (true) {
            if (j2 >= 0 && j2 <= 4294967296L && j2 / 2 != 0) {
                break;
            }
            j2 = (this.init_random.nextInt() * 2) + 1;
        }
        BigInteger bigInteger = new BigInteger(Long.toString(j2));
        BigInteger bigInteger2 = new BigInteger("97781173");
        BigInteger[] bigIntegerArr2 = {new BigInteger(Long.toString(j))};
        int[] iArr = {i};
        int i2 = 0;
        for (int i3 = 0; iArr[i3] >= 33; i3++) {
            int[] iArr2 = new int[iArr.length + 1];
            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
            iArr = new int[iArr2.length];
            System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
            iArr[i3 + 1] = iArr[i3] / 2;
            i2 = i3 + 1;
        }
        BigInteger[] bigIntegerArr3 = new BigInteger[i2 + 1];
        bigIntegerArr3[i2] = new BigInteger("8000000B", 16);
        int i4 = i2 - 1;
        for (int i5 = 0; i5 < i2; i5++) {
            int i6 = iArr[i4] / 32;
            while (true) {
                BigInteger[] bigIntegerArr4 = new BigInteger[bigIntegerArr2.length];
                System.arraycopy(bigIntegerArr2, 0, bigIntegerArr4, 0, bigIntegerArr2.length);
                bigIntegerArr2 = new BigInteger[i6 + 1];
                System.arraycopy(bigIntegerArr4, 0, bigIntegerArr2, 0, bigIntegerArr4.length);
                for (int i7 = 0; i7 < i6; i7++) {
                    bigIntegerArr2[i7 + 1] = bigIntegerArr2[i7].multiply(bigInteger2).add(bigInteger).mod(TWO.pow(32));
                }
                BigInteger bigInteger3 = new BigInteger("0");
                for (int i8 = 0; i8 < i6; i8++) {
                    bigInteger3 = bigInteger3.add(bigIntegerArr2[i8].multiply(TWO.pow(32 * i8)));
                }
                bigIntegerArr2[0] = bigIntegerArr2[i6];
                BigInteger bigIntegerAdd = TWO.pow(iArr[i4] - 1).divide(bigIntegerArr3[i4 + 1]).add(TWO.pow(iArr[i4] - 1).multiply(bigInteger3).divide(bigIntegerArr3[i4 + 1].multiply(TWO.pow(32 * i6))));
                if (bigIntegerAdd.mod(TWO).compareTo(ONE) == 0) {
                    bigIntegerAdd = bigIntegerAdd.add(ONE);
                }
                int i9 = 0;
                while (true) {
                    bigIntegerArr3[i4] = bigIntegerArr3[i4 + 1].multiply(bigIntegerAdd.add(BigInteger.valueOf(i9))).add(ONE);
                    if (bigIntegerArr3[i4].compareTo(TWO.pow(iArr[i4])) != 1) {
                        if (TWO.modPow(bigIntegerArr3[i4 + 1].multiply(bigIntegerAdd.add(BigInteger.valueOf(i9))), bigIntegerArr3[i4]).compareTo(ONE) == 0 && TWO.modPow(bigIntegerAdd.add(BigInteger.valueOf(i9)), bigIntegerArr3[i4]).compareTo(ONE) != 0) {
                            break;
                        }
                        i9 += 2;
                    }
                }
            }
            i4--;
            if (i4 < 0) {
                bigIntegerArr[0] = bigIntegerArr3[0];
                bigIntegerArr[1] = bigIntegerArr3[1];
                return bigIntegerArr2[0].longValue();
            }
        }
        return bigIntegerArr2[0].longValue();
    }

    private void procedure_B(int i, int i2, BigInteger[] bigIntegerArr) {
        while (true) {
            if (i >= 0 && i <= 65536) {
                break;
            } else {
                i = this.init_random.nextInt() / 32768;
            }
        }
        while (true) {
            if (i2 >= 0 && i2 <= 65536 && i2 / 2 != 0) {
                break;
            } else {
                i2 = (this.init_random.nextInt() / 32768) + 1;
            }
        }
        BigInteger[] bigIntegerArr2 = new BigInteger[2];
        BigInteger bigInteger = new BigInteger(Integer.toString(i2));
        BigInteger bigInteger2 = new BigInteger("19381");
        int iProcedure_A = procedure_A(i, i2, bigIntegerArr2, 256);
        BigInteger bigInteger3 = bigIntegerArr2[0];
        int iProcedure_A2 = procedure_A(iProcedure_A, i2, bigIntegerArr2, 512);
        BigInteger bigInteger4 = bigIntegerArr2[0];
        BigInteger[] bigIntegerArr3 = new BigInteger[65];
        bigIntegerArr3[0] = new BigInteger(Integer.toString(iProcedure_A2));
        while (true) {
            for (int i3 = 0; i3 < 64; i3++) {
                bigIntegerArr3[i3 + 1] = bigIntegerArr3[i3].multiply(bigInteger2).add(bigInteger).mod(TWO.pow(16));
            }
            BigInteger bigInteger5 = new BigInteger("0");
            for (int i4 = 0; i4 < 64; i4++) {
                bigInteger5 = bigInteger5.add(bigIntegerArr3[i4].multiply(TWO.pow(16 * i4)));
            }
            bigIntegerArr3[0] = bigIntegerArr3[64];
            BigInteger bigIntegerAdd = TWO.pow(1024 - 1).divide(bigInteger3.multiply(bigInteger4)).add(TWO.pow(1024 - 1).multiply(bigInteger5).divide(bigInteger3.multiply(bigInteger4).multiply(TWO.pow(1024))));
            if (bigIntegerAdd.mod(TWO).compareTo(ONE) == 0) {
                bigIntegerAdd = bigIntegerAdd.add(ONE);
            }
            int i5 = 0;
            while (true) {
                BigInteger bigIntegerAdd2 = bigInteger3.multiply(bigInteger4).multiply(bigIntegerAdd.add(BigInteger.valueOf(i5))).add(ONE);
                if (bigIntegerAdd2.compareTo(TWO.pow(1024)) == 1) {
                    break;
                }
                if (TWO.modPow(bigInteger3.multiply(bigInteger4).multiply(bigIntegerAdd.add(BigInteger.valueOf(i5))), bigIntegerAdd2).compareTo(ONE) == 0 && TWO.modPow(bigInteger3.multiply(bigIntegerAdd.add(BigInteger.valueOf(i5))), bigIntegerAdd2).compareTo(ONE) != 0) {
                    bigIntegerArr[0] = bigIntegerAdd2;
                    bigIntegerArr[1] = bigInteger3;
                    return;
                }
                i5 += 2;
            }
        }
    }

    private void procedure_Bb(long j, long j2, BigInteger[] bigIntegerArr) {
        while (true) {
            if (j >= 0 && j <= 4294967296L) {
                break;
            } else {
                j = this.init_random.nextInt() * 2;
            }
        }
        while (true) {
            if (j2 >= 0 && j2 <= 4294967296L && j2 / 2 != 0) {
                break;
            } else {
                j2 = (this.init_random.nextInt() * 2) + 1;
            }
        }
        BigInteger[] bigIntegerArr2 = new BigInteger[2];
        BigInteger bigInteger = new BigInteger(Long.toString(j2));
        BigInteger bigInteger2 = new BigInteger("97781173");
        long jProcedure_Aa = procedure_Aa(j, j2, bigIntegerArr2, 256);
        BigInteger bigInteger3 = bigIntegerArr2[0];
        long jProcedure_Aa2 = procedure_Aa(jProcedure_Aa, j2, bigIntegerArr2, 512);
        BigInteger bigInteger4 = bigIntegerArr2[0];
        BigInteger[] bigIntegerArr3 = new BigInteger[33];
        bigIntegerArr3[0] = new BigInteger(Long.toString(jProcedure_Aa2));
        while (true) {
            for (int i = 0; i < 32; i++) {
                bigIntegerArr3[i + 1] = bigIntegerArr3[i].multiply(bigInteger2).add(bigInteger).mod(TWO.pow(32));
            }
            BigInteger bigInteger5 = new BigInteger("0");
            for (int i2 = 0; i2 < 32; i2++) {
                bigInteger5 = bigInteger5.add(bigIntegerArr3[i2].multiply(TWO.pow(32 * i2)));
            }
            bigIntegerArr3[0] = bigIntegerArr3[32];
            BigInteger bigIntegerAdd = TWO.pow(1024 - 1).divide(bigInteger3.multiply(bigInteger4)).add(TWO.pow(1024 - 1).multiply(bigInteger5).divide(bigInteger3.multiply(bigInteger4).multiply(TWO.pow(1024))));
            if (bigIntegerAdd.mod(TWO).compareTo(ONE) == 0) {
                bigIntegerAdd = bigIntegerAdd.add(ONE);
            }
            int i3 = 0;
            while (true) {
                BigInteger bigIntegerAdd2 = bigInteger3.multiply(bigInteger4).multiply(bigIntegerAdd.add(BigInteger.valueOf(i3))).add(ONE);
                if (bigIntegerAdd2.compareTo(TWO.pow(1024)) == 1) {
                    break;
                }
                if (TWO.modPow(bigInteger3.multiply(bigInteger4).multiply(bigIntegerAdd.add(BigInteger.valueOf(i3))), bigIntegerAdd2).compareTo(ONE) == 0 && TWO.modPow(bigInteger3.multiply(bigIntegerAdd.add(BigInteger.valueOf(i3))), bigIntegerAdd2).compareTo(ONE) != 0) {
                    bigIntegerArr[0] = bigIntegerAdd2;
                    bigIntegerArr[1] = bigInteger3;
                    return;
                }
                i3 += 2;
            }
        }
    }

    private BigInteger procedure_C(BigInteger bigInteger, BigInteger bigInteger2) {
        BigInteger bigIntegerSubtract = bigInteger.subtract(ONE);
        BigInteger bigIntegerDivide = bigIntegerSubtract.divide(bigInteger2);
        int iBitLength = bigInteger.bitLength();
        while (true) {
            BigInteger bigIntegerCreateRandomBigInteger = BigIntegers.createRandomBigInteger(iBitLength, this.init_random);
            if (bigIntegerCreateRandomBigInteger.compareTo(ONE) > 0 && bigIntegerCreateRandomBigInteger.compareTo(bigIntegerSubtract) < 0) {
                BigInteger bigIntegerModPow = bigIntegerCreateRandomBigInteger.modPow(bigIntegerDivide, bigInteger);
                if (bigIntegerModPow.compareTo(ONE) != 0) {
                    return bigIntegerModPow;
                }
            }
        }
    }

    public GOST3410Parameters generateParameters() {
        BigInteger[] bigIntegerArr = new BigInteger[2];
        if (this.typeproc == 1) {
            int iNextInt = this.init_random.nextInt();
            int iNextInt2 = this.init_random.nextInt();
            switch (this.size) {
                case 512:
                    procedure_A(iNextInt, iNextInt2, bigIntegerArr, 512);
                    break;
                case 1024:
                    procedure_B(iNextInt, iNextInt2, bigIntegerArr);
                    break;
                default:
                    throw new IllegalArgumentException("Ooops! key size 512 or 1024 bit.");
            }
            BigInteger bigInteger = bigIntegerArr[0];
            BigInteger bigInteger2 = bigIntegerArr[1];
            return new GOST3410Parameters(bigInteger, bigInteger2, procedure_C(bigInteger, bigInteger2), new GOST3410ValidationParameters(iNextInt, iNextInt2));
        }
        long jNextLong = this.init_random.nextLong();
        long jNextLong2 = this.init_random.nextLong();
        switch (this.size) {
            case 512:
                procedure_Aa(jNextLong, jNextLong2, bigIntegerArr, 512);
                break;
            case 1024:
                procedure_Bb(jNextLong, jNextLong2, bigIntegerArr);
                break;
            default:
                throw new IllegalStateException("Ooops! key size 512 or 1024 bit.");
        }
        BigInteger bigInteger3 = bigIntegerArr[0];
        BigInteger bigInteger4 = bigIntegerArr[1];
        return new GOST3410Parameters(bigInteger3, bigInteger4, procedure_C(bigInteger3, bigInteger4), new GOST3410ValidationParameters(jNextLong, jNextLong2));
    }
}
