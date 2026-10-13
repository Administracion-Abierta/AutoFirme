package org.bouncycastle.pqc.legacy.math.linearalgebra;

import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/Permutation.class */
public class Permutation {
    private int[] perm;

    public Permutation(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("invalid length");
        }
        this.perm = new int[i];
        for (int i2 = i - 1; i2 >= 0; i2--) {
            this.perm[i2] = i2;
        }
    }

    public Permutation(int[] iArr) {
        if (!isPermutation(iArr)) {
            throw new IllegalArgumentException("array is not a permutation vector");
        }
        this.perm = IntUtils.clone(iArr);
    }

    public Permutation(byte[] bArr) {
        if (bArr.length <= 4) {
            throw new IllegalArgumentException("invalid encoding");
        }
        int iOS2IP = LittleEndianConversions.OS2IP(bArr, 0);
        int iCeilLog256 = IntegerFunctions.ceilLog256(iOS2IP - 1);
        if (bArr.length != 4 + (iOS2IP * iCeilLog256)) {
            throw new IllegalArgumentException("invalid encoding");
        }
        this.perm = new int[iOS2IP];
        for (int i = 0; i < iOS2IP; i++) {
            this.perm[i] = LittleEndianConversions.OS2IP(bArr, 4 + (i * iCeilLog256), iCeilLog256);
        }
        if (!isPermutation(this.perm)) {
            throw new IllegalArgumentException("invalid encoding");
        }
    }

    public Permutation(int i, SecureRandom secureRandom) {
        if (i <= 0) {
            throw new IllegalArgumentException("invalid length");
        }
        this.perm = new int[i];
        int[] iArr = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            iArr[i2] = i2;
        }
        int i3 = i;
        for (int i4 = 0; i4 < i; i4++) {
            int iNextInt = RandUtils.nextInt(secureRandom, i3);
            i3--;
            this.perm[i4] = iArr[iNextInt];
            iArr[iNextInt] = iArr[i3];
        }
    }

    public byte[] getEncoded() {
        int length = this.perm.length;
        int iCeilLog256 = IntegerFunctions.ceilLog256(length - 1);
        byte[] bArr = new byte[4 + (length * iCeilLog256)];
        LittleEndianConversions.I2OSP(length, bArr, 0);
        for (int i = 0; i < length; i++) {
            LittleEndianConversions.I2OSP(this.perm[i], bArr, 4 + (i * iCeilLog256), iCeilLog256);
        }
        return bArr;
    }

    public int[] getVector() {
        return IntUtils.clone(this.perm);
    }

    public Permutation computeInverse() {
        Permutation permutation = new Permutation(this.perm.length);
        for (int length = this.perm.length - 1; length >= 0; length--) {
            permutation.perm[this.perm[length]] = length;
        }
        return permutation;
    }

    public Permutation rightMultiply(Permutation permutation) {
        if (permutation.perm.length != this.perm.length) {
            throw new IllegalArgumentException("length mismatch");
        }
        Permutation permutation2 = new Permutation(this.perm.length);
        for (int length = this.perm.length - 1; length >= 0; length--) {
            permutation2.perm[length] = this.perm[permutation.perm[length]];
        }
        return permutation2;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Permutation) {
            return IntUtils.equals(this.perm, ((Permutation) obj).perm);
        }
        return false;
    }

    public String toString() {
        String str = "[" + this.perm[0];
        for (int i = 1; i < this.perm.length; i++) {
            str = str + ", " + this.perm[i];
        }
        return str + "]";
    }

    public int hashCode() {
        return Arrays.hashCode(this.perm);
    }

    private boolean isPermutation(int[] iArr) {
        int length = iArr.length;
        boolean[] zArr = new boolean[length];
        for (int i = 0; i < length; i++) {
            if (iArr[i] < 0 || iArr[i] >= length || zArr[iArr[i]]) {
                return false;
            }
            zArr[iArr[i]] = true;
        }
        return true;
    }
}
