package org.spongycastle.crypto.tls;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/TlsCipher.class */
public interface TlsCipher {
    int getPlaintextLimit(int i);

    byte[] encodePlaintext(long j, short s, byte[] bArr, int i, int i2) throws IOException;

    byte[] decodeCiphertext(long j, short s, byte[] bArr, int i, int i2) throws IOException;
}
