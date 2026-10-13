package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/frodo/FrodoPublicKeyParameters.class */
public class FrodoPublicKeyParameters extends FrodoKeyParameters {
    public byte[] publicKey;

    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }

    public byte[] getEncoded() {
        return getPublicKey();
    }

    public FrodoPublicKeyParameters(FrodoParameters frodoParameters, byte[] bArr) {
        super(false, frodoParameters);
        this.publicKey = Arrays.clone(bArr);
    }
}
