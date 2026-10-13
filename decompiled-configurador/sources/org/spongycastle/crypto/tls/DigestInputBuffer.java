package org.spongycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import org.spongycastle.crypto.Digest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/DigestInputBuffer.class */
class DigestInputBuffer extends ByteArrayOutputStream {
    DigestInputBuffer() {
    }

    void updateDigest(Digest d) {
        d.update(this.buf, 0, this.count);
    }
}
