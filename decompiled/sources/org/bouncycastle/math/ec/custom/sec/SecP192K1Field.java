package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/ec/custom/sec/SecP192K1Field.class */
public class SecP192K1Field {
    static final int[] P = {-4553, -2, -1, -1, -1, -1};
    private static final int[] PExt = {20729809, 9106, 1, 0, 0, 0, -9106, -3, -1, -1, -1, -1};
    private static final int[] PExtInv = {-20729809, -9107, -2, -1, -1, -1, 9105, 2};
    private static final int P5 = -1;
    private static final int PExt11 = -1;
    private static final int PInv33 = 4553;

    public static void add(int[] iArr, int[] iArr2, int[] iArr3) {
        if (Nat192.add(iArr, iArr2, iArr3) != 0 || (iArr3[5] == -1 && Nat192.gte(iArr3, P))) {
            Nat.add33To(6, PInv33, iArr3);
        }
    }

    public static void addExt(int[] iArr, int[] iArr2, int[] iArr3) {
        if ((Nat.add(12, iArr, iArr2, iArr3) != 0 || (iArr3[11] == -1 && Nat.gte(12, iArr3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, iArr3) != 0) {
            Nat.incAt(12, iArr3, PExtInv.length);
        }
    }

    public static void addOne(int[] iArr, int[] iArr2) {
        if (Nat.inc(6, iArr, iArr2) != 0 || (iArr2[5] == -1 && Nat192.gte(iArr2, P))) {
            Nat.add33To(6, PInv33, iArr2);
        }
    }

    public static int[] fromBigInteger(BigInteger bigInteger) {
        int[] iArrFromBigInteger = Nat192.fromBigInteger(bigInteger);
        if (iArrFromBigInteger[5] == -1 && Nat192.gte(iArrFromBigInteger, P)) {
            Nat192.subFrom(P, iArrFromBigInteger);
        }
        return iArrFromBigInteger;
    }

    public static void half(int[] iArr, int[] iArr2) {
        if ((iArr[0] & 1) == 0) {
            Nat.shiftDownBit(6, iArr, 0, iArr2);
        } else {
            Nat.shiftDownBit(6, iArr2, Nat192.add(iArr, P, iArr2));
        }
    }

    public static void inv(int[] iArr, int[] iArr2) {
        Mod.checkedModOddInverse(P, iArr, iArr2);
    }

    public static int isZero(int[] iArr) {
        int i = 0;
        for (int i2 = 0; i2 < 6; i2++) {
            i |= iArr[i2];
        }
        return (((i >>> 1) | (i & 1)) - 1) >> 31;
    }

    public static void multiply(int[] iArr, int[] iArr2, int[] iArr3) {
        int[] iArrCreateExt = Nat192.createExt();
        Nat192.mul(iArr, iArr2, iArrCreateExt);
        reduce(iArrCreateExt, iArr3);
    }

    public static void multiplyAddToExt(int[] iArr, int[] iArr2, int[] iArr3) {
        if ((Nat192.mulAddTo(iArr, iArr2, iArr3) != 0 || (iArr3[11] == -1 && Nat.gte(12, iArr3, PExt))) && Nat.addTo(PExtInv.length, PExtInv, iArr3) != 0) {
            Nat.incAt(12, iArr3, PExtInv.length);
        }
    }

    public static void negate(int[] iArr, int[] iArr2) {
        if (0 != isZero(iArr)) {
            Nat192.sub(P, P, iArr2);
        } else {
            Nat192.sub(P, iArr, iArr2);
        }
    }

    public static void random(SecureRandom secureRandom, int[] iArr) {
        byte[] bArr = new byte[24];
        do {
            secureRandom.nextBytes(bArr);
            Pack.littleEndianToInt(bArr, 0, iArr, 0, 6);
        } while (0 == Nat.lessThan(6, iArr, P));
    }

    public static void randomMult(SecureRandom secureRandom, int[] iArr) {
        do {
            random(secureRandom, iArr);
        } while (0 != isZero(iArr));
    }

    public static void reduce(int[] iArr, int[] iArr2) {
        if (Nat192.mul33DWordAdd(PInv33, Nat192.mul33Add(PInv33, iArr, 6, iArr, 0, iArr2, 0), iArr2, 0) != 0 || (iArr2[5] == -1 && Nat192.gte(iArr2, P))) {
            Nat.add33To(6, PInv33, iArr2);
        }
    }

    public static void reduce32(int i, int[] iArr) {
        if ((i == 0 || Nat192.mul33WordAdd(PInv33, i, iArr, 0) == 0) && !(iArr[5] == -1 && Nat192.gte(iArr, P))) {
            return;
        }
        Nat.add33To(6, PInv33, iArr);
    }

    public static void square(int[] iArr, int[] iArr2) {
        int[] iArrCreateExt = Nat192.createExt();
        Nat192.square(iArr, iArrCreateExt);
        reduce(iArrCreateExt, iArr2);
    }

    public static void squareN(int[] iArr, int i, int[] iArr2) {
        int[] iArrCreateExt = Nat192.createExt();
        Nat192.square(iArr, iArrCreateExt);
        reduce(iArrCreateExt, iArr2);
        while (true) {
            i--;
            if (i <= 0) {
                return;
            }
            Nat192.square(iArr2, iArrCreateExt);
            reduce(iArrCreateExt, iArr2);
        }
    }

    public static void subtract(int[] iArr, int[] iArr2, int[] iArr3) {
        if (Nat192.sub(iArr, iArr2, iArr3) != 0) {
            Nat.sub33From(6, PInv33, iArr3);
        }
    }

    public static void subtractExt(int[] iArr, int[] iArr2, int[] iArr3) {
        if (Nat.sub(12, iArr, iArr2, iArr3) == 0 || Nat.subFrom(PExtInv.length, PExtInv, iArr3) == 0) {
            return;
        }
        Nat.decAt(12, iArr3, PExtInv.length);
    }

    public static void twice(int[] iArr, int[] iArr2) {
        if (Nat.shiftUpBit(6, iArr, 0, iArr2) != 0 || (iArr2[5] == -1 && Nat192.gte(iArr2, P))) {
            Nat.add33To(6, PInv33, iArr2);
        }
    }
}
