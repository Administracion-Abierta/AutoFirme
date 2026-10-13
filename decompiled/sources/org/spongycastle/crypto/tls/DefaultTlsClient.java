package org.spongycastle.crypto.tls;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DefaultTlsClient.class */
public abstract class DefaultTlsClient extends AbstractTlsClient {
    public DefaultTlsClient() {
    }

    public DefaultTlsClient(TlsCipherFactory cipherFactory) {
        super(cipherFactory);
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public int[] getCipherSuites() {
        return new int[]{CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, 162, 64, 50, 158, 103, 51, 156, 60, 47};
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public TlsKeyExchange getKeyExchange() throws IOException {
        int keyExchangeAlgorithm = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
        switch (keyExchangeAlgorithm) {
            case 1:
                return createRSAKeyExchange();
            case 2:
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                throw new TlsFatalAlert((short) 80);
            case 3:
            case 5:
                return createDHEKeyExchange(keyExchangeAlgorithm);
            case 7:
            case 9:
            case 11:
                return createDHKeyExchange(keyExchangeAlgorithm);
            case 16:
            case 18:
            case 20:
                return createECDHKeyExchange(keyExchangeAlgorithm);
            case 17:
            case 19:
                return createECDHEKeyExchange(keyExchangeAlgorithm);
        }
    }

    protected TlsKeyExchange createDHKeyExchange(int keyExchange) {
        return new TlsDHKeyExchange(keyExchange, this.supportedSignatureAlgorithms, null);
    }

    protected TlsKeyExchange createDHEKeyExchange(int keyExchange) {
        return new TlsDHEKeyExchange(keyExchange, this.supportedSignatureAlgorithms, null);
    }

    protected TlsKeyExchange createECDHKeyExchange(int keyExchange) {
        return new TlsECDHKeyExchange(keyExchange, this.supportedSignatureAlgorithms, this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
    }

    protected TlsKeyExchange createECDHEKeyExchange(int keyExchange) {
        return new TlsECDHEKeyExchange(keyExchange, this.supportedSignatureAlgorithms, this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
    }

    protected TlsKeyExchange createRSAKeyExchange() {
        return new TlsRSAKeyExchange(this.supportedSignatureAlgorithms);
    }
}
