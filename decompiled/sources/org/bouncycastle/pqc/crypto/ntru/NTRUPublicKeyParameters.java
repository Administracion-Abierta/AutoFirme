package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntru/NTRUPublicKeyParameters.class */
public class NTRUPublicKeyParameters extends NTRUKeyParameters {
    final byte[] publicKey;

    public NTRUPublicKeyParameters(NTRUParameters nTRUParameters, byte[] bArr) {
        super(false, nTRUParameters);
        this.publicKey = Arrays.clone(bArr);
    }

    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }

    public byte[] getEncoded() {
        return getPublicKey();
    }
}
