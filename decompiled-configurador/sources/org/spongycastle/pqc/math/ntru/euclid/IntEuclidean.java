package org.spongycastle.pqc.math.ntru.euclid;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/ntru/euclid/IntEuclidean.class */
public class IntEuclidean {
    public int x;
    public int y;
    public int gcd;

    private IntEuclidean() {
    }

    public static IntEuclidean calculate(int a, int b) {
        int x = 0;
        int lastx = 1;
        int y = 1;
        int i = 0;
        while (true) {
            int lasty = i;
            if (b != 0) {
                int quotient = a / b;
                int temp = a;
                a = b;
                b = temp % b;
                int temp2 = x;
                x = lastx - (quotient * x);
                lastx = temp2;
                int temp3 = y;
                y = lasty - (quotient * y);
                i = temp3;
            } else {
                IntEuclidean result = new IntEuclidean();
                result.x = lastx;
                result.y = lasty;
                result.gcd = a;
                return result;
            }
        }
    }
}
