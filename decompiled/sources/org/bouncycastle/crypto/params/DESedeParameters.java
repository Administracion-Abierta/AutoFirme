package org.bouncycastle.crypto.params;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/params/DESedeParameters.class */
public class DESedeParameters extends DESParameters {
    public static final int DES_EDE_KEY_LENGTH = 24;

    public DESedeParameters(byte[] bArr) {
        super(bArr);
        if (isWeakKey(bArr, 0, bArr.length)) {
            throw new IllegalArgumentException("attempt to create weak DESede key");
        }
    }

    public static boolean isWeakKey(byte[] bArr, int i, int i2) {
        for (int i3 = i; i3 < i2; i3 += 8) {
            if (DESParameters.isWeakKey(bArr, i3)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeakKey(byte[] bArr, int i) {
        return isWeakKey(bArr, i, bArr.length - i);
    }

    public static boolean isRealEDEKey(byte[] bArr, int i) {
        return bArr.length == 16 ? isReal2Key(bArr, i) : isReal3Key(bArr, i);
    }

    public static boolean isReal2Key(byte[] bArr, int i) {
        boolean z = false;
        for (int i2 = i; i2 != i + 8; i2++) {
            if (bArr[i2] != bArr[i2 + 8]) {
                z = true;
            }
        }
        return z;
    }

    public static boolean isReal3Key(byte[] bArr, int i) {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        for (int i2 = i; i2 != i + 8; i2++) {
            z |= bArr[i2] != bArr[i2 + 8];
            z2 |= bArr[i2] != bArr[i2 + 16];
            z3 |= bArr[i2 + 8] != bArr[i2 + 16];
        }
        return z && z2 && z3;
    }
}
