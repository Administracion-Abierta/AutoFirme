package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/paddings/ISO7816d4Padding.class */
public class ISO7816d4Padding implements BlockCipherPadding {
    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public void init(SecureRandom secureRandom) throws IllegalArgumentException {
    }

    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public String getPaddingName() {
        return "ISO7816-4";
    }

    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int addPadding(byte[] bArr, int i) {
        int length = bArr.length - i;
        bArr[i] = -128;
        while (true) {
            i++;
            if (i >= bArr.length) {
                return length;
            }
            bArr[i] = 0;
        }
    }

    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int padCount(byte[] bArr) throws InvalidCipherTextException {
        int i = -1;
        int i2 = -1;
        int length = bArr.length;
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            int i3 = bArr[length] & 255;
            i ^= (length ^ i) & (i2 & (((i3 ^ 128) - 1) >> 31));
            i2 &= ((i3 ^ 0) - 1) >> 31;
        }
        if (i < 0) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return bArr.length - i;
    }
}
