package org.bouncycastle.pqc.crypto.crystals.dilithium;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/crystals/dilithium/PolyVecMatrix.class */
class PolyVecMatrix {
    private final int dilithiumK;
    private final int dilithiumL;
    private final PolyVecL[] mat;

    public PolyVecMatrix(DilithiumEngine dilithiumEngine) {
        this.dilithiumK = dilithiumEngine.getDilithiumK();
        this.dilithiumL = dilithiumEngine.getDilithiumL();
        this.mat = new PolyVecL[this.dilithiumK];
        for (int i = 0; i < this.dilithiumK; i++) {
            this.mat[i] = new PolyVecL(dilithiumEngine);
        }
    }

    public void pointwiseMontgomery(PolyVecK polyVecK, PolyVecL polyVecL) {
        for (int i = 0; i < this.dilithiumK; i++) {
            polyVecK.getVectorIndex(i).pointwiseAccountMontgomery(this.mat[i], polyVecL);
        }
    }

    public void expandMatrix(byte[] bArr) {
        for (int i = 0; i < this.dilithiumK; i++) {
            for (int i2 = 0; i2 < this.dilithiumL; i2++) {
                this.mat[i].getVectorIndex(i2).uniformBlocks(bArr, (short) ((i << 8) + i2));
            }
        }
    }

    private String addString() {
        String str = "[";
        int i = 0;
        while (i < this.dilithiumK) {
            String str2 = (str + "Outer Matrix " + i + " [") + this.mat[i].toString();
            str = i == this.dilithiumK - 1 ? str2 + "]\n" : str2 + "],\n";
            i++;
        }
        return str + "]\n";
    }

    public String toString(String str) {
        return str.concat(": \n" + addString());
    }
}
