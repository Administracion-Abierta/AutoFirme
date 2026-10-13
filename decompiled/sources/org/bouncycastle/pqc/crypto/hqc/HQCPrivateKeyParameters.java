package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/hqc/HQCPrivateKeyParameters.class */
public class HQCPrivateKeyParameters extends HQCKeyParameters {
    private byte[] sk;

    public HQCPrivateKeyParameters(HQCParameters hQCParameters, byte[] bArr) {
        super(true, hQCParameters);
        this.sk = Arrays.clone(bArr);
    }

    public byte[] getPrivateKey() {
        return Arrays.clone(this.sk);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.sk);
    }
}
