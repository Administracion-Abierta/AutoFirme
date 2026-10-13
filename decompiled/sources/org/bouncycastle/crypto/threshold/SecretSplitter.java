package org.bouncycastle.crypto.threshold;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/threshold/SecretSplitter.class */
public interface SecretSplitter {
    SplitSecret split(int i, int i2);

    SplitSecret splitAround(SecretShare secretShare, int i, int i2) throws IOException;

    SplitSecret resplit(byte[] bArr, int i, int i2);
}
