package org.bouncycastle.pqc.crypto.mldsa;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/PolyVecK.class */
class PolyVecK {
    Poly[] vec;
    private MLDSAEngine engine;
    private int mode;
    private int polyVecBytes;
    private int dilithiumK;
    private int dilithiumL;

    public PolyVecK(MLDSAEngine mLDSAEngine) {
        this.engine = mLDSAEngine;
        this.mode = mLDSAEngine.getDilithiumMode();
        this.dilithiumK = mLDSAEngine.getDilithiumK();
        this.dilithiumL = mLDSAEngine.getDilithiumL();
        this.vec = new Poly[this.dilithiumK];
        for (int i = 0; i < this.dilithiumK; i++) {
            this.vec[i] = new Poly(mLDSAEngine);
        }
    }

    public PolyVecK() throws Exception {
        throw new Exception("Requires Parameter");
    }

    public Poly getVectorIndex(int i) {
        return this.vec[i];
    }

    public void setVectorIndex(int i, Poly poly) {
        this.vec[i] = poly;
    }

    public void uniformEta(byte[] bArr, short s) {
        short s2 = s;
        for (int i = 0; i < this.dilithiumK; i++) {
            short s3 = s2;
            s2 = (short) (s2 + 1);
            getVectorIndex(i).uniformEta(bArr, s3);
        }
    }

    public void reduce() {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).reduce();
        }
    }

    public void invNttToMont() {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).invNttToMont();
        }
    }

    public void addPolyVecK(PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).addPoly(polyVecK.getVectorIndex(i));
        }
    }

    public void conditionalAddQ() {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).conditionalAddQ();
        }
    }

    public void power2Round(PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).power2Round(polyVecK.getVectorIndex(i));
        }
    }

    public void polyVecNtt() {
        for (int i = 0; i < this.dilithiumK; i++) {
            this.vec[i].polyNtt();
        }
    }

    public void decompose(PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).decompose(polyVecK.getVectorIndex(i));
        }
    }

    public byte[] packW1() {
        byte[] bArr = new byte[this.dilithiumK * this.engine.getDilithiumPolyW1PackedBytes()];
        for (int i = 0; i < this.dilithiumK; i++) {
            System.arraycopy(getVectorIndex(i).w1Pack(), 0, bArr, i * this.engine.getDilithiumPolyW1PackedBytes(), this.engine.getDilithiumPolyW1PackedBytes());
        }
        return bArr;
    }

    public void pointwisePolyMontgomery(Poly poly, PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).pointwiseMontgomery(poly, polyVecK.getVectorIndex(i));
        }
    }

    public void subtract(PolyVecK polyVecK) {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).subtract(polyVecK.getVectorIndex(i));
        }
    }

    public boolean checkNorm(int i) {
        for (int i2 = 0; i2 < this.dilithiumK; i2++) {
            if (getVectorIndex(i2).checkNorm(i)) {
                return true;
            }
        }
        return false;
    }

    public int makeHint(PolyVecK polyVecK, PolyVecK polyVecK2) {
        int iPolyMakeHint = 0;
        for (int i = 0; i < this.dilithiumK; i++) {
            iPolyMakeHint += getVectorIndex(i).polyMakeHint(polyVecK.getVectorIndex(i), polyVecK2.getVectorIndex(i));
        }
        return iPolyMakeHint;
    }

    public void useHint(PolyVecK polyVecK, PolyVecK polyVecK2) {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).polyUseHint(polyVecK.getVectorIndex(i), polyVecK2.getVectorIndex(i));
        }
    }

    public void shiftLeft() {
        for (int i = 0; i < this.dilithiumK; i++) {
            getVectorIndex(i).shiftLeft();
        }
    }

    public String toString() {
        String str = "[";
        for (int i = 0; i < this.dilithiumK; i++) {
            str = str + i + " " + getVectorIndex(i).toString();
            if (i != this.dilithiumK - 1) {
                str = str + ",\n";
            }
        }
        return str + "]";
    }

    public String toString(String str) {
        return str + ": " + toString();
    }
}
