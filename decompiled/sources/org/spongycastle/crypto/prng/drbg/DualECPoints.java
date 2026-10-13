package org.spongycastle.crypto.prng.drbg;

import org.spongycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/drbg/DualECPoints.class */
public class DualECPoints {
    private final ECPoint p;
    private final ECPoint q;
    private final int securityStrength;
    private final int cofactor;

    public DualECPoints(int securityStrength, ECPoint p, ECPoint q, int cofactor) {
        if (!p.getCurve().equals(q.getCurve())) {
            throw new IllegalArgumentException("points need to be on the same curve");
        }
        this.securityStrength = securityStrength;
        this.p = p;
        this.q = q;
        this.cofactor = cofactor;
    }

    public int getSeedLen() {
        return this.p.getCurve().getFieldSize();
    }

    public int getMaxOutlen() {
        return ((this.p.getCurve().getFieldSize() - (13 + log2(this.cofactor))) / 8) * 8;
    }

    public ECPoint getP() {
        return this.p;
    }

    public ECPoint getQ() {
        return this.q;
    }

    public int getSecurityStrength() {
        return this.securityStrength;
    }

    public int getCofactor() {
        return this.cofactor;
    }

    private static int log2(int value) {
        int log = 0;
        while (true) {
            int i = value >> 1;
            value = i;
            if (i != 0) {
                log++;
            } else {
                return log;
            }
        }
    }
}
