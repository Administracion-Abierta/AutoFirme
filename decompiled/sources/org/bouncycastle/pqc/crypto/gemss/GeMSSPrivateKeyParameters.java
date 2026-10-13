package org.bouncycastle.pqc.crypto.gemss;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/gemss/GeMSSPrivateKeyParameters.class */
public class GeMSSPrivateKeyParameters extends GeMSSKeyParameters {
    final byte[] sk;

    public GeMSSPrivateKeyParameters(GeMSSParameters geMSSParameters, byte[] bArr) {
        super(false, geMSSParameters);
        this.sk = new byte[bArr.length];
        System.arraycopy(bArr, 0, this.sk, 0, this.sk.length);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.sk);
    }
}
