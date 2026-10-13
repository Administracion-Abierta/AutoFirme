package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/newhope/NHAgreement.class */
public class NHAgreement {
    private NHPrivateKeyParameters privKey;

    public void init(CipherParameters cipherParameters) {
        this.privKey = (NHPrivateKeyParameters) cipherParameters;
    }

    public byte[] calculateAgreement(CipherParameters cipherParameters) {
        byte[] bArr = new byte[32];
        NewHope.sharedA(bArr, this.privKey.secData, ((NHPublicKeyParameters) cipherParameters).pubData);
        return bArr;
    }
}
