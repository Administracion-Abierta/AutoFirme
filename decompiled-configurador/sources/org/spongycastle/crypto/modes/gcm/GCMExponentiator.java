package org.spongycastle.crypto.modes.gcm;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/modes/gcm/GCMExponentiator.class */
public interface GCMExponentiator {
    void init(byte[] bArr);

    void exponentiateX(long j, byte[] bArr);
}
