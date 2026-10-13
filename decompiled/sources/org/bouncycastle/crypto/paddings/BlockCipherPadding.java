package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/paddings/BlockCipherPadding.class */
public interface BlockCipherPadding {
    void init(SecureRandom secureRandom) throws IllegalArgumentException;

    String getPaddingName();

    int addPadding(byte[] bArr, int i);

    int padCount(byte[] bArr) throws InvalidCipherTextException;
}
