package org.spongycastle.crypto.modes.gcm;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/modes/gcm/GCMMultiplier.class */
public interface GCMMultiplier {
    void init(byte[] bArr);

    void multiplyH(byte[] bArr);
}
