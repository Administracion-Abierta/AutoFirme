package org.spongycastle.crypto.paddings;

import java.security.SecureRandom;
import org.spongycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/paddings/BlockCipherPadding.class */
public interface BlockCipherPadding {
    void init(SecureRandom secureRandom) throws IllegalArgumentException;

    String getPaddingName();

    int addPadding(byte[] bArr, int i);

    int padCount(byte[] bArr) throws InvalidCipherTextException;
}
