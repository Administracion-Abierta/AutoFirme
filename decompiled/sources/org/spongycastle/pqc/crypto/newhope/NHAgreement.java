package org.spongycastle.pqc.crypto.newhope;

import org.spongycastle.crypto.CipherParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/newhope/NHAgreement.class */
public class NHAgreement {
    private NHPrivateKeyParameters privKey;

    public void init(CipherParameters param) {
        this.privKey = (NHPrivateKeyParameters) param;
    }

    public byte[] calculateAgreement(CipherParameters otherPublicKey) {
        NHPublicKeyParameters pubKey = (NHPublicKeyParameters) otherPublicKey;
        byte[] sharedValue = new byte[32];
        NewHope.sharedA(sharedValue, this.privKey.secData, pubKey.pubData);
        return sharedValue;
    }
}
