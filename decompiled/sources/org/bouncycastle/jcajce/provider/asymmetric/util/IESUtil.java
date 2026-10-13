package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.jce.spec.IESParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/util/IESUtil.class */
public class IESUtil {
    public static IESParameterSpec guessParameterSpec(BufferedBlockCipher bufferedBlockCipher, byte[] bArr) {
        if (bufferedBlockCipher == null) {
            return new IESParameterSpec(null, null, 128);
        }
        BlockCipher underlyingCipher = bufferedBlockCipher.getUnderlyingCipher();
        if (underlyingCipher.getAlgorithmName().equals("DES") || underlyingCipher.getAlgorithmName().equals("RC2") || underlyingCipher.getAlgorithmName().equals("RC5-32") || underlyingCipher.getAlgorithmName().equals("RC5-64")) {
            return new IESParameterSpec(null, null, 64, 64, bArr);
        }
        if (underlyingCipher.getAlgorithmName().equals("SKIPJACK")) {
            return new IESParameterSpec(null, null, 80, 80, bArr);
        }
        return underlyingCipher.getAlgorithmName().equals("GOST28147") ? new IESParameterSpec(null, null, 256, 256, bArr) : new IESParameterSpec(null, null, 128, 128, bArr);
    }
}
