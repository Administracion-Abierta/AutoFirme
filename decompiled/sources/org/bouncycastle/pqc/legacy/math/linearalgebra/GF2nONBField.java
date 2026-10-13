package org.bouncycastle.pqc.legacy.math.linearalgebra;

import java.security.SecureRandom;
import java.util.Random;
import org.apache.commons.codec.language.bm.Rule;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/GF2nONBField.class */
public class GF2nONBField extends GF2nField {
    private static final int MAXLONG = 64;
    private int mLength;
    private int mBit;
    private int mType;
    int[][] mMult;

    public GF2nONBField(int i, SecureRandom secureRandom) throws RuntimeException {
        super(secureRandom);
        if (i < 3) {
            throw new IllegalArgumentException("k must be at least 3");
        }
        this.mDegree = i;
        this.mLength = this.mDegree / 64;
        this.mBit = this.mDegree & 63;
        if (this.mBit == 0) {
            this.mBit = 64;
        } else {
            this.mLength++;
        }
        computeType();
        if (this.mType >= 3) {
            throw new RuntimeException("\nThe type of this field is " + this.mType);
        }
        this.mMult = new int[this.mDegree][2];
        for (int i2 = 0; i2 < this.mDegree; i2++) {
            this.mMult[i2][0] = -1;
            this.mMult[i2][1] = -1;
        }
        computeMultMatrix();
        computeFieldPolynomial();
        this.fields = new java.util.Vector();
        this.matrices = new java.util.Vector();
    }

    int getONBLength() {
        return this.mLength;
    }

    int getONBBit() {
        return this.mBit;
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.GF2nField
    protected GF2nElement getRandomRoot(GF2Polynomial gF2Polynomial) {
        GF2nPolynomial gF2nPolynomialGcd;
        int degree;
        int degree2;
        GF2nPolynomial gF2nPolynomial = new GF2nPolynomial(gF2Polynomial, this);
        int degree3 = gF2nPolynomial.getDegree();
        while (degree3 > 1) {
            while (true) {
                GF2nONBElement gF2nONBElement = new GF2nONBElement(this, this.random);
                GF2nPolynomial gF2nPolynomial2 = new GF2nPolynomial(2, GF2nONBElement.ZERO(this));
                gF2nPolynomial2.set(1, gF2nONBElement);
                GF2nPolynomial gF2nPolynomial3 = new GF2nPolynomial(gF2nPolynomial2);
                for (int i = 1; i <= this.mDegree - 1; i++) {
                    gF2nPolynomial3 = gF2nPolynomial3.multiplyAndReduce(gF2nPolynomial3, gF2nPolynomial).add(gF2nPolynomial2);
                }
                gF2nPolynomialGcd = gF2nPolynomial3.gcd(gF2nPolynomial);
                degree = gF2nPolynomialGcd.getDegree();
                degree2 = gF2nPolynomial.getDegree();
                if (degree != 0 && degree != degree2) {
                    break;
                }
            }
            gF2nPolynomial = (degree << 1) > degree2 ? gF2nPolynomial.quotient(gF2nPolynomialGcd) : new GF2nPolynomial(gF2nPolynomialGcd);
            degree3 = gF2nPolynomial.getDegree();
        }
        return gF2nPolynomial.at(0);
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.GF2nField
    protected void computeCOBMatrix(GF2nField gF2nField) {
        GF2nElement randomRoot;
        if (this.mDegree != gF2nField.mDegree) {
            throw new IllegalArgumentException("GF2nField.computeCOBMatrix: B1 has a different degree and thus cannot be coverted to!");
        }
        GF2Polynomial[] gF2PolynomialArr = new GF2Polynomial[this.mDegree];
        for (int i = 0; i < this.mDegree; i++) {
            gF2PolynomialArr[i] = new GF2Polynomial(this.mDegree);
        }
        do {
            randomRoot = gF2nField.getRandomRoot(this.fieldPolynomial);
        } while (randomRoot.isZero());
        GF2nElement[] gF2nElementArr = new GF2nPolynomialElement[this.mDegree];
        gF2nElementArr[0] = (GF2nElement) randomRoot.clone();
        for (int i2 = 1; i2 < this.mDegree; i2++) {
            gF2nElementArr[i2] = gF2nElementArr[i2 - 1].square();
        }
        for (int i3 = 0; i3 < this.mDegree; i3++) {
            for (int i4 = 0; i4 < this.mDegree; i4++) {
                if (gF2nElementArr[i3].testBit(i4)) {
                    gF2PolynomialArr[(this.mDegree - i4) - 1].setBit((this.mDegree - i3) - 1);
                }
            }
        }
        this.fields.addElement(gF2nField);
        this.matrices.addElement(gF2PolynomialArr);
        gF2nField.fields.addElement(this);
        gF2nField.matrices.addElement(invertMatrix(gF2PolynomialArr));
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.GF2nField
    protected void computeFieldPolynomial() {
        if (this.mType == 1) {
            this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1, Rule.ALL);
            return;
        }
        if (this.mType == 2) {
            GF2Polynomial gF2Polynomial = new GF2Polynomial(this.mDegree + 1, "ONE");
            GF2Polynomial gF2Polynomial2 = new GF2Polynomial(this.mDegree + 1, "X");
            gF2Polynomial2.addToThis(gF2Polynomial);
            for (int i = 1; i < this.mDegree; i++) {
                GF2Polynomial gF2Polynomial3 = gF2Polynomial;
                gF2Polynomial = gF2Polynomial2;
                gF2Polynomial2 = gF2Polynomial.shiftLeft();
                gF2Polynomial2.addToThis(gF2Polynomial3);
            }
            this.fieldPolynomial = gF2Polynomial2;
        }
    }

    int[][] invMatrix(int[][] iArr) {
        int[][] iArr2 = new int[this.mDegree][this.mDegree];
        int[][] iArr3 = new int[this.mDegree][this.mDegree];
        for (int i = 0; i < this.mDegree; i++) {
            iArr3[i][i] = 1;
        }
        for (int i2 = 0; i2 < this.mDegree; i2++) {
            for (int i3 = i2; i3 < this.mDegree; i3++) {
                iArr[(this.mDegree - 1) - i2][i3] = iArr[i2][i2];
            }
        }
        return null;
    }

    private void computeType() throws RuntimeException {
        if ((this.mDegree & 7) == 0) {
            throw new RuntimeException("The extension degree is divisible by 8!");
        }
        this.mType = 1;
        int iGcd = 0;
        while (iGcd != 1) {
            int i = (this.mType * this.mDegree) + 1;
            if (IntegerFunctions.isPrime(i)) {
                iGcd = IntegerFunctions.gcd((this.mType * this.mDegree) / IntegerFunctions.order(2, i), this.mDegree);
            }
            this.mType++;
        }
        this.mType--;
        if (this.mType == 1) {
            int i2 = (this.mDegree << 1) + 1;
            if (IntegerFunctions.isPrime(i2)) {
                if (IntegerFunctions.gcd((this.mDegree << 1) / IntegerFunctions.order(2, i2), this.mDegree) == 1) {
                    this.mType++;
                }
            }
        }
    }

    private void computeMultMatrix() {
        if ((this.mType & 7) == 0) {
            throw new RuntimeException("bisher nur fuer Gausssche Normalbasen implementiert");
        }
        int i = (this.mType * this.mDegree) + 1;
        int[] iArr = new int[i];
        int iElementOfOrder = this.mType == 1 ? 1 : this.mType == 2 ? i - 1 : elementOfOrder(this.mType, i);
        int i2 = 1;
        for (int i3 = 0; i3 < this.mType; i3++) {
            int i4 = i2;
            for (int i5 = 0; i5 < this.mDegree; i5++) {
                iArr[i4] = i5;
                i4 = (i4 << 1) % i;
                if (i4 < 0) {
                    i4 += i;
                }
            }
            i2 = (iElementOfOrder * i2) % i;
            if (i2 < 0) {
                i2 += i;
            }
        }
        if (this.mType != 1) {
            if (this.mType != 2) {
                throw new RuntimeException("only type 1 or type 2 implemented");
            }
            for (int i6 = 1; i6 < i - 1; i6++) {
                if (this.mMult[iArr[i6 + 1]][0] == -1) {
                    this.mMult[iArr[i6 + 1]][0] = iArr[i - i6];
                } else {
                    this.mMult[iArr[i6 + 1]][1] = iArr[i - i6];
                }
            }
            return;
        }
        for (int i7 = 1; i7 < i - 1; i7++) {
            if (this.mMult[iArr[i7 + 1]][0] == -1) {
                this.mMult[iArr[i7 + 1]][0] = iArr[i - i7];
            } else {
                this.mMult[iArr[i7 + 1]][1] = iArr[i - i7];
            }
        }
        int i8 = this.mDegree >> 1;
        for (int i9 = 1; i9 <= i8; i9++) {
            if (this.mMult[i9 - 1][0] == -1) {
                this.mMult[i9 - 1][0] = (i8 + i9) - 1;
            } else {
                this.mMult[i9 - 1][1] = (i8 + i9) - 1;
            }
            if (this.mMult[(i8 + i9) - 1][0] == -1) {
                this.mMult[(i8 + i9) - 1][0] = i9 - 1;
            } else {
                this.mMult[(i8 + i9) - 1][1] = i9 - 1;
            }
        }
    }

    private int elementOfOrder(int i, int i2) {
        int i3;
        Random random = new Random();
        int iNextInt = 0;
        while (iNextInt == 0) {
            iNextInt = random.nextInt() % (i2 - 1);
            if (iNextInt < 0) {
                iNextInt += i2 - 1;
            }
        }
        int iOrder = IntegerFunctions.order(iNextInt, i2);
        while (true) {
            i3 = iOrder;
            if (i3 % i == 0 && i3 != 0) {
                break;
            }
            while (iNextInt == 0) {
                iNextInt = random.nextInt() % (i2 - 1);
                if (iNextInt < 0) {
                    iNextInt += i2 - 1;
                }
            }
            iOrder = IntegerFunctions.order(iNextInt, i2);
        }
        int i4 = iNextInt;
        for (int i5 = 2; i5 <= i / i3; i5++) {
            i4 *= iNextInt;
        }
        return i4;
    }
}
