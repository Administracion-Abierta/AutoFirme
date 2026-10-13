package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/HQCPublicKeyParameters.class */
public class HQCPublicKeyParameters extends HQCKeyParameters {
    private byte[] pk;

    public HQCPublicKeyParameters(HQCParameters hQCParameters, byte[] bArr) {
        super(true, hQCParameters);
        this.pk = Arrays.clone(bArr);
    }

    public byte[] getPublicKey() {
        return Arrays.clone(this.pk);
    }

    public byte[] getEncoded() {
        return getPublicKey();
    }
}
