package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.digests.NullDigest;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.signers.DSADigestSigner;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsDSASigner.class */
public abstract class TlsDSASigner extends AbstractTlsSigner {
    protected abstract short getSignatureAlgorithm();

    protected abstract DSA createDSAImpl(short s);

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public byte[] generateRawSignature(SignatureAndHashAlgorithm algorithm, AsymmetricKeyParameter privateKey, byte[] hash) throws CryptoException {
        Signer signer = makeSigner(algorithm, true, true, new ParametersWithRandom(privateKey, this.context.getSecureRandom()));
        if (algorithm == null) {
            signer.update(hash, 16, 20);
        } else {
            signer.update(hash, 0, hash.length);
        }
        return signer.generateSignature();
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public boolean verifyRawSignature(SignatureAndHashAlgorithm algorithm, byte[] sigBytes, AsymmetricKeyParameter publicKey, byte[] hash) throws CryptoException {
        Signer signer = makeSigner(algorithm, true, false, publicKey);
        if (algorithm == null) {
            signer.update(hash, 16, 20);
        } else {
            signer.update(hash, 0, hash.length);
        }
        return signer.verifySignature(sigBytes);
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public Signer createSigner(SignatureAndHashAlgorithm algorithm, AsymmetricKeyParameter privateKey) {
        return makeSigner(algorithm, false, true, privateKey);
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public Signer createVerifyer(SignatureAndHashAlgorithm algorithm, AsymmetricKeyParameter publicKey) {
        return makeSigner(algorithm, false, false, publicKey);
    }

    protected CipherParameters makeInitParameters(boolean forSigning, CipherParameters cp) {
        return cp;
    }

    protected Signer makeSigner(SignatureAndHashAlgorithm algorithm, boolean raw, boolean forSigning, CipherParameters cp) {
        if ((algorithm != null) != TlsUtils.isTLSv12(this.context)) {
            throw new IllegalStateException();
        }
        if (algorithm != null && algorithm.getSignature() != getSignatureAlgorithm()) {
            throw new IllegalStateException();
        }
        short hashAlgorithm = algorithm == null ? (short) 2 : algorithm.getHash();
        Digest d = raw ? new NullDigest() : TlsUtils.createHash(hashAlgorithm);
        Signer s = new DSADigestSigner(createDSAImpl(hashAlgorithm), d);
        s.init(forSigning, makeInitParameters(forSigning, cp));
        return s;
    }
}
