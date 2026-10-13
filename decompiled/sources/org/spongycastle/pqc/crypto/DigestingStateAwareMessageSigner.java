package org.spongycastle.pqc.crypto;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/DigestingStateAwareMessageSigner.class */
public class DigestingStateAwareMessageSigner extends DigestingMessageSigner {
    private final StateAwareMessageSigner signer;

    public DigestingStateAwareMessageSigner(StateAwareMessageSigner messSigner, Digest messDigest) {
        super(messSigner, messDigest);
        this.signer = messSigner;
    }

    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        return this.signer.getUpdatedPrivateKey();
    }
}
