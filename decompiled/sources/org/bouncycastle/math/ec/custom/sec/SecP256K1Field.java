package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecP256K1Field.class */
public class SecP256K1Field {
    static final int[] P = {-977, -2, -1, -1, -1, -1, -1, -1};
    private static final int[] PExt = {954529, 1954, 1, 0, 0, 0, 0, 0, -1954, -3, -1, -1, -1, -1, -1, -1};
    private static final int[] PExtInv = {-954529, -1955, -2, -1, -1, -1, -1, -1, 1953, 2};
    private static final int P7 = -1;
    private static final int PExt15 = -1;
    private static final int PInv33 = 977;

    public static void add(int[] iArr, int[] iArr2, int[] iArr3) {
        if (Nat256.add(iArr, iArr2, iArr3) != 0 || (iArr3[7] == -1 && Nat256.gte(iArr3, P))) {
            Nat.add33To(8, PInv33, iArr3);
        }
    }

    public static void addExt(int[] iArr, int[] iArr2, int[] iArr3) {
        if ((Nat.add(16, iArr, iArr2, iArr3) != 0 || (iArr3[15] == -1 && Nat.gte(16, iArr3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, iArr3) != 0) {
            Nat.incAt(16, iArr3, PExtInv.length);
        }
    }

    public static void addOne(int[] iArr, int[] iArr2) {
        if (Nat.inc(8, iArr, iArr2) != 0 || (iArr2[7] == -1 && Nat256.gte(iArr2, P))) {
            Nat.add33To(8, PInv33, iArr2);
        }
    }

    public static int[] fromBigInteger(BigInteger bigInteger) {
        int[] iArrFromBigInteger = Nat256.fromBigInteger(bigInteger);
        if (iArrFromBigInteger[7] == -1 && Nat256.gte(iArrFromBigInteger, P)) {
            Nat256.subFrom(P, iArrFromBigInteger);
        }
        return iArrFromBigInteger;
    }

    public static void half(int[] iArr, int[] iArr2) {
        if ((iArr[0] & 1) == 0) {
            Nat.shiftDownBit(8, iArr, 0, iArr2);
        } else {
            Nat.shiftDownBit(8, iArr2, Nat256.add(iArr, P, iArr2));
        }
    }

    public static void inv(int[] iArr, int[] iArr2) {
        Mod.checkedModOddInverse(P, iArr, iArr2);
    }

    public static int isZero(int[] iArr) {
        int i = 0;
        for (int i2 = 0; i2 < 8; i2++) {
            i |= iArr[i2];
        }
        return (((i >>> 1) | (i & 1)) - 1) >> 31;
    }

    public static void multiply(int[] iArr, int[] iArr2, int[] iArr3) {
        int[] iArrCreateExt = Nat256.createExt();
        Nat256.mul(iArr, iArr2, iArrCreateExt);
        reduce(iArrCreateExt, iArr3);
    }

    public static void multiply(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        Nat256.mul(iArr, iArr2, iArr4);
        reduce(iArr4, iArr3);
    }

    public static void multiplyAddToExt(int[] iArr, int[] iArr2, int[] iArr3) {
        if ((Nat256.mulAddTo(iArr, iArr2, iArr3) != 0 || (iArr3[15] == -1 && Nat.gte(16, iArr3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, iArr3) != 0) {
            Nat.incAt(16, iArr3, PExtInv.length);
        }
    }

    public static void negate(int[] iArr, int[] iArr2) {
        if (0 != isZero(iArr)) {
            Nat256.sub(P, P, iArr2);
        } else {
            Nat256.sub(P, iArr, iArr2);
        }
    }

    public static void random(SecureRandom secureRandom, int[] iArr) {
        byte[] bArr = new byte[32];
        do {
            secureRandom.nextBytes(bArr);
            Pack.littleEndianToInt(bArr, 0, iArr, 0, 8);
        } while (0 == Nat.lessThan(8, iArr, P));
    }

    public static void randomMult(SecureRandom secureRandom, int[] iArr) {
        do {
            random(secureRandom, iArr);
        } while (0 != isZero(iArr));
    }

    public static void reduce(int[] iArr, int[] iArr2) {
        if (Nat256.mul33DWordAdd(PInv33, Nat256.mul33Add(PInv33, iArr, 8, iArr, 0, iArr2, 0), iArr2, 0) != 0 || (iArr2[7] == -1 && Nat256.gte(iArr2, P))) {
            Nat.add33To(8, PInv33, iArr2);
        }
    }

    public static void reduce32(int i, int[] iArr) {
        if ((i == 0 || Nat256.mul33WordAdd(PInv33, i, iArr, 0) == 0) && !(iArr[7] == -1 && Nat256.gte(iArr, P))) {
            return;
        }
        Nat.add33To(8, PInv33, iArr);
    }

    public static void square(int[] iArr, int[] iArr2) {
        int[] iArrCreateExt = Nat256.createExt();
        Nat256.square(iArr, iArrCreateExt);
        reduce(iArrCreateExt, iArr2);
    }

    public static void square(int[] iArr, int[] iArr2, int[] iArr3) {
        Nat256.square(iArr, iArr3);
        reduce(iArr3, iArr2);
    }

    public static void squareN(int[] iArr, int i, int[] iArr2) {
        int[] iArrCreateExt = Nat256.createExt();
        Nat256.square(iArr, iArrCreateExt);
        reduce(iArrCreateExt, iArr2);
        while (true) {
            i--;
            if (i <= 0) {
                return;
            }
            Nat256.square(iArr2, iArrCreateExt);
            reduce(iArrCreateExt, iArr2);
        }
    }

    public static void squareN(int[] iArr, int i, int[] iArr2, int[] iArr3) {
        Nat256.square(iArr, iArr3);
        reduce(iArr3, iArr2);
        while (true) {
            i--;
            if (i <= 0) {
                return;
            }
            Nat256.square(iArr2, iArr3);
            reduce(iArr3, iArr2);
        }
    }

    public static void subtract(int[] iArr, int[] iArr2, int[] iArr3) {
        if (Nat256.sub(iArr, iArr2, iArr3) != 0) {
            Nat.sub33From(8, PInv33, iArr3);
        }
    }

    public static void subtractExt(int[] iArr, int[] iArr2, int[] iArr3) {
        if (Nat.sub(16, iArr, iArr2, iArr3) == 0 || Nat.subFrom(PExtInv.length, PExtInv, iArr3) == 0) {
            return;
        }
        Nat.decAt(16, iArr3, PExtInv.length);
    }

    public static void twice(int[] iArr, int[] iArr2) {
        if (Nat.shiftUpBit(8, iArr, 0, iArr2) != 0 || (iArr2[7] == -1 && Nat256.gte(iArr2, P))) {
            Nat.add33To(8, PInv33, iArr2);
        }
    }
}
