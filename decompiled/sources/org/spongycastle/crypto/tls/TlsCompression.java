package org.spongycastle.crypto.tls;

import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsCompression.class */
public interface TlsCompression {
    OutputStream compress(OutputStream outputStream);

    OutputStream decompress(OutputStream outputStream);
}
