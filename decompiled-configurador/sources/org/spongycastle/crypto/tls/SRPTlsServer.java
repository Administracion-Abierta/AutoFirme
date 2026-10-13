package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/SRPTlsServer.class */
public class SRPTlsServer extends AbstractTlsServer {
    protected TlsSRPIdentityManager srpIdentityManager;
    protected byte[] srpIdentity;
    protected TlsSRPLoginParameters loginParameters;

    public SRPTlsServer(TlsSRPIdentityManager srpIdentityManager) {
        this(new DefaultTlsCipherFactory(), srpIdentityManager);
    }

    public SRPTlsServer(TlsCipherFactory cipherFactory, TlsSRPIdentityManager srpIdentityManager) {
        super(cipherFactory);
        this.srpIdentity = null;
        this.loginParameters = null;
        this.srpIdentityManager = srpIdentityManager;
    }

    protected TlsSignerCredentials getDSASignerCredentials() throws IOException {
        throw new TlsFatalAlert((short) 80);
    }

    protected TlsSignerCredentials getRSASignerCredentials() throws IOException {
        throw new TlsFatalAlert((short) 80);
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsServer
    protected int[] getCipherSuites() {
        return new int[]{CipherSuite.TLS_SRP_SHA_DSS_WITH_AES_256_CBC_SHA, CipherSuite.TLS_SRP_SHA_DSS_WITH_AES_128_CBC_SHA, CipherSuite.TLS_SRP_SHA_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_SRP_SHA_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_SRP_SHA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_SRP_SHA_WITH_AES_128_CBC_SHA};
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsServer, org.spongycastle.crypto.tls.TlsServer
    public void processClientExtensions(Hashtable clientExtensions) throws IOException {
        super.processClientExtensions(clientExtensions);
        this.srpIdentity = TlsSRPUtils.getSRPExtension(clientExtensions);
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsServer, org.spongycastle.crypto.tls.TlsServer
    public int getSelectedCipherSuite() throws IOException {
        int cipherSuite = super.getSelectedCipherSuite();
        if (TlsSRPUtils.isSRPCipherSuite(cipherSuite)) {
            if (this.srpIdentity != null) {
                this.loginParameters = this.srpIdentityManager.getLoginParameters(this.srpIdentity);
            }
            if (this.loginParameters == null) {
                throw new TlsFatalAlert((short) 115);
            }
        }
        return cipherSuite;
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public TlsCredentials getCredentials() throws IOException {
        int keyExchangeAlgorithm = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
        switch (keyExchangeAlgorithm) {
            case 21:
                return null;
            case 22:
                return getDSASignerCredentials();
            case 23:
                return getRSASignerCredentials();
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsServer
    public TlsKeyExchange getKeyExchange() throws IOException {
        int keyExchangeAlgorithm = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
        switch (keyExchangeAlgorithm) {
            case 21:
            case 22:
            case 23:
                return createSRPKeyExchange(keyExchangeAlgorithm);
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    protected TlsKeyExchange createSRPKeyExchange(int keyExchange) {
        return new TlsSRPKeyExchange(keyExchange, this.supportedSignatureAlgorithms, this.srpIdentity, this.loginParameters);
    }
}
