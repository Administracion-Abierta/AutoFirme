package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconPrivateKeyParameters.class */
public class FalconPrivateKeyParameters extends FalconKeyParameters {
    private final byte[] pk;
    private final byte[] f;
    private final byte[] g;
    private final byte[] F;

    public FalconPrivateKeyParameters(FalconParameters falconParameters, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        super(true, falconParameters);
        this.f = Arrays.clone(bArr);
        this.g = Arrays.clone(bArr2);
        this.F = Arrays.clone(bArr3);
        this.pk = Arrays.clone(bArr4);
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(this.f, this.g, this.F);
    }

    public byte[] getPublicKey() {
        return Arrays.clone(this.pk);
    }

    public byte[] getSpolyf() {
        return Arrays.clone(this.f);
    }

    public byte[] getG() {
        return Arrays.clone(this.g);
    }

    public byte[] getSpolyF() {
        return Arrays.clone(this.F);
    }
}
