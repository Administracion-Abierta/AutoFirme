package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/SkippingCipher.class */
public interface SkippingCipher {
    long skip(long j);

    long seekTo(long j);

    long getPosition();
}
