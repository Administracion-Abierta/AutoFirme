package org.bouncycastle.crypto.prng;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/EntropyUtil.class */
public class EntropyUtil {
    public static byte[] generateSeed(EntropySource entropySource, int i) {
        byte[] bArr = new byte[i];
        if (i * 8 > entropySource.entropySize()) {
            int iEntropySize = entropySource.entropySize() / 8;
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= bArr.length) {
                    break;
                }
                byte[] entropy = entropySource.getEntropy();
                if (entropy.length <= bArr.length - i3) {
                    System.arraycopy(entropy, 0, bArr, i3, entropy.length);
                } else {
                    System.arraycopy(entropy, 0, bArr, i3, bArr.length - i3);
                }
                i2 = i3 + iEntropySize;
            }
        } else {
            System.arraycopy(entropySource.getEntropy(), 0, bArr, 0, bArr.length);
        }
        return bArr;
    }
}
