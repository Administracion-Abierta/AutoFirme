package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.digests.NullDigest;
import org.spongycastle.crypto.encodings.PKCS1Encoding;
import org.spongycastle.crypto.engines.RSABlindedEngine;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.crypto.signers.GenericSigner;
import org.spongycastle.crypto.signers.RSADigestSigner;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsRSASigner.class */
public class TlsRSASigner extends AbstractTlsSigner {
    @Override // org.spongycastle.crypto.tls.TlsSigner
    public byte[] generateRawSignature(SignatureAndHashAlgorithm algorithm, AsymmetricKeyParameter privateKey, byte[] hash) throws CryptoException {
        Signer signer = makeSigner(algorithm, true, true, new ParametersWithRandom(privateKey, this.context.getSecureRandom()));
        signer.update(hash, 0, hash.length);
        return signer.generateSignature();
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public boolean verifyRawSignature(SignatureAndHashAlgorithm algorithm, byte[] sigBytes, AsymmetricKeyParameter publicKey, byte[] hash) throws CryptoException {
        Signer signer = makeSigner(algorithm, true, false, publicKey);
        signer.update(hash, 0, hash.length);
        return signer.verifySignature(sigBytes);
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public Signer createSigner(SignatureAndHashAlgorithm algorithm, AsymmetricKeyParameter privateKey) {
        return makeSigner(algorithm, false, true, new ParametersWithRandom(privateKey, this.context.getSecureRandom()));
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public Signer createVerifyer(SignatureAndHashAlgorithm algorithm, AsymmetricKeyParameter publicKey) {
        return makeSigner(algorithm, false, false, publicKey);
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public boolean isValidPublicKey(AsymmetricKeyParameter publicKey) {
        return (publicKey instanceof RSAKeyParameters) && !publicKey.isPrivate();
    }

    protected Signer makeSigner(SignatureAndHashAlgorithm algorithm, boolean raw, boolean forSigning, CipherParameters cp) {
        Digest d;
        Signer s;
        if ((algorithm != null) != TlsUtils.isTLSv12(this.context)) {
            throw new IllegalStateException();
        }
        if (algorithm != null && algorithm.getSignature() != 1) {
            throw new IllegalStateException();
        }
        if (raw) {
            d = new NullDigest();
        } else if (algorithm == null) {
            d = new CombinedHash();
        } else {
            d = TlsUtils.createHash(algorithm.getHash());
        }
        if (algorithm != null) {
            s = new RSADigestSigner(d, TlsUtils.getOIDForHashAlgorithm(algorithm.getHash()));
        } else {
            s = new GenericSigner(createRSAImpl(), d);
        }
        s.init(forSigning, cp);
        return s;
    }

    protected AsymmetricBlockCipher createRSAImpl() {
        return new PKCS1Encoding(new RSABlindedEngine());
    }
}
