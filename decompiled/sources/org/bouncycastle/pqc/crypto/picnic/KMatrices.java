package org.bouncycastle.pqc.crypto.picnic;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/KMatrices.class */
class KMatrices {
    private int nmatrices;
    private int rows;
    private int columns;
    private int[] data;

    public KMatrices(int i, int i2, int i3, int[] iArr) {
        this.nmatrices = i;
        this.rows = i2;
        this.columns = i3;
        this.data = iArr;
    }

    public int getNmatrices() {
        return this.nmatrices;
    }

    public int getSize() {
        return this.rows * this.columns;
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public int[] getData() {
        return this.data;
    }
}
