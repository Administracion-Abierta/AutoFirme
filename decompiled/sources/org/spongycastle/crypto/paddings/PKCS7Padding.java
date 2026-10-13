package org.spongycastle.crypto.paddings;

import java.security.SecureRandom;
import org.spongycastle.crypto.InvalidCipherTextException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/paddings/PKCS7Padding.class */
public class PKCS7Padding implements BlockCipherPadding {
    @Override // org.spongycastle.crypto.paddings.BlockCipherPadding
    public void init(SecureRandom random) throws IllegalArgumentException {
    }

    @Override // org.spongycastle.crypto.paddings.BlockCipherPadding
    public String getPaddingName() {
        return "PKCS7";
    }

    @Override // org.spongycastle.crypto.paddings.BlockCipherPadding
    public int addPadding(byte[] in, int inOff) {
        byte code = (byte) (in.length - inOff);
        while (inOff < in.length) {
            in[inOff] = code;
            inOff++;
        }
        return code;
    }

    @Override // org.spongycastle.crypto.paddings.BlockCipherPadding
    public int padCount(byte[] in) throws InvalidCipherTextException {
        int count = in[in.length - 1] & 255;
        byte countAsbyte = (byte) count;
        boolean failed = (count > in.length) | (count == 0);
        for (int i = 0; i < in.length; i++) {
            failed |= (in.length - i <= count) & (in[i] != countAsbyte);
        }
        if (failed) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return count;
    }
}
