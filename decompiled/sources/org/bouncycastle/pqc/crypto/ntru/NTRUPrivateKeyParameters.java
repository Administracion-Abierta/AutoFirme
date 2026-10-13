package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntru/NTRUPrivateKeyParameters.class */
public class NTRUPrivateKeyParameters extends NTRUKeyParameters {
    final byte[] privateKey;

    public NTRUPrivateKeyParameters(NTRUParameters nTRUParameters, byte[] bArr) {
        super(true, nTRUParameters);
        this.privateKey = Arrays.clone(bArr);
    }

    public byte[] getPrivateKey() {
        return Arrays.clone(this.privateKey);
    }

    public byte[] getEncoded() {
        return getPrivateKey();
    }
}
