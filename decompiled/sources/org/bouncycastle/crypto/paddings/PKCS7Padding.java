package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/paddings/PKCS7Padding.class */
public class PKCS7Padding implements BlockCipherPadding {
    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public void init(SecureRandom secureRandom) throws IllegalArgumentException {
    }

    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public String getPaddingName() {
        return "PKCS7";
    }

    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int addPadding(byte[] bArr, int i) {
        byte length = (byte) (bArr.length - i);
        while (i < bArr.length) {
            bArr[i] = length;
            i++;
        }
        return length;
    }

    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int padCount(byte[] bArr) throws InvalidCipherTextException {
        byte b = bArr[bArr.length - 1];
        int i = b & 255;
        int length = bArr.length - i;
        int i2 = (length | (i - 1)) >> 31;
        for (int i3 = 0; i3 < bArr.length; i3++) {
            i2 |= (bArr[i3] ^ b) & (((i3 - length) >> 31) ^ (-1));
        }
        if (i2 != 0) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return i;
    }
}
