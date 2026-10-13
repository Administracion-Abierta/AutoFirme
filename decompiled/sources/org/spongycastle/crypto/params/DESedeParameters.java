package org.spongycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/params/DESedeParameters.class */
public class DESedeParameters extends DESParameters {
    public static final int DES_EDE_KEY_LENGTH = 24;

    public DESedeParameters(byte[] key) {
        super(key);
        if (isWeakKey(key, 0, key.length)) {
            throw new IllegalArgumentException("attempt to create weak DESede key");
        }
    }

    public static boolean isWeakKey(byte[] key, int offset, int length) {
        for (int i = offset; i < length; i += 8) {
            if (DESParameters.isWeakKey(key, i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeakKey(byte[] key, int offset) {
        return isWeakKey(key, offset, key.length - offset);
    }

    public static boolean isRealEDEKey(byte[] key, int offset) {
        return key.length == 16 ? isReal2Key(key, offset) : isReal3Key(key, offset);
    }

    public static boolean isReal2Key(byte[] key, int offset) {
        boolean isValid = false;
        for (int i = offset; i != offset + 8; i++) {
            if (key[i] != key[i + 8]) {
                isValid = true;
            }
        }
        return isValid;
    }

    public static boolean isReal3Key(byte[] key, int offset) {
        boolean diff12 = false;
        boolean diff13 = false;
        boolean diff23 = false;
        for (int i = offset; i != offset + 8; i++) {
            diff12 |= key[i] != key[i + 8];
            diff13 |= key[i] != key[i + 16];
            diff23 |= key[i + 8] != key[i + 16];
        }
        return diff12 && diff13 && diff23;
    }
}
