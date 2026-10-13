package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/saber/SABERPublicKeyParameters.class */
public class SABERPublicKeyParameters extends SABERKeyParameters {
    private final byte[] publicKey;

    public SABERPublicKeyParameters(SABERParameters sABERParameters, byte[] bArr) {
        super(false, sABERParameters);
        this.publicKey = Arrays.clone(bArr);
    }

    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }

    public byte[] getEncoded() {
        return getPublicKey();
    }
}
