package org.spongycastle.crypto.tls;

import java.io.IOException;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DSAPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.RSAKeyParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/DefaultTlsSignerCredentials.class */
public class DefaultTlsSignerCredentials extends AbstractTlsSignerCredentials {
    protected TlsContext context;
    protected Certificate certificate;
    protected AsymmetricKeyParameter privateKey;
    protected SignatureAndHashAlgorithm signatureAndHashAlgorithm;
    protected TlsSigner signer;

    public DefaultTlsSignerCredentials(TlsContext context, Certificate certificate, AsymmetricKeyParameter privateKey) {
        this(context, certificate, privateKey, null);
    }

    public DefaultTlsSignerCredentials(TlsContext context, Certificate certificate, AsymmetricKeyParameter privateKey, SignatureAndHashAlgorithm signatureAndHashAlgorithm) {
        if (certificate == null) {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        if (certificate.isEmpty()) {
            throw new IllegalArgumentException("'certificate' cannot be empty");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("'privateKey' cannot be null");
        }
        if (!privateKey.isPrivate()) {
            throw new IllegalArgumentException("'privateKey' must be private");
        }
        if (TlsUtils.isTLSv12(context) && signatureAndHashAlgorithm == null) {
            throw new IllegalArgumentException("'signatureAndHashAlgorithm' cannot be null for (D)TLS 1.2+");
        }
        if (privateKey instanceof RSAKeyParameters) {
            this.signer = new TlsRSASigner();
        } else if (privateKey instanceof DSAPrivateKeyParameters) {
            this.signer = new TlsDSSSigner();
        } else if (privateKey instanceof ECPrivateKeyParameters) {
            this.signer = new TlsECDSASigner();
        } else {
            throw new IllegalArgumentException("'privateKey' type not supported: " + privateKey.getClass().getName());
        }
        this.signer.init(context);
        this.context = context;
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.signatureAndHashAlgorithm = signatureAndHashAlgorithm;
    }

    @Override // org.spongycastle.crypto.tls.TlsCredentials
    public Certificate getCertificate() {
        return this.certificate;
    }

    @Override // org.spongycastle.crypto.tls.TlsSignerCredentials
    public byte[] generateCertificateSignature(byte[] hash) throws IOException {
        try {
            if (TlsUtils.isTLSv12(this.context)) {
                return this.signer.generateRawSignature(this.signatureAndHashAlgorithm, this.privateKey, hash);
            }
            return this.signer.generateRawSignature(this.privateKey, hash);
        } catch (CryptoException e) {
            throw new TlsFatalAlert((short) 80, e);
        }
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsSignerCredentials, org.spongycastle.crypto.tls.TlsSignerCredentials
    public SignatureAndHashAlgorithm getSignatureAndHashAlgorithm() {
        return this.signatureAndHashAlgorithm;
    }
}
