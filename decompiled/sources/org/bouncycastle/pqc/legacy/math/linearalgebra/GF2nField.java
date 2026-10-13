package org.bouncycastle.pqc.legacy.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/GF2nField.class */
public abstract class GF2nField {
    protected final SecureRandom random;
    protected int mDegree;
    protected GF2Polynomial fieldPolynomial;
    protected java.util.Vector fields;
    protected java.util.Vector matrices;

    protected GF2nField(SecureRandom secureRandom) {
        this.random = secureRandom;
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

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GF2nField)) {
            return false;
        }
        GF2nField gF2nField = (GF2nField) obj;
        if (gF2nField.mDegree != this.mDegree || !this.fieldPolynomial.equals(gF2nField.fieldPolynomial)) {
            return false;
        }
        if (!(this instanceof GF2nPolynomialField) || (gF2nField instanceof GF2nPolynomialField)) {
            return !(this instanceof GF2nONBField) || (gF2nField instanceof GF2nONBField);
        }
        return false;
    }

    public int hashCode() {
        return this.mDegree + this.fieldPolynomial.hashCode();
    }

    protected abstract GF2nElement getRandomRoot(GF2Polynomial gF2Polynomial);

    protected abstract void computeCOBMatrix(GF2nField gF2nField);

    protected abstract void computeFieldPolynomial();

    protected final GF2Polynomial[] invertMatrix(GF2Polynomial[] gF2PolynomialArr) {
        GF2Polynomial[] gF2PolynomialArr2 = new GF2Polynomial[gF2PolynomialArr.length];
        GF2Polynomial[] gF2PolynomialArr3 = new GF2Polynomial[gF2PolynomialArr.length];
        for (int i = 0; i < this.mDegree; i++) {
            gF2PolynomialArr2[i] = new GF2Polynomial(gF2PolynomialArr[i]);
            gF2PolynomialArr3[i] = new GF2Polynomial(this.mDegree);
            gF2PolynomialArr3[i].setBit((this.mDegree - 1) - i);
        }
        for (int i2 = 0; i2 < this.mDegree - 1; i2++) {
            int i3 = i2;
            while (i3 < this.mDegree && !gF2PolynomialArr2[i3].testBit((this.mDegree - 1) - i2)) {
                i3++;
            }
            if (i3 >= this.mDegree) {
                throw new RuntimeException("GF2nField.invertMatrix: Matrix cannot be inverted!");
            }
            if (i2 != i3) {
                GF2Polynomial gF2Polynomial = gF2PolynomialArr2[i2];
                gF2PolynomialArr2[i2] = gF2PolynomialArr2[i3];
                gF2PolynomialArr2[i3] = gF2Polynomial;
                GF2Polynomial gF2Polynomial2 = gF2PolynomialArr3[i2];
                gF2PolynomialArr3[i2] = gF2PolynomialArr3[i3];
                gF2PolynomialArr3[i3] = gF2Polynomial2;
            }
            for (int i4 = i2 + 1; i4 < this.mDegree; i4++) {
                if (gF2PolynomialArr2[i4].testBit((this.mDegree - 1) - i2)) {
                    gF2PolynomialArr2[i4].addToThis(gF2PolynomialArr2[i2]);
                    gF2PolynomialArr3[i4].addToThis(gF2PolynomialArr3[i2]);
                }
            }
        }
        for (int i5 = this.mDegree - 1; i5 > 0; i5--) {
            for (int i6 = i5 - 1; i6 >= 0; i6--) {
                if (gF2PolynomialArr2[i6].testBit((this.mDegree - 1) - i5)) {
                    gF2PolynomialArr2[i6].addToThis(gF2PolynomialArr2[i5]);
                    gF2PolynomialArr3[i6].addToThis(gF2PolynomialArr3[i5]);
                }
            }
        }
        return gF2PolynomialArr3;
    }

    public final GF2nElement convert(GF2nElement gF2nElement, GF2nField gF2nField) throws RuntimeException {
        if (gF2nField != this && !this.fieldPolynomial.equals(gF2nField.fieldPolynomial)) {
            if (this.mDegree != gF2nField.mDegree) {
                throw new RuntimeException("GF2nField.convert: B1 has a different degree and thus cannot be coverted to!");
            }
            int iIndexOf = this.fields.indexOf(gF2nField);
            if (iIndexOf == -1) {
                computeCOBMatrix(gF2nField);
                iIndexOf = this.fields.indexOf(gF2nField);
            }
            GF2Polynomial[] gF2PolynomialArr = (GF2Polynomial[]) this.matrices.elementAt(iIndexOf);
            GF2nElement gF2nElement2 = (GF2nElement) gF2nElement.clone();
            if (gF2nElement2 instanceof GF2nONBElement) {
                ((GF2nONBElement) gF2nElement2).reverseOrder();
            }
            GF2Polynomial gF2Polynomial = new GF2Polynomial(this.mDegree, gF2nElement2.toFlexiBigInt());
            gF2Polynomial.expandN(this.mDegree);
            GF2Polynomial gF2Polynomial2 = new GF2Polynomial(this.mDegree);
            for (int i = 0; i < this.mDegree; i++) {
                if (gF2Polynomial.vectorMult(gF2PolynomialArr[i])) {
                    gF2Polynomial2.setBit((this.mDegree - 1) - i);
                }
            }
            if (gF2nField instanceof GF2nPolynomialField) {
                return new GF2nPolynomialElement((GF2nPolynomialField) gF2nField, gF2Polynomial2);
            }
            if (!(gF2nField instanceof GF2nONBField)) {
                throw new RuntimeException("GF2nField.convert: B1 must be an instance of GF2nPolynomialField or GF2nONBField!");
            }
            GF2nONBElement gF2nONBElement = new GF2nONBElement((GF2nONBField) gF2nField, gF2Polynomial2.toFlexiBigInt());
            gF2nONBElement.reverseOrder();
            return gF2nONBElement;
        }
        return (GF2nElement) gF2nElement.clone();
    }
}
