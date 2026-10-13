package org.bouncycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/GOST3410ValidationParameters.class */
public class GOST3410ValidationParameters {
    private int x0;
    private int c;
    private long x0L;
    private long cL;

    public GOST3410ValidationParameters(int i, int i2) {
        this.x0 = i;
        this.c = i2;
    }

    public GOST3410ValidationParameters(long j, long j2) {
        this.x0L = j;
        this.cL = j2;
    }

    public int getC() {
        return this.c;
    }

    public int getX0() {
        return this.x0;
    }

    public long getCL() {
        return this.cL;
    }

    public long getX0L() {
        return this.x0L;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof GOST3410ValidationParameters)) {
            return false;
        }
        GOST3410ValidationParameters gOST3410ValidationParameters = (GOST3410ValidationParameters) obj;
        return gOST3410ValidationParameters.c == this.c && gOST3410ValidationParameters.x0 == this.x0 && gOST3410ValidationParameters.cL == this.cL && gOST3410ValidationParameters.x0L == this.x0L;
    }

    public int hashCode() {
        return ((((this.x0 ^ this.c) ^ ((int) this.x0L)) ^ ((int) (this.x0L >> 32))) ^ ((int) this.cL)) ^ ((int) (this.cL >> 32));
    }
}
