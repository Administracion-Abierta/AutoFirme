package org.bouncycastle.pqc.legacy.math.linearalgebra;

import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/GF2Vector.class */
public class GF2Vector extends Vector {
    private int[] v;

    public GF2Vector(int i) {
        if (i < 0) {
            throw new ArithmeticException("Negative length.");
        }
        this.length = i;
        this.v = new int[(i + 31) >> 5];
    }

    public GF2Vector(int i, SecureRandom secureRandom) {
        this.length = i;
        int i2 = (i + 31) >> 5;
        this.v = new int[i2];
        for (int i3 = i2 - 1; i3 >= 0; i3--) {
            this.v[i3] = secureRandom.nextInt();
        }
        int i4 = i & 31;
        if (i4 != 0) {
            int[] iArr = this.v;
            int i5 = i2 - 1;
            iArr[i5] = iArr[i5] & ((1 << i4) - 1);
        }
    }

    public GF2Vector(int i, int i2, SecureRandom secureRandom) {
        if (i2 > i) {
            throw new ArithmeticException("The hamming weight is greater than the length of vector.");
        }
        this.length = i;
        this.v = new int[(i + 31) >> 5];
        int[] iArr = new int[i];
        for (int i3 = 0; i3 < i; i3++) {
            iArr[i3] = i3;
        }
        int i4 = i;
        for (int i5 = 0; i5 < i2; i5++) {
            int iNextInt = RandUtils.nextInt(secureRandom, i4);
            setBit(iArr[iNextInt]);
            i4--;
            iArr[iNextInt] = iArr[i4];
        }
    }

    public GF2Vector(int i, int[] iArr) {
        if (i < 0) {
            throw new ArithmeticException("negative length");
        }
        this.length = i;
        int i2 = (i + 31) >> 5;
        if (iArr.length != i2) {
            throw new ArithmeticException("length mismatch");
        }
        this.v = IntUtils.clone(iArr);
        int i3 = i & 31;
        if (i3 != 0) {
            int[] iArr2 = this.v;
            int i4 = i2 - 1;
            iArr2[i4] = iArr2[i4] & ((1 << i3) - 1);
        }
    }

    public GF2Vector(GF2Vector gF2Vector) {
        this.length = gF2Vector.length;
        this.v = IntUtils.clone(gF2Vector.v);
    }

    protected GF2Vector(int[] iArr, int i) {
        this.v = iArr;
        this.length = i;
    }

    public static GF2Vector OS2VP(int i, byte[] bArr) {
        if (i < 0) {
            throw new ArithmeticException("negative length");
        }
        if (bArr.length > ((i + 7) >> 3)) {
            throw new ArithmeticException("length mismatch");
        }
        return new GF2Vector(i, LittleEndianConversions.toIntArray(bArr));
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Vector
    public byte[] getEncoded() {
        return LittleEndianConversions.toByteArray(this.v, (this.length + 7) >> 3);
    }

    public int[] getVecArray() {
        return this.v;
    }

    public int getHammingWeight() {
        int i = 0;
        for (int i2 = 0; i2 < this.v.length; i2++) {
            int i3 = this.v[i2];
            for (int i4 = 0; i4 < 32; i4++) {
                if ((i3 & 1) != 0) {
                    i++;
                }
                i3 >>>= 1;
            }
        }
        return i;
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Vector
    public boolean isZero() {
        for (int length = this.v.length - 1; length >= 0; length--) {
            if (this.v[length] != 0) {
                return false;
            }
        }
        return true;
    }

    public int getBit(int i) {
        if (i >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        int i2 = i & 31;
        return (this.v[i >> 5] & (1 << i2)) >>> i2;
    }

    public void setBit(int i) {
        if (i >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        int[] iArr = this.v;
        int i2 = i >> 5;
        iArr[i2] = iArr[i2] | (1 << (i & 31));
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Vector
    public Vector add(Vector vector) {
        if (!(vector instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (this.length != ((GF2Vector) vector).length) {
            throw new ArithmeticException("length mismatch");
        }
        int[] iArrClone = IntUtils.clone(((GF2Vector) vector).v);
        for (int length = iArrClone.length - 1; length >= 0; length--) {
            int i = length;
            iArrClone[i] = iArrClone[i] ^ this.v[length];
        }
        return new GF2Vector(this.length, iArrClone);
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Vector
    public Vector multiply(Permutation permutation) {
        int[] vector = permutation.getVector();
        if (this.length != vector.length) {
            throw new ArithmeticException("length mismatch");
        }
        GF2Vector gF2Vector = new GF2Vector(this.length);
        for (int i = 0; i < vector.length; i++) {
            if ((this.v[vector[i] >> 5] & (1 << (vector[i] & 31))) != 0) {
                int[] iArr = gF2Vector.v;
                int i2 = i >> 5;
                iArr[i2] = iArr[i2] | (1 << (i & 31));
            }
        }
        return gF2Vector;
    }

    public GF2Vector extractVector(int[] iArr) {
        int length = iArr.length;
        if (iArr[length - 1] > this.length) {
            throw new ArithmeticException("invalid index set");
        }
        GF2Vector gF2Vector = new GF2Vector(length);
        for (int i = 0; i < length; i++) {
            if ((this.v[iArr[i] >> 5] & (1 << (iArr[i] & 31))) != 0) {
                int[] iArr2 = gF2Vector.v;
                int i2 = i >> 5;
                iArr2[i2] = iArr2[i2] | (1 << (i & 31));
            }
        }
        return gF2Vector;
    }

    public GF2Vector extractLeftVector(int i) {
        if (i > this.length) {
            throw new ArithmeticException("invalid length");
        }
        if (i == this.length) {
            return new GF2Vector(this);
        }
        GF2Vector gF2Vector = new GF2Vector(i);
        int i2 = i >> 5;
        int i3 = i & 31;
        System.arraycopy(this.v, 0, gF2Vector.v, 0, i2);
        if (i3 != 0) {
            gF2Vector.v[i2] = this.v[i2] & ((1 << i3) - 1);
        }
        return gF2Vector;
    }

    public GF2Vector extractRightVector(int i) {
        if (i > this.length) {
            throw new ArithmeticException("invalid length");
        }
        if (i == this.length) {
            return new GF2Vector(this);
        }
        GF2Vector gF2Vector = new GF2Vector(i);
        int i2 = (this.length - i) >> 5;
        int i3 = (this.length - i) & 31;
        int i4 = (i + 31) >> 5;
        int i5 = i2;
        if (i3 != 0) {
            for (int i6 = 0; i6 < i4 - 1; i6++) {
                int i7 = i5;
                i5++;
                gF2Vector.v[i6] = (this.v[i7] >>> i3) | (this.v[i5] << (32 - i3));
            }
            int i8 = i5;
            int i9 = i5 + 1;
            gF2Vector.v[i4 - 1] = this.v[i8] >>> i3;
            if (i9 < this.v.length) {
                int[] iArr = gF2Vector.v;
                int i10 = i4 - 1;
                iArr[i10] = iArr[i10] | (this.v[i9] << (32 - i3));
            }
        } else {
            System.arraycopy(this.v, i2, gF2Vector.v, 0, i4);
        }
        return gF2Vector;
    }

    public GF2mVector toExtensionFieldVector(GF2mField gF2mField) {
        int degree = gF2mField.getDegree();
        if (this.length % degree != 0) {
            throw new ArithmeticException("conversion is impossible");
        }
        int i = this.length / degree;
        int[] iArr = new int[i];
        int i2 = 0;
        for (int i3 = i - 1; i3 >= 0; i3--) {
            for (int degree2 = gF2mField.getDegree() - 1; degree2 >= 0; degree2--) {
                if (((this.v[i2 >>> 5] >>> (i2 & 31)) & 1) == 1) {
                    int i4 = i3;
                    iArr[i4] = iArr[i4] ^ (1 << degree2);
                }
                i2++;
            }
        }
        return new GF2mVector(gF2mField, iArr);
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Vector
    public boolean equals(Object obj) {
        if (!(obj instanceof GF2Vector)) {
            return false;
        }
        GF2Vector gF2Vector = (GF2Vector) obj;
        return this.length == gF2Vector.length && IntUtils.equals(this.v, gF2Vector.v);
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Vector
    public int hashCode() {
        return (this.length * 31) + Arrays.hashCode(this.v);
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.Vector
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.length; i++) {
            if (i != 0 && (i & 31) == 0) {
                stringBuffer.append(' ');
            }
            if ((this.v[i >> 5] & (1 << (i & 31))) == 0) {
                stringBuffer.append('0');
            } else {
                stringBuffer.append('1');
            }
        }
        return stringBuffer.toString();
    }
}
