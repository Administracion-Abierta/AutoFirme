package org.bouncycastle.crypto.modes.kgcm;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/kgcm/KGCMMultiplier.class */
public interface KGCMMultiplier {
    void init(long[] jArr);

    void multiplyH(long[] jArr);
}
