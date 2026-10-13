package org.spongycastle.crypto.signers;

import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/signers/GenericSigner.class */
public class GenericSigner implements Signer {
    private final AsymmetricBlockCipher engine;
    private final Digest digest;
    private boolean forSigning;

    public GenericSigner(AsymmetricBlockCipher engine, Digest digest) {
        this.engine = engine;
        this.digest = digest;
    }

    @Override // org.spongycastle.crypto.Signer
    public void init(boolean forSigning, CipherParameters parameters) {
        AsymmetricKeyParameter k;
        this.forSigning = forSigning;
        if (parameters instanceof ParametersWithRandom) {
            k = (AsymmetricKeyParameter) ((ParametersWithRandom) parameters).getParameters();
        } else {
            k = (AsymmetricKeyParameter) parameters;
        }
        if (forSigning && !k.isPrivate()) {
            throw new IllegalArgumentException("signing requires private key");
        }
        if (!forSigning && k.isPrivate()) {
            throw new IllegalArgumentException("verification requires public key");
        }
        reset();
        this.engine.init(forSigning, parameters);
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte input) {
        this.digest.update(input);
    }

    @Override // org.spongycastle.crypto.Signer
    public void update(byte[] input, int inOff, int length) {
        this.digest.update(input, inOff, length);
    }

    @Override // org.spongycastle.crypto.Signer
    public byte[] generateSignature() throws DataLengthException, CryptoException {
        if (!this.forSigning) {
            throw new IllegalStateException("GenericSigner not initialised for signature generation.");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        return this.engine.processBlock(hash, 0, hash.length);
    }

    @Override // org.spongycastle.crypto.Signer
    public boolean verifySignature(byte[] signature) {
        if (this.forSigning) {
            throw new IllegalStateException("GenericSigner not initialised for verification");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            byte[] sig = this.engine.processBlock(signature, 0, signature.length);
            if (sig.length < hash.length) {
                byte[] tmp = new byte[hash.length];
                System.arraycopy(sig, 0, tmp, tmp.length - sig.length, sig.length);
                sig = tmp;
            }
            return Arrays.constantTimeAreEqual(sig, hash);
        } catch (Exception e) {
            return false;
        }
    }

    @Override // org.spongycastle.crypto.Signer
    public void reset() {
        this.digest.reset();
    }
}
