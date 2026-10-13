package org.spongycastle.pqc.math.linearalgebra;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/GF2mVector.class */
public class GF2mVector extends Vector {
    private GF2mField field;
    private int[] vector;

    public GF2mVector(GF2mField field, byte[] v) {
        this.field = new GF2mField(field);
        int d = 8;
        int count = 1;
        while (field.getDegree() > d) {
            count++;
            d += 8;
        }
        if (v.length % count != 0) {
            throw new IllegalArgumentException("Byte array is not an encoded vector over the given finite field.");
        }
        this.length = v.length / count;
        this.vector = new int[this.length];
        int count2 = 0;
        for (int i = 0; i < this.vector.length; i++) {
            for (int j = 0; j < d; j += 8) {
                int[] iArr = this.vector;
                int i2 = i;
                int i3 = count2;
                count2++;
                iArr[i2] = iArr[i2] | ((v[i3] & 255) << j);
            }
            if (!field.isElementOfThisField(this.vector[i])) {
                throw new IllegalArgumentException("Byte array is not an encoded vector over the given finite field.");
            }
        }
    }

    public GF2mVector(GF2mField field, int[] vector) {
        this.field = field;
        this.length = vector.length;
        for (int i = vector.length - 1; i >= 0; i--) {
            if (!field.isElementOfThisField(vector[i])) {
                throw new ArithmeticException("Element array is not specified over the given finite field.");
            }
        }
        this.vector = IntUtils.clone(vector);
    }

    public GF2mVector(GF2mVector other) {
        this.field = new GF2mField(other.field);
        this.length = other.length;
        this.vector = IntUtils.clone(other.vector);
    }

    public GF2mField getField() {
        return this.field;
    }

    public int[] getIntArrayForm() {
        return IntUtils.clone(this.vector);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public byte[] getEncoded() {
        int d = 8;
        int count = 1;
        while (this.field.getDegree() > d) {
            count++;
            d += 8;
        }
        byte[] res = new byte[this.vector.length * count];
        int count2 = 0;
        for (int i = 0; i < this.vector.length; i++) {
            for (int j = 0; j < d; j += 8) {
                int i2 = count2;
                count2++;
                res[i2] = (byte) (this.vector[i] >>> j);
            }
        }
        return res;
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public boolean isZero() {
        for (int i = this.vector.length - 1; i >= 0; i--) {
            if (this.vector[i] != 0) {
                return false;
            }
        }
        return true;
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public Vector add(Vector addend) {
        throw new RuntimeException("not implemented");
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public Vector multiply(Permutation p) {
        int[] pVec = p.getVector();
        if (this.length != pVec.length) {
            throw new ArithmeticException("permutation size and vector size mismatch");
        }
        int[] result = new int[this.length];
        for (int i = 0; i < pVec.length; i++) {
            result[i] = this.vector[pVec[i]];
        }
        return new GF2mVector(this.field, result);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public boolean equals(Object other) {
        if (!(other instanceof GF2mVector)) {
            return false;
        }
        GF2mVector otherVec = (GF2mVector) other;
        if (!this.field.equals(otherVec.field)) {
            return false;
        }
        return IntUtils.equals(this.vector, otherVec.vector);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public int hashCode() {
        int hash = this.field.hashCode();
        return (hash * 31) + this.vector.hashCode();
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.Vector
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.vector.length; i++) {
            for (int j = 0; j < this.field.getDegree(); j++) {
                int r = j & 31;
                int bitMask = 1 << r;
                int coeff = this.vector[i] & bitMask;
                if (coeff != 0) {
                    buf.append('1');
                } else {
                    buf.append('0');
                }
            }
            buf.append(' ');
        }
        return buf.toString();
    }
}
