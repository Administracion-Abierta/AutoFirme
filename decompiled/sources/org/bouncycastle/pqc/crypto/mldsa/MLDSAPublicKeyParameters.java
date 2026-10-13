package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/mldsa/MLDSAPublicKeyParameters.class */
public class MLDSAPublicKeyParameters extends MLDSAKeyParameters {
    final byte[] rho;
    final byte[] t1;

    static byte[] getEncoded(byte[] bArr, byte[] bArr2) {
        return Arrays.concatenate(bArr, bArr2);
    }

    public MLDSAPublicKeyParameters(MLDSAParameters mLDSAParameters, byte[] bArr) {
        super(false, mLDSAParameters);
        this.rho = Arrays.copyOfRange(bArr, 0, 32);
        this.t1 = Arrays.copyOfRange(bArr, 32, bArr.length);
        if (this.t1.length == 0) {
            throw new IllegalArgumentException("encoding too short");
        }
    }

    public MLDSAPublicKeyParameters(MLDSAParameters mLDSAParameters, byte[] bArr, byte[] bArr2) {
        super(false, mLDSAParameters);
        if (bArr == null) {
            throw new NullPointerException("rho cannot be null");
        }
        if (bArr2 == null) {
            throw new NullPointerException("t1 cannot be null");
        }
        this.rho = Arrays.clone(bArr);
        this.t1 = Arrays.clone(bArr2);
    }

    public byte[] getEncoded() {
        return getEncoded(this.rho, this.t1);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getT1() {
        return Arrays.clone(this.t1);
    }
}
