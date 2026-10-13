package org.spongycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Vector;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.crypto.params.DHPrivateKeyParameters;
import org.spongycastle.crypto.params.DHPublicKeyParameters;
import org.spongycastle.crypto.util.PublicKeyFactory;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsDHKeyExchange.class */
public class TlsDHKeyExchange extends AbstractTlsKeyExchange {
    protected TlsSigner tlsSigner;
    protected DHParameters dhParameters;
    protected AsymmetricKeyParameter serverPublicKey;
    protected TlsAgreementCredentials agreementCredentials;
    protected DHPrivateKeyParameters dhAgreePrivateKey;
    protected DHPublicKeyParameters dhAgreePublicKey;

    public TlsDHKeyExchange(int keyExchange, Vector supportedSignatureAlgorithms, DHParameters dhParameters) {
        super(keyExchange, supportedSignatureAlgorithms);
        switch (keyExchange) {
            case 3:
                this.tlsSigner = new TlsDSSSigner();
                break;
            case 4:
            case 6:
            case 8:
            case 10:
            default:
                throw new IllegalArgumentException("unsupported key exchange algorithm");
            case 5:
                this.tlsSigner = new TlsRSASigner();
                break;
            case 7:
            case 9:
            case 11:
                this.tlsSigner = null;
                break;
        }
        this.dhParameters = dhParameters;
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void init(TlsContext context) {
        super.init(context);
        if (this.tlsSigner != null) {
            this.tlsSigner.init(context);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerCredentials() throws IOException {
        if (this.keyExchange != 11) {
            throw new TlsFatalAlert((short) 10);
        }
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCertificate(Certificate serverCertificate) throws IOException {
        if (this.keyExchange == 11) {
            throw new TlsFatalAlert((short) 10);
        }
        if (serverCertificate.isEmpty()) {
            throw new TlsFatalAlert((short) 42);
        }
        org.spongycastle.asn1.x509.Certificate x509Cert = serverCertificate.getCertificateAt(0);
        SubjectPublicKeyInfo keyInfo = x509Cert.getSubjectPublicKeyInfo();
        try {
            this.serverPublicKey = PublicKeyFactory.createKey(keyInfo);
            if (this.tlsSigner == null) {
                try {
                    this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey((DHPublicKeyParameters) this.serverPublicKey);
                    this.dhParameters = validateDHParameters(this.dhAgreePublicKey.getParameters());
                    TlsUtils.validateKeyUsage(x509Cert, 8);
                } catch (ClassCastException e) {
                    throw new TlsFatalAlert((short) 46, e);
                }
            } else {
                if (!this.tlsSigner.isValidPublicKey(this.serverPublicKey)) {
                    throw new TlsFatalAlert((short) 46);
                }
                TlsUtils.validateKeyUsage(x509Cert, 128);
            }
            super.processServerCertificate(serverCertificate);
        } catch (RuntimeException e2) {
            throw new TlsFatalAlert((short) 43, e2);
        }
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public boolean requiresServerKeyExchange() {
        switch (this.keyExchange) {
            case 3:
            case 5:
            case 11:
                return true;
            default:
                return false;
        }
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generateServerKeyExchange() throws IOException {
        if (!requiresServerKeyExchange()) {
            return null;
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.dhParameters, buf);
        return buf.toByteArray();
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerKeyExchange(InputStream input) throws IOException {
        if (!requiresServerKeyExchange()) {
            throw new TlsFatalAlert((short) 10);
        }
        ServerDHParams dhParams = ServerDHParams.parse(input);
        this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(dhParams.getPublicKey());
        this.dhParameters = validateDHParameters(this.dhAgreePublicKey.getParameters());
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void validateCertificateRequest(CertificateRequest certificateRequest) throws IOException {
        if (this.keyExchange == 11) {
            throw new TlsFatalAlert((short) 40);
        }
        short[] types = certificateRequest.getCertificateTypes();
        for (short s : types) {
            switch (s) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 64:
                    break;
                default:
                    throw new TlsFatalAlert((short) 47);
            }
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientCredentials(TlsCredentials clientCredentials) throws IOException {
        if (this.keyExchange == 11) {
            throw new TlsFatalAlert((short) 80);
        }
        if (clientCredentials instanceof TlsAgreementCredentials) {
            this.agreementCredentials = (TlsAgreementCredentials) clientCredentials;
        } else if (!(clientCredentials instanceof TlsSignerCredentials)) {
            throw new TlsFatalAlert((short) 80);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void generateClientKeyExchange(OutputStream output) throws IOException {
        if (this.agreementCredentials == null) {
            this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.dhParameters, output);
        }
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientCertificate(Certificate clientCertificate) throws IOException {
        if (this.keyExchange == 11) {
            throw new TlsFatalAlert((short) 10);
        }
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientKeyExchange(InputStream input) throws IOException {
        if (this.dhAgreePublicKey != null) {
            return;
        }
        BigInteger Yc = TlsDHUtils.readDHParameter(input);
        this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(new DHPublicKeyParameters(Yc, this.dhParameters));
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generatePremasterSecret() throws IOException {
        if (this.agreementCredentials != null) {
            return this.agreementCredentials.generateAgreement(this.dhAgreePublicKey);
        }
        if (this.dhAgreePrivateKey != null) {
            return TlsDHUtils.calculateDHBasicAgreement(this.dhAgreePublicKey, this.dhAgreePrivateKey);
        }
        throw new TlsFatalAlert((short) 80);
    }

    protected int getMinimumPrimeBits() {
        return 1024;
    }

    protected DHParameters validateDHParameters(DHParameters params) throws IOException {
        if (params.getP().bitLength() < getMinimumPrimeBits()) {
            throw new TlsFatalAlert((short) 71);
        }
        return TlsDHUtils.validateDHParameters(params);
    }
}
