package org.spongycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/GF2nField.class */
public abstract class GF2nField {
    protected final SecureRandom random;
    protected int mDegree;
    protected GF2Polynomial fieldPolynomial;
    protected java.util.Vector fields;
    protected java.util.Vector matrices;

    protected abstract GF2nElement getRandomRoot(GF2Polynomial gF2Polynomial);

    protected abstract void computeCOBMatrix(GF2nField gF2nField);

    protected abstract void computeFieldPolynomial();

    protected GF2nField(SecureRandom random) {
        this.random = random;
    }

    public final int getDegree() {
        return this.mDegree;
    }

    public final GF2Polynomial getFieldPolynomial() {
        if (this.fieldPolynomial == null) {
            computeFieldPolynomial();
        }
        return new GF2Polynomial(this.fieldPolynomial);
    }

    public final boolean equals(Object other) {
        if (other == null || !(other instanceof GF2nField)) {
            return false;
        }
        GF2nField otherField = (GF2nField) other;
        if (otherField.mDegree != this.mDegree || !this.fieldPolynomial.equals(otherField.fieldPolynomial)) {
            return false;
        }
        if ((this instanceof GF2nPolynomialField) && !(otherField instanceof GF2nPolynomialField)) {
            return false;
        }
        if ((this instanceof GF2nONBField) && !(otherField instanceof GF2nONBField)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.mDegree + this.fieldPolynomial.hashCode();
    }

    protected final GF2Polynomial[] invertMatrix(GF2Polynomial[] matrix) {
        GF2Polynomial[] a = new GF2Polynomial[matrix.length];
        GF2Polynomial[] inv = new GF2Polynomial[matrix.length];
        for (int i = 0; i < this.mDegree; i++) {
            try {
                a[i] = new GF2Polynomial(matrix[i]);
                inv[i] = new GF2Polynomial(this.mDegree);
                inv[i].setBit((this.mDegree - 1) - i);
            } catch (RuntimeException BDNEExc) {
                BDNEExc.printStackTrace();
            }
        }
        for (int i2 = 0; i2 < this.mDegree - 1; i2++) {
            int j = i2;
            while (j < this.mDegree && !a[j].testBit((this.mDegree - 1) - i2)) {
                j++;
            }
            if (j >= this.mDegree) {
                throw new RuntimeException("GF2nField.invertMatrix: Matrix cannot be inverted!");
            }
            if (i2 != j) {
                GF2Polynomial dummy = a[i2];
                a[i2] = a[j];
                a[j] = dummy;
                GF2Polynomial dummy2 = inv[i2];
                inv[i2] = inv[j];
                inv[j] = dummy2;
            }
            for (int j2 = i2 + 1; j2 < this.mDegree; j2++) {
                if (a[j2].testBit((this.mDegree - 1) - i2)) {
                    a[j2].addToThis(a[i2]);
                    inv[j2].addToThis(inv[i2]);
                }
            }
        }
        for (int i3 = this.mDegree - 1; i3 > 0; i3--) {
            for (int j3 = i3 - 1; j3 >= 0; j3--) {
                if (a[j3].testBit((this.mDegree - 1) - i3)) {
                    a[j3].addToThis(a[i3]);
                    inv[j3].addToThis(inv[i3]);
                }
            }
        }
        return inv;
    }

    public final GF2nElement convert(GF2nElement elem, GF2nField basis) throws RuntimeException {
        if (basis == this) {
            return (GF2nElement) elem.clone();
        }
        if (this.fieldPolynomial.equals(basis.fieldPolynomial)) {
            return (GF2nElement) elem.clone();
        }
        if (this.mDegree != basis.mDegree) {
            throw new RuntimeException("GF2nField.convert: B1 has a different degree and thus cannot be coverted to!");
        }
        int i = this.fields.indexOf(basis);
        if (i == -1) {
            computeCOBMatrix(basis);
            i = this.fields.indexOf(basis);
        }
        GF2Polynomial[] COBMatrix = (GF2Polynomial[]) this.matrices.elementAt(i);
        GF2nElement elemCopy = (GF2nElement) elem.clone();
        if (elemCopy instanceof GF2nONBElement) {
            ((GF2nONBElement) elemCopy).reverseOrder();
        }
        GF2Polynomial bs = new GF2Polynomial(this.mDegree, elemCopy.toFlexiBigInt());
        bs.expandN(this.mDegree);
        GF2Polynomial result = new GF2Polynomial(this.mDegree);
        for (int i2 = 0; i2 < this.mDegree; i2++) {
            if (bs.vectorMult(COBMatrix[i2])) {
                result.setBit((this.mDegree - 1) - i2);
            }
        }
        if (basis instanceof GF2nPolynomialField) {
            return new GF2nPolynomialElement((GF2nPolynomialField) basis, result);
        }
        if (basis instanceof GF2nONBField) {
            GF2nONBElement res = new GF2nONBElement((GF2nONBField) basis, result.toFlexiBigInt());
            res.reverseOrder();
            return res;
        }
        throw new RuntimeException("GF2nField.convert: B1 must be an instance of GF2nPolynomialField or GF2nONBField!");
    }
}
