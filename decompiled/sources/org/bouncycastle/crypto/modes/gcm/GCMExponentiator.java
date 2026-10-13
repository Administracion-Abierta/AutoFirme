package org.bouncycastle.crypto.modes.gcm;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/gcm/GCMExponentiator.class */
public interface GCMExponentiator {
    void init(byte[] bArr);

    void exponentiateX(long j, byte[] bArr);
}
