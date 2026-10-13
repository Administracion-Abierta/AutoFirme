package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ITSPublicVerificationKey.class */
public class ITSPublicVerificationKey {
    protected final PublicVerificationKey verificationKey;

    public ITSPublicVerificationKey(PublicVerificationKey publicVerificationKey) {
        this.verificationKey = publicVerificationKey;
    }

    public PublicVerificationKey toASN1Structure() {
        return this.verificationKey;
    }
}
