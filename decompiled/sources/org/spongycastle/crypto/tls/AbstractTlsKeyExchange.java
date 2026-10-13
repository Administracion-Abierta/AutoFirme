package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/AbstractTlsKeyExchange.class */
public abstract class AbstractTlsKeyExchange implements TlsKeyExchange {
    protected int keyExchange;
    protected Vector supportedSignatureAlgorithms;
    protected TlsContext context;

    protected AbstractTlsKeyExchange(int keyExchange, Vector supportedSignatureAlgorithms) {
        this.keyExchange = keyExchange;
        this.supportedSignatureAlgorithms = supportedSignatureAlgorithms;
    }

    protected DigitallySigned parseSignature(InputStream input) throws IOException {
        DigitallySigned signature = DigitallySigned.parse(this.context, input);
        SignatureAndHashAlgorithm signatureAlgorithm = signature.getAlgorithm();
        if (signatureAlgorithm != null) {
            TlsUtils.verifySupportedSignatureAlgorithm(this.supportedSignatureAlgorithms, signatureAlgorithm);
        }
        return signature;
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void init(TlsContext context) {
        this.context = context;
        ProtocolVersion clientVersion = context.getClientVersion();
        if (TlsUtils.isSignatureAlgorithmsExtensionAllowed(clientVersion)) {
            if (this.supportedSignatureAlgorithms == null) {
                switch (this.keyExchange) {
                    case 1:
                    case 5:
                    case 9:
                    case 15:
                    case 18:
                    case 19:
                    case 23:
                        this.supportedSignatureAlgorithms = TlsUtils.getDefaultRSASignatureAlgorithms();
                        return;
                    case 2:
                    case 4:
                    case 6:
                    case 8:
                    case 10:
                    case 11:
                    case 12:
                    case 20:
                    default:
                        throw new IllegalStateException("unsupported key exchange algorithm");
                    case 3:
                    case 7:
                    case 22:
                        this.supportedSignatureAlgorithms = TlsUtils.getDefaultDSSSignatureAlgorithms();
                        return;
                    case 13:
                    case 14:
                    case 21:
                    case 24:
                        return;
                    case 16:
                    case 17:
                        this.supportedSignatureAlgorithms = TlsUtils.getDefaultECDSASignatureAlgorithms();
                        return;
                }
            }
            return;
        }
        if (this.supportedSignatureAlgorithms != null) {
            throw new IllegalStateException("supported_signature_algorithms not allowed for " + clientVersion);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCertificate(Certificate serverCertificate) throws IOException {
        if (this.supportedSignatureAlgorithms == null) {
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCredentials(TlsCredentials serverCredentials) throws IOException {
        processServerCertificate(serverCredentials.getCertificate());
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public boolean requiresServerKeyExchange() {
        return false;
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generateServerKeyExchange() throws IOException {
        if (requiresServerKeyExchange()) {
            throw new TlsFatalAlert((short) 80);
        }
        return null;
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerKeyExchange() throws IOException {
        if (requiresServerKeyExchange()) {
            throw new TlsFatalAlert((short) 10);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerKeyExchange(InputStream input) throws IOException {
        if (!requiresServerKeyExchange()) {
            throw new TlsFatalAlert((short) 10);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipClientCredentials() throws IOException {
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientCertificate(Certificate clientCertificate) throws IOException {
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientKeyExchange(InputStream input) throws IOException {
        throw new TlsFatalAlert((short) 80);
    }
}
