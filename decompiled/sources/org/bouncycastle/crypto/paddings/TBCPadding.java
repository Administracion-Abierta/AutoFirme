package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/paddings/TBCPadding.class */
public class TBCPadding implements BlockCipherPadding {
    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public void init(SecureRandom secureRandom) throws IllegalArgumentException {
    }

    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public String getPaddingName() {
        return "TBC";
    }

    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int addPadding(byte[] bArr, int i) {
        byte b;
        int length = bArr.length - i;
        if (i > 0) {
            b = (byte) ((bArr[i - 1] & 1) == 0 ? 255 : 0);
        } else {
            b = (byte) ((bArr[bArr.length - 1] & 1) == 0 ? 255 : 0);
        }
        while (i < bArr.length) {
            bArr[i] = b;
            i++;
        }
        return length;
    }

    @Override // org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int padCount(byte[] bArr) throws InvalidCipherTextException {
        int length = bArr.length - 1;
        int i = bArr[length] & 255;
        int i2 = 1;
        int i3 = -1;
        while (true) {
            length--;
            if (length < 0) {
                return i2;
            }
            i3 &= (((bArr[length] & 255) ^ i) - 1) >> 31;
            i2 -= i3;
        }
    }
}
