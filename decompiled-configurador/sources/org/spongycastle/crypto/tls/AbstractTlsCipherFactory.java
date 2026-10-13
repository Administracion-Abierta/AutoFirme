package org.spongycastle.crypto.tls;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/AbstractTlsCipherFactory.class */
public class AbstractTlsCipherFactory implements TlsCipherFactory {
    @Override // org.spongycastle.crypto.tls.TlsCipherFactory
    public TlsCipher createCipher(TlsContext context, int encryptionAlgorithm, int macAlgorithm) throws IOException {
        throw new TlsFatalAlert((short) 80);
    }
}
