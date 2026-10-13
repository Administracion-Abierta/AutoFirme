package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/saber/SABERPrivateKeyParameters.class */
public class SABERPrivateKeyParameters extends SABERKeyParameters {
    private byte[] privateKey;

    public SABERPrivateKeyParameters(SABERParameters sABERParameters, byte[] bArr) {
        super(true, sABERParameters);
        this.privateKey = Arrays.clone(bArr);
    }

    public byte[] getPrivateKey() {
        return Arrays.clone(this.privateKey);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.privateKey);
    }
}
