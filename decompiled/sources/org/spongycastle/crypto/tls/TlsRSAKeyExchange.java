package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.crypto.util.PublicKeyFactory;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsRSAKeyExchange.class */
public class TlsRSAKeyExchange extends AbstractTlsKeyExchange {
    protected AsymmetricKeyParameter serverPublicKey;
    protected RSAKeyParameters rsaServerPublicKey;
    protected TlsEncryptionCredentials serverCredentials;
    protected byte[] premasterSecret;

    public TlsRSAKeyExchange(Vector supportedSignatureAlgorithms) {
        super(1, supportedSignatureAlgorithms);
        this.serverPublicKey = null;
        this.rsaServerPublicKey = null;
        this.serverCredentials = null;
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerCredentials() throws IOException {
        throw new TlsFatalAlert((short) 10);
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCredentials(TlsCredentials serverCredentials) throws IOException {
        if (!(serverCredentials instanceof TlsEncryptionCredentials)) {
            throw new TlsFatalAlert((short) 80);
        }
        processServerCertificate(serverCredentials.getCertificate());
        this.serverCredentials = (TlsEncryptionCredentials) serverCredentials;
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCertificate(Certificate serverCertificate) throws IOException {
        if (serverCertificate.isEmpty()) {
            throw new TlsFatalAlert((short) 42);
        }
        org.spongycastle.asn1.x509.Certificate x509Cert = serverCertificate.getCertificateAt(0);
        SubjectPublicKeyInfo keyInfo = x509Cert.getSubjectPublicKeyInfo();
        try {
            this.serverPublicKey = PublicKeyFactory.createKey(keyInfo);
            if (this.serverPublicKey.isPrivate()) {
                throw new TlsFatalAlert((short) 80);
            }
            this.rsaServerPublicKey = validateRSAPublicKey((RSAKeyParameters) this.serverPublicKey);
            TlsUtils.validateKeyUsage(x509Cert, 32);
            super.processServerCertificate(serverCertificate);
        } catch (RuntimeException e) {
            throw new TlsFatalAlert((short) 43, e);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void validateCertificateRequest(CertificateRequest certificateRequest) throws IOException {
        short[] types = certificateRequest.getCertificateTypes();
        for (short s : types) {
            switch (s) {
                case 1:
                case 2:
                case 64:
                    break;
                default:
                    throw new TlsFatalAlert((short) 47);
            }
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientCredentials(TlsCredentials clientCredentials) throws IOException {
        if (!(clientCredentials instanceof TlsSignerCredentials)) {
            throw new TlsFatalAlert((short) 80);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void generateClientKeyExchange(OutputStream output) throws IOException {
        this.premasterSecret = TlsRSAUtils.generateEncryptedPreMasterSecret(this.context, this.rsaServerPublicKey, output);
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientKeyExchange(InputStream input) throws IOException {
        byte[] encryptedPreMasterSecret;
        if (TlsUtils.isSSL(this.context)) {
            encryptedPreMasterSecret = Streams.readAll(input);
        } else {
            encryptedPreMasterSecret = TlsUtils.readOpaque16(input);
        }
        this.premasterSecret = this.serverCredentials.decryptPreMasterSecret(encryptedPreMasterSecret);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generatePremasterSecret() throws IOException {
        if (this.premasterSecret == null) {
            throw new TlsFatalAlert((short) 80);
        }
        byte[] tmp = this.premasterSecret;
        this.premasterSecret = null;
        return tmp;
    }

    protected RSAKeyParameters validateRSAPublicKey(RSAKeyParameters key) throws IOException {
        if (!key.getExponent().isProbablePrime(2)) {
            throw new TlsFatalAlert((short) 47);
        }
        return key;
    }
}
