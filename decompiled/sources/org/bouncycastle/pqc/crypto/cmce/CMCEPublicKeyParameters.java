package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/cmce/CMCEPublicKeyParameters.class */
public class CMCEPublicKeyParameters extends CMCEKeyParameters {
    private final byte[] publicKey;

    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }

    public byte[] getEncoded() {
        return getPublicKey();
    }

    public CMCEPublicKeyParameters(CMCEParameters cMCEParameters, byte[] bArr) {
        super(false, cMCEParameters);
        this.publicKey = Arrays.clone(bArr);
    }
}
