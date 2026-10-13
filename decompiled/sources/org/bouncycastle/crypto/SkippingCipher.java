package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/SkippingCipher.class */
public interface SkippingCipher {
    long skip(long j);

    long seekTo(long j);

    long getPosition();
}
