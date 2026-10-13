package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsSigner.class */
public interface TlsSigner {
    void init(TlsContext tlsContext);

    byte[] generateRawSignature(AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr) throws CryptoException;

    byte[] generateRawSignature(SignatureAndHashAlgorithm signatureAndHashAlgorithm, AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr) throws CryptoException;

    boolean verifyRawSignature(byte[] bArr, AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr2) throws CryptoException;

    boolean verifyRawSignature(SignatureAndHashAlgorithm signatureAndHashAlgorithm, byte[] bArr, AsymmetricKeyParameter asymmetricKeyParameter, byte[] bArr2) throws CryptoException;

    Signer createSigner(AsymmetricKeyParameter asymmetricKeyParameter);

    Signer createSigner(SignatureAndHashAlgorithm signatureAndHashAlgorithm, AsymmetricKeyParameter asymmetricKeyParameter);

    Signer createVerifyer(AsymmetricKeyParameter asymmetricKeyParameter);

    Signer createVerifyer(SignatureAndHashAlgorithm signatureAndHashAlgorithm, AsymmetricKeyParameter asymmetricKeyParameter);

    boolean isValidPublicKey(AsymmetricKeyParameter asymmetricKeyParameter);
}
