package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/Poly1305KeyGenerator.class */
public class Poly1305KeyGenerator extends CipherKeyGenerator {
    private static final byte R_MASK_LOW_2 = -4;
    private static final byte R_MASK_HIGH_4 = 15;

    @Override // org.bouncycastle.crypto.CipherKeyGenerator
    public void init(KeyGenerationParameters keyGenerationParameters) {
        super.init(new KeyGenerationParameters(keyGenerationParameters.getRandom(), 256));
    }

    @Override // org.bouncycastle.crypto.CipherKeyGenerator
    public byte[] generateKey() {
        byte[] bArrGenerateKey = super.generateKey();
        clamp(bArrGenerateKey);
        return bArrGenerateKey;
    }

    public static void clamp(byte[] bArr) {
        if (bArr.length != 32) {
            throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
        }
        bArr[3] = (byte) (bArr[3] & 15);
        bArr[7] = (byte) (bArr[7] & 15);
        bArr[11] = (byte) (bArr[11] & 15);
        bArr[15] = (byte) (bArr[15] & 15);
        bArr[4] = (byte) (bArr[4] & (-4));
        bArr[8] = (byte) (bArr[8] & (-4));
        bArr[12] = (byte) (bArr[12] & (-4));
    }

    public static void checkKey(byte[] bArr) {
        if (bArr.length != 32) {
            throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
        }
        checkMask(bArr[3], (byte) 15);
        checkMask(bArr[7], (byte) 15);
        checkMask(bArr[11], (byte) 15);
        checkMask(bArr[15], (byte) 15);
        checkMask(bArr[4], (byte) -4);
        checkMask(bArr[8], (byte) -4);
        checkMask(bArr[12], (byte) -4);
    }

    private static void checkMask(byte b, byte b2) {
        if ((b & (b2 ^ (-1))) != 0) {
            throw new IllegalArgumentException("Invalid format for r portion of Poly1305 key.");
        }
    }
}
