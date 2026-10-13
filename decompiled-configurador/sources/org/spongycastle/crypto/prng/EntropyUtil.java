package org.spongycastle.crypto.prng;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/prng/EntropyUtil.class */
public class EntropyUtil {
    public static byte[] generateSeed(EntropySource entropySource, int numBytes) {
        byte[] bytes = new byte[numBytes];
        if (numBytes * 8 <= entropySource.entropySize()) {
            System.arraycopy(entropySource.getEntropy(), 0, bytes, 0, bytes.length);
        } else {
            int entSize = entropySource.entropySize() / 8;
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= bytes.length) {
                    break;
                }
                byte[] ent = entropySource.getEntropy();
                if (ent.length <= bytes.length - i2) {
                    System.arraycopy(ent, 0, bytes, i2, ent.length);
                } else {
                    System.arraycopy(ent, 0, bytes, i2, bytes.length - i2);
                }
                i = i2 + entSize;
            }
        }
        return bytes;
    }
}
