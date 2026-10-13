package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/SNTRUPrimePrivateKeyParameters.class */
public class SNTRUPrimePrivateKeyParameters extends SNTRUPrimeKeyParameters {
    private final byte[] f;
    private final byte[] ginv;
    private final byte[] pk;
    private final byte[] rho;
    private final byte[] hash;

    public SNTRUPrimePrivateKeyParameters(SNTRUPrimeParameters sNTRUPrimeParameters, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5) {
        super(true, sNTRUPrimeParameters);
        this.f = Arrays.clone(bArr);
        this.ginv = Arrays.clone(bArr2);
        this.pk = Arrays.clone(bArr3);
        this.rho = Arrays.clone(bArr4);
        this.hash = Arrays.clone(bArr5);
    }

    public byte[] getF() {
        return Arrays.clone(this.f);
    }

    public byte[] getGinv() {
        return Arrays.clone(this.ginv);
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
        System.arraycopy(this.f, 0, bArr, 0, this.f.length);
        System.arraycopy(this.ginv, 0, bArr, this.f.length, this.ginv.length);
        System.arraycopy(this.pk, 0, bArr, this.f.length + this.ginv.length, this.pk.length);
        System.arraycopy(this.rho, 0, bArr, this.f.length + this.ginv.length + this.pk.length, this.rho.length);
        System.arraycopy(this.hash, 0, bArr, this.f.length + this.ginv.length + this.pk.length + this.rho.length, this.hash.length);
        return bArr;
    }
}
