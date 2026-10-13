package org.spongycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/GF2Vector.class */
public class GF2Vector extends Vector {
    private int[] v;

    public GF2Vector(int length) {
        if (length < 0) {
            throw new ArithmeticException("Negative length.");
        }
        this.length = length;
        this.v = new int[(length + 31) >> 5];
    }

    public GF2Vector(int length, SecureRandom sr) {
        this.length = length;
        int size = (length + 31) >> 5;
        this.v = new int[size];
        for (int i = size - 1; i >= 0; i--) {
            this.v[i] = sr.nextInt();
        }
        int r = length & 31;
        if (r != 0) {
            int[] iArr = this.v;
            int i2 = size - 1;
            iArr[i2] = iArr[i2] & ((1 << r) - 1);
        }
    }

    public GF2Vector(int length, int t, SecureRandom sr) {
        if (t > length) {
            throw new ArithmeticException("The hamming weight is greater than the length of vector.");
        }
        this.length = length;
        int size = (length + 31) >> 5;
        this.v = new int[size];
        int[] help = new int[length];
        for (int i = 0; i < length; i++) {
            help[i] = i;
        }
        int m = length;
        for (int i2 = 0; i2 < t; i2++) {
            int j = RandUtils.nextInt(sr, m);
            setBit(help[j]);
            m--;
            help[j] = help[m];
        }
    }

    public GF2Vector(int length, int[] v) {
        if (length < 0) {
            throw new ArithmeticException("negative length");
        }
        this.length = length;
        int size = (length + 31) >> 5;
        if (v.length != size) {
            throw new ArithmeticException("length mismatch");
        }
        this.v = IntUtils.clone(v);
        int r = length & 31;
        if (r != 0) {
            int[] iArr = this.v;
            int i = size - 1;
            iArr[i] = iArr[i] & ((1 << r) - 1);
        }
    }

    public GF2Vector(GF2Vector other) {
        this.length = other.length;
        this.v = IntUtils.clone(other.v);
    }

    protected GF2Vector(int[] v, int length) {
        this.v = v;
        this.length = length;
    }

    public static GF2Vector OS2VP(int length, byte[] encVec) {
        if (length < 0) {
            throw new ArithmeticException("negative length");
        }
        int byteLen = (length + 7) >> 3;
        if (encVec.length > byteLen) {
            throw new ArithmeticException("length mismatch");
        }
        return new GF2Vector(length, LittleEndianConversions.toIntArray(encVec));
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public byte[] getEncoded() {
        int byteLen = (this.length + 7) >> 3;
        return LittleEndianConversions.toByteArray(this.v, byteLen);
    }

    public int[] getVecArray() {
        return this.v;
    }

    public int getHammingWeight() {
        int weight = 0;
        for (int i = 0; i < this.v.length; i++) {
            int e = this.v[i];
            for (int j = 0; j < 32; j++) {
                int b = e & 1;
                if (b != 0) {
                    weight++;
                }
                e >>>= 1;
            }
        }
        return weight;
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public boolean isZero() {
        for (int i = this.v.length - 1; i >= 0; i--) {
            if (this.v[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public int getBit(int index) {
        if (index >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        int q = index >> 5;
        int r = index & 31;
        return (this.v[q] & (1 << r)) >>> r;
    }

    public void setBit(int index) {
        if (index >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        int[] iArr = this.v;
        int i = index >> 5;
        iArr[i] = iArr[i] | (1 << (index & 31));
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public Vector add(Vector other) {
        if (!(other instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        GF2Vector otherVec = (GF2Vector) other;
        if (this.length != otherVec.length) {
            throw new ArithmeticException("length mismatch");
        }
        int[] vec = IntUtils.clone(((GF2Vector) other).v);
        for (int i = vec.length - 1; i >= 0; i--) {
            int i2 = i;
            vec[i2] = vec[i2] ^ this.v[i];
        }
        return new GF2Vector(this.length, vec);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public Vector multiply(Permutation p) {
        int[] pVec = p.getVector();
        if (this.length != pVec.length) {
            throw new ArithmeticException("length mismatch");
        }
        GF2Vector result = new GF2Vector(this.length);
        for (int i = 0; i < pVec.length; i++) {
            int e = this.v[pVec[i] >> 5] & (1 << (pVec[i] & 31));
            if (e != 0) {
                int[] iArr = result.v;
                int i2 = i >> 5;
                iArr[i2] = iArr[i2] | (1 << (i & 31));
            }
        }
        return result;
    }

    public GF2Vector extractVector(int[] setJ) {
        int k = setJ.length;
        if (setJ[k - 1] > this.length) {
            throw new ArithmeticException("invalid index set");
        }
        GF2Vector result = new GF2Vector(k);
        for (int i = 0; i < k; i++) {
            int e = this.v[setJ[i] >> 5] & (1 << (setJ[i] & 31));
            if (e != 0) {
                int[] iArr = result.v;
                int i2 = i >> 5;
                iArr[i2] = iArr[i2] | (1 << (i & 31));
            }
        }
        return result;
    }

    public GF2Vector extractLeftVector(int k) {
        if (k > this.length) {
            throw new ArithmeticException("invalid length");
        }
        if (k == this.length) {
            return new GF2Vector(this);
        }
        GF2Vector result = new GF2Vector(k);
        int q = k >> 5;
        int r = k & 31;
        System.arraycopy(this.v, 0, result.v, 0, q);
        if (r != 0) {
            result.v[q] = this.v[q] & ((1 << r) - 1);
        }
        return result;
    }

    public GF2Vector extractRightVector(int k) {
        if (k > this.length) {
            throw new ArithmeticException("invalid length");
        }
        if (k == this.length) {
            return new GF2Vector(this);
        }
        GF2Vector result = new GF2Vector(k);
        int q = (this.length - k) >> 5;
        int r = (this.length - k) & 31;
        int length = (k + 31) >> 5;
        int ind = q;
        if (r != 0) {
            for (int i = 0; i < length - 1; i++) {
                int i2 = ind;
                ind++;
                result.v[i] = (this.v[i2] >>> r) | (this.v[ind] << (32 - r));
            }
            int i3 = ind;
            int ind2 = ind + 1;
            result.v[length - 1] = this.v[i3] >>> r;
            if (ind2 < this.v.length) {
                int[] iArr = result.v;
                int i4 = length - 1;
                iArr[i4] = iArr[i4] | (this.v[ind2] << (32 - r));
            }
        } else {
            System.arraycopy(this.v, q, result.v, 0, length);
        }
        return result;
    }

    public GF2mVector toExtensionFieldVector(GF2mField field) {
        int m = field.getDegree();
        if (this.length % m != 0) {
            throw new ArithmeticException("conversion is impossible");
        }
        int t = this.length / m;
        int[] result = new int[t];
        int count = 0;
        for (int i = t - 1; i >= 0; i--) {
            for (int j = field.getDegree() - 1; j >= 0; j--) {
                int q = count >>> 5;
                int r = count & 31;
                int e = (this.v[q] >>> r) & 1;
                if (e == 1) {
                    int i2 = i;
                    result[i2] = result[i2] ^ (1 << j);
                }
                count++;
            }
        }
        return new GF2mVector(field, result);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public boolean equals(Object other) {
        if (!(other instanceof GF2Vector)) {
            return false;
        }
        GF2Vector otherVec = (GF2Vector) other;
        return this.length == otherVec.length && IntUtils.equals(this.v, otherVec.v);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public int hashCode() {
        int hash = this.length;
        return (hash * 31) + this.v.hashCode();
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.length; i++) {
            if (i != 0 && (i & 31) == 0) {
                buf.append(' ');
            }
            int q = i >> 5;
            int r = i & 31;
            int bit = this.v[q] & (1 << r);
            if (bit == 0) {
                buf.append('0');
            } else {
                buf.append('1');
            }
        }
        return buf.toString();
    }
}
