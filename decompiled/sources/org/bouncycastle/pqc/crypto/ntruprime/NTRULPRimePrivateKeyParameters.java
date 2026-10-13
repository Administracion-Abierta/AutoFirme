package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/NTRULPRimePrivateKeyParameters.class */
public class NTRULPRimePrivateKeyParameters extends NTRULPRimeKeyParameters {
    private final byte[] enca;
    private final byte[] pk;
    private final byte[] rho;
    private final byte[] hash;

    public NTRULPRimePrivateKeyParameters(NTRULPRimeParameters nTRULPRimeParameters, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        super(true, nTRULPRimeParameters);
        this.enca = Arrays.clone(bArr);
        this.pk = Arrays.clone(bArr2);
        this.rho = Arrays.clone(bArr3);
        this.hash = Arrays.clone(bArr4);
    }

    public byte[] getEnca() {
        return Arrays.clone(this.enca);
    }

    public byte[] getPk() {
        return Arrays.clone(this.pk);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getHash() {
        return Arrays.clone(this.hash);
    }

    public byte[] getEncoded() {
        byte[] bArr = new byte[getParameters().getPrivateKeyBytes()];
        System.arraycopy(this.enca, 0, bArr, 0, this.enca.length);
        System.arraycopy(this.pk, 0, bArr, this.enca.length, this.pk.length);
        System.arraycopy(this.rho, 0, bArr, this.enca.length + this.pk.length, this.rho.length);
        System.arraycopy(this.hash, 0, bArr, this.enca.length + this.pk.length + this.rho.length, this.hash.length);
        return bArr;
    }
}
