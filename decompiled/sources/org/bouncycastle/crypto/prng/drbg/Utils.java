package org.bouncycastle.crypto.prng.drbg;

import java.util.Hashtable;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/prng/drbg/Utils.class */
class Utils {
    static final Hashtable maxSecurityStrengths = new Hashtable();

    Utils() {
    }

    static int getMaxSecurityStrength(Digest digest) {
        return ((Integer) maxSecurityStrengths.get(digest.getAlgorithmName())).intValue();
    }

    static int getMaxSecurityStrength(Mac mac) {
        String algorithmName = mac.getAlgorithmName();
        return ((Integer) maxSecurityStrengths.get(algorithmName.substring(0, algorithmName.indexOf("/")))).intValue();
    }

    static byte[] hash_df(Digest digest, byte[] bArr, int i) {
        byte[] bArr2 = new byte[(i + 7) / 8];
        int length = bArr2.length / digest.getDigestSize();
        int i2 = 1;
        byte[] bArr3 = new byte[digest.getDigestSize()];
        for (int i3 = 0; i3 <= length; i3++) {
            digest.update((byte) i2);
            digest.update((byte) (i >> 24));
            digest.update((byte) (i >> 16));
            digest.update((byte) (i >> 8));
            digest.update((byte) i);
            digest.update(bArr, 0, bArr.length);
            digest.doFinal(bArr3, 0);
            System.arraycopy(bArr3, 0, bArr2, i3 * bArr3.length, bArr2.length - (i3 * bArr3.length) > bArr3.length ? bArr3.length : bArr2.length - (i3 * bArr3.length));
            i2++;
        }
        if (i % 8 != 0) {
            int i4 = 8 - (i % 8);
            int i5 = 0;
            for (int i6 = 0; i6 != bArr2.length; i6++) {
                int i7 = bArr2[i6] & 255;
                bArr2[i6] = (byte) ((i7 >>> i4) | (i5 << (8 - i4)));
                i5 = i7;
            }
        }
        return bArr2;
    }

    static boolean isTooLarge(byte[] bArr, int i) {
        return bArr != null && bArr.length > i;
    }

    static {
        maxSecurityStrengths.put("SHA-1", Integers.valueOf(128));
        maxSecurityStrengths.put("SHA-224", Integers.valueOf(192));
        maxSecurityStrengths.put("SHA-256", Integers.valueOf(256));
        maxSecurityStrengths.put("SHA-384", Integers.valueOf(256));
        maxSecurityStrengths.put("SHA-512", Integers.valueOf(256));
        maxSecurityStrengths.put(MessageDigestAlgorithms.SHA_512_224, Integers.valueOf(192));
        maxSecurityStrengths.put("SHA-512/256", Integers.valueOf(256));
    }
}
