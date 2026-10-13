package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/SRPTlsClient.class */
public class SRPTlsClient extends AbstractTlsClient {
    protected TlsSRPGroupVerifier groupVerifier;
    protected byte[] identity;
    protected byte[] password;

    public SRPTlsClient(byte[] identity, byte[] password) {
        this(new DefaultTlsCipherFactory(), new DefaultTlsSRPGroupVerifier(), identity, password);
    }

    public SRPTlsClient(TlsCipherFactory cipherFactory, byte[] identity, byte[] password) {
        this(cipherFactory, new DefaultTlsSRPGroupVerifier(), identity, password);
    }

    public SRPTlsClient(TlsCipherFactory cipherFactory, TlsSRPGroupVerifier groupVerifier, byte[] identity, byte[] password) {
        super(cipherFactory);
        this.groupVerifier = groupVerifier;
        this.identity = Arrays.clone(identity);
        this.password = Arrays.clone(password);
    }

    protected boolean requireSRPServerExtension() {
        return false;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public int[] getCipherSuites() {
        return new int[]{CipherSuite.TLS_SRP_SHA_RSA_WITH_AES_128_CBC_SHA};
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsClient, org.spongycastle.crypto.tls.TlsClient
    public Hashtable getClientExtensions() throws IOException {
        Hashtable clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(super.getClientExtensions());
        TlsSRPUtils.addSRPExtension(clientExtensions, this.identity);
        return clientExtensions;
    }

    @Override // org.spongycastle.crypto.tls.AbstractTlsClient, org.spongycastle.crypto.tls.TlsClient
    public void processServerExtensions(Hashtable serverExtensions) throws IOException {
        if (!TlsUtils.hasExpectedEmptyExtensionData(serverExtensions, TlsSRPUtils.EXT_SRP, (short) 47) && requireSRPServerExtension()) {
            throw new TlsFatalAlert((short) 47);
        }
        super.processServerExtensions(serverExtensions);
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
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

    @Override // org.spongycastle.crypto.tls.TlsClient
    public TlsAuthentication getAuthentication() throws IOException {
        throw new TlsFatalAlert((short) 80);
    }

    protected TlsKeyExchange createSRPKeyExchange(int keyExchange) {
        return new TlsSRPKeyExchange(keyExchange, this.supportedSignatureAlgorithms, this.groupVerifier, this.identity, this.password);
    }
}
