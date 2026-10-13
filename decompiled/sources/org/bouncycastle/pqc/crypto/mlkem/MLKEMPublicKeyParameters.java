package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mlkem/MLKEMPublicKeyParameters.class */
public class MLKEMPublicKeyParameters extends MLKEMKeyParameters {
    final byte[] t;
    final byte[] rho;

    static byte[] getEncoded(byte[] bArr, byte[] bArr2) {
        return Arrays.concatenate(bArr, bArr2);
    }

    public MLKEMPublicKeyParameters(MLKEMParameters mLKEMParameters, byte[] bArr, byte[] bArr2) {
        super(false, mLKEMParameters);
        this.t = Arrays.clone(bArr);
        this.rho = Arrays.clone(bArr2);
    }

    public MLKEMPublicKeyParameters(MLKEMParameters mLKEMParameters, byte[] bArr) {
        super(false, mLKEMParameters);
        this.t = Arrays.copyOfRange(bArr, 0, bArr.length - 32);
        this.rho = Arrays.copyOfRange(bArr, bArr.length - 32, bArr.length);
    }

    public byte[] getEncoded() {
        return getEncoded(this.t, this.rho);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getT() {
        return Arrays.clone(this.t);
    }
}
