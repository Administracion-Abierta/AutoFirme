package org.spongycastle.pqc.crypto.gmss;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.pqc.crypto.StateAwareMessageSigner;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/gmss/GMSSStateAwareSigner.class */
public class GMSSStateAwareSigner implements StateAwareMessageSigner {
    private final GMSSSigner gmssSigner;
    private GMSSPrivateKeyParameters key;

    public GMSSStateAwareSigner(Digest digest) {
        if (!(digest instanceof Memoable)) {
            throw new IllegalArgumentException("digest must implement Memoable");
        }
        final Memoable dig = ((Memoable) digest).copy();
        this.gmssSigner = new GMSSSigner(new GMSSDigestProvider() { // from class: org.spongycastle.pqc.crypto.gmss.GMSSStateAwareSigner.1
            @Override // org.spongycastle.pqc.crypto.gmss.GMSSDigestProvider
            public Digest get() {
                return (Digest) dig.copy();
            }
        });
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) param;
                this.key = (GMSSPrivateKeyParameters) rParam.getParameters();
            } else {
                this.key = (GMSSPrivateKeyParameters) param;
            }
        }
        this.gmssSigner.init(forSigning, param);
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public byte[] generateSignature(byte[] message) {
        if (this.key == null) {
            throw new IllegalStateException("signing key no longer usable");
        }
        byte[] sig = this.gmssSigner.generateSignature(message);
        this.key = this.key.nextKey();
        return sig;
    }

    @Override // org.spongycastle.pqc.crypto.MessageSigner
    public boolean verifySignature(byte[] message, byte[] signature) {
        return this.gmssSigner.verifySignature(message, signature);
    }

    @Override // org.spongycastle.pqc.crypto.StateAwareMessageSigner
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        AsymmetricKeyParameter k = this.key;
        this.key = null;
        return k;
    }
}
