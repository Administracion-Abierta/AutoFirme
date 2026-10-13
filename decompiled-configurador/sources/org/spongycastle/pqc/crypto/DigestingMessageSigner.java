package org.spongycastle.pqc.crypto;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ParametersWithRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/DigestingMessageSigner.class */
public class DigestingMessageSigner implements Signer {
    private final Digest messDigest;
    private final MessageSigner messSigner;
    private boolean forSigning;

    public DigestingMessageSigner(MessageSigner messSigner, Digest messDigest) {
        this.messSigner = messSigner;
        this.messDigest = messDigest;
    }

    @Override // org.spongycastle.crypto.Signer
    public void init(boolean forSigning, CipherParameters param) {
        AsymmetricKeyParameter k;
        this.forSigning = forSigning;
        if (param instanceof ParametersWithRandom) {
            k = (AsymmetricKeyParameter) ((ParametersWithRandom) param).getParameters();
        } else {
            k = (AsymmetricKeyParameter) param;
        }
        if (forSigning && !k.isPrivate()) {
            throw new IllegalArgumentException("Signing Requires Private Key.");
        }
        if (!forSigning && k.isPrivate()) {
            throw new IllegalArgumentException("Verification Requires Public Key.");
        }
        reset();
        this.messSigner.init(forSigning, param);
    }

    @Override // org.spongycastle.crypto.Signer
    public byte[] generateSignature() {
        if (!this.forSigning) {
            throw new IllegalStateException("DigestingMessageSigner not initialised for signature generation.");
        }
        byte[] hash = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(hash, 0);
        return this.messSigner.generateSignature(hash);
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte b) {
        this.messDigest.update(b);
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte[] in, int off, int len) {
        this.messDigest.update(in, off, len);
    }

    @Override // org.spongycastle.crypto.Signer
    public void reset() {
        this.messDigest.reset();
    }

    @Override // org.spongycastle.crypto.Signer
    public boolean verifySignature(byte[] signature) {
        if (this.forSigning) {
            throw new IllegalStateException("DigestingMessageSigner not initialised for verification");
        }
        byte[] hash = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(hash, 0);
        return this.messSigner.verifySignature(hash, signature);
    }
}
