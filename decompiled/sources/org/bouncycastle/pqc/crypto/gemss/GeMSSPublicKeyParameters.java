package org.bouncycastle.pqc.crypto.gemss;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/GeMSSPublicKeyParameters.class */
public class GeMSSPublicKeyParameters extends GeMSSKeyParameters {
    private final byte[] pk;

    public GeMSSPublicKeyParameters(GeMSSParameters geMSSParameters, byte[] bArr) {
        super(false, geMSSParameters);
        this.pk = new byte[bArr.length];
        System.arraycopy(bArr, 0, this.pk, 0, this.pk.length);
    }

    public byte[] getPK() {
        return this.pk;
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.pk);
    }
}
