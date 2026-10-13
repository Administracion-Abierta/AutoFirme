package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/MaxFragmentLength.class */
public class MaxFragmentLength {
    public static final short pow2_9 = 1;
    public static final short pow2_10 = 2;
    public static final short pow2_11 = 3;
    public static final short pow2_12 = 4;

    public static boolean isValid(short maxFragmentLength) {
        return maxFragmentLength >= 1 && maxFragmentLength <= 4;
    }
}
