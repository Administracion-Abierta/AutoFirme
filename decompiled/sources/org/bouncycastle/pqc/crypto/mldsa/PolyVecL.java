package org.bouncycastle.pqc.crypto.mldsa;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/PolyVecL.class */
class PolyVecL {
    Poly[] vec;
    private MLDSAEngine engine;
    private int mode;
    private int polyVecBytes;
    private int dilithiumL;
    private int dilithiumK;

    public PolyVecL(MLDSAEngine mLDSAEngine) {
        this.engine = mLDSAEngine;
        this.mode = mLDSAEngine.getDilithiumMode();
        this.dilithiumL = mLDSAEngine.getDilithiumL();
        this.dilithiumK = mLDSAEngine.getDilithiumK();
        this.vec = new Poly[this.dilithiumL];
        for (int i = 0; i < this.dilithiumL; i++) {
            this.vec[i] = new Poly(mLDSAEngine);
        }
    }

    public PolyVecL() throws Exception {
        throw new Exception("Requires Parameter");
    }

    public Poly getVectorIndex(int i) {
        return this.vec[i];
    }

    public void expandMatrix(byte[] bArr, int i) {
        for (int i2 = 0; i2 < this.dilithiumL; i2++) {
            this.vec[i2].uniformBlocks(bArr, (short) ((i << 8) + i2));
        }
    }

    public void uniformEta(byte[] bArr, short s) {
        short s2 = s;
        for (int i = 0; i < this.dilithiumL; i++) {
            short s3 = s2;
            s2 = (short) (s2 + 1);
            getVectorIndex(i).uniformEta(bArr, s3);
        }
    }

    public void copyPolyVecL(PolyVecL polyVecL) {
        for (int i = 0; i < this.dilithiumL; i++) {
            for (int i2 = 0; i2 < 256; i2++) {
                polyVecL.getVectorIndex(i).setCoeffIndex(i2, getVectorIndex(i).getCoeffIndex(i2));
            }
        }
    }

    public void polyVecNtt() {
        for (int i = 0; i < this.dilithiumL; i++) {
            this.vec[i].polyNtt();
        }
    }

    public void uniformGamma1(byte[] bArr, short s) {
        for (int i = 0; i < this.dilithiumL; i++) {
            getVectorIndex(i).uniformGamma1(bArr, (short) ((this.dilithiumL * s) + i));
        }
    }

    public void pointwisePolyMontgomery(Poly poly, PolyVecL polyVecL) {
        for (int i = 0; i < this.dilithiumL; i++) {
            getVectorIndex(i).pointwiseMontgomery(poly, polyVecL.getVectorIndex(i));
        }
    }

    public void invNttToMont() {
        for (int i = 0; i < this.dilithiumL; i++) {
            getVectorIndex(i).invNttToMont();
        }
    }

    public void addPolyVecL(PolyVecL polyVecL) {
        for (int i = 0; i < this.dilithiumL; i++) {
            getVectorIndex(i).addPoly(polyVecL.getVectorIndex(i));
        }
    }

    public void reduce() {
        for (int i = 0; i < this.dilithiumL; i++) {
            getVectorIndex(i).reduce();
        }
    }

    public boolean checkNorm(int i) {
        for (int i2 = 0; i2 < this.dilithiumL; i2++) {
            if (getVectorIndex(i2).checkNorm(i)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String str = "\n[";
        for (int i = 0; i < this.dilithiumL; i++) {
            str = str + "Inner Matrix " + i + " " + getVectorIndex(i).toString();
            if (i != this.dilithiumL - 1) {
                str = str + ",\n";
            }
        }
        return str + "]";
    }

    public String toString(String str) {
        return str + ": " + toString();
    }
}
