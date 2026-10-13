package org.bouncycastle.pqc.crypto.picnic;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/KMatricesWithPointer.class */
class KMatricesWithPointer extends KMatrices {
    private int matrixPointer;

    public KMatricesWithPointer(KMatrices kMatrices) {
        super(kMatrices.getNmatrices(), kMatrices.getRows(), kMatrices.getColumns(), kMatrices.getData());
        this.matrixPointer = 0;
    }

    public int getMatrixPointer() {
        return this.matrixPointer;
    }

    public void setMatrixPointer(int i) {
        this.matrixPointer = i;
    }
}
