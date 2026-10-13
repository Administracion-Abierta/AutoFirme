package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/AbstractTlsSigner.class */
public abstract class AbstractTlsSigner implements TlsSigner {
    protected TlsContext context;

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public void init(TlsContext context) {
        this.context = context;
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public byte[] generateRawSignature(AsymmetricKeyParameter privateKey, byte[] md5AndSha1) throws CryptoException {
        return generateRawSignature(null, privateKey, md5AndSha1);
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public boolean verifyRawSignature(byte[] sigBytes, AsymmetricKeyParameter publicKey, byte[] md5AndSha1) throws CryptoException {
        return verifyRawSignature(null, sigBytes, publicKey, md5AndSha1);
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public Signer createSigner(AsymmetricKeyParameter privateKey) {
        return createSigner(null, privateKey);
    }

    @Override // org.spongycastle.crypto.tls.TlsSigner
    public Signer createVerifyer(AsymmetricKeyParameter publicKey) {
        return createVerifyer(null, publicKey);
    }
}
