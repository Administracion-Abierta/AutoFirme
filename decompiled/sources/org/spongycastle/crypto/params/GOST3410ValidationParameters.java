package org.spongycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/GOST3410ValidationParameters.class */
public class GOST3410ValidationParameters {
    private int x0;
    private int c;
    private long x0L;
    private long cL;

    public GOST3410ValidationParameters(int x0, int c) {
        this.x0 = x0;
        this.c = c;
    }

    public GOST3410ValidationParameters(long x0L, long cL) {
        this.x0L = x0L;
        this.cL = cL;
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

    public boolean equals(Object o) {
        if (!(o instanceof GOST3410ValidationParameters)) {
            return false;
        }
        GOST3410ValidationParameters other = (GOST3410ValidationParameters) o;
        if (other.c != this.c || other.x0 != this.x0 || other.cL != this.cL || other.x0L != this.x0L) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((this.x0 ^ this.c) ^ ((int) this.x0L)) ^ ((int) (this.x0L >> 32))) ^ ((int) this.cL)) ^ ((int) (this.cL >> 32));
    }
}
