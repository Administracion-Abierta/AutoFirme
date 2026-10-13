package org.bouncycastle.pqc.legacy.math.ntru.euclid;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/euclid/IntEuclidean.class */
public class IntEuclidean {
    public int x;
    public int y;
    public int gcd;

    private IntEuclidean() {
    }

    public static IntEuclidean calculate(int i, int i2) {
        int i3 = 0;
        int i4 = 1;
        int i5 = 1;
        int i6 = 0;
        while (true) {
            int i7 = i6;
            if (i2 == 0) {
                IntEuclidean intEuclidean = new IntEuclidean();
                intEuclidean.x = i4;
                intEuclidean.y = i7;
                intEuclidean.gcd = i;
                return intEuclidean;
            }
            int i8 = i / i2;
            int i9 = i;
            i = i2;
            i2 = i9 % i2;
            int i10 = i3;
            i3 = i4 - (i8 * i3);
            i4 = i10;
            int i11 = i5;
            i5 = i7 - (i8 * i5);
            i6 = i11;
        }
    }
}
