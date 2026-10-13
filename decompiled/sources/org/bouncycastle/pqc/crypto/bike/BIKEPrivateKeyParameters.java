package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/bike/BIKEPrivateKeyParameters.class */
public class BIKEPrivateKeyParameters extends BIKEKeyParameters {
    private byte[] h0;
    private byte[] h1;
    private byte[] sigma;

    public BIKEPrivateKeyParameters(BIKEParameters bIKEParameters, byte[] bArr, byte[] bArr2, byte[] bArr3) {
        super(true, bIKEParameters);
        this.h0 = Arrays.clone(bArr);
        this.h1 = Arrays.clone(bArr2);
        this.sigma = Arrays.clone(bArr3);
    }

    byte[] getH0() {
        return this.h0;
    }

    byte[] getH1() {
        return this.h1;
    }

    byte[] getSigma() {
        return this.sigma;
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(this.h0, this.h1, this.sigma);
    }
}
