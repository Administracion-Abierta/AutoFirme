package org.bouncycastle.pqc.legacy.crypto.gmss;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/gmss/GMSSPublicKeyParameters.class */
public class GMSSPublicKeyParameters extends GMSSKeyParameters {
    private byte[] gmssPublicKey;

    public GMSSPublicKeyParameters(byte[] bArr, GMSSParameters gMSSParameters) {
        super(false, gMSSParameters);
        this.gmssPublicKey = bArr;
    }

    public byte[] getPublicKey() {
        return this.gmssPublicKey;
    }
}
