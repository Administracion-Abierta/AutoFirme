package org.bouncycastle.pqc.legacy.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/GF2nPolynomialField.class */
public class GF2nPolynomialField extends GF2nField {
    GF2Polynomial[] squaringMatrix;
    private boolean isTrinomial;
    private boolean isPentanomial;
    private int tc;
    private int[] pc;

    public GF2nPolynomialField(int i, SecureRandom secureRandom) {
        super(secureRandom);
        this.isTrinomial = false;
        this.isPentanomial = false;
        this.pc = new int[3];
        if (i < 3) {
            throw new IllegalArgumentException("k must be at least 3");
        }
        this.mDegree = i;
        computeFieldPolynomial();
        computeSquaringMatrix();
        this.fields = new java.util.Vector();
        this.matrices = new java.util.Vector();
    }

    public GF2nPolynomialField(int i, SecureRandom secureRandom, boolean z) {
        super(secureRandom);
        this.isTrinomial = false;
        this.isPentanomial = false;
        this.pc = new int[3];
        if (i < 3) {
            throw new IllegalArgumentException("k must be at least 3");
        }
        this.mDegree = i;
        if (z) {
            computeFieldPolynomial();
        } else {
            computeFieldPolynomial2();
        }
        computeSquaringMatrix();
        this.fields = new java.util.Vector();
        this.matrices = new java.util.Vector();
    }

    public GF2nPolynomialField(int i, SecureRandom secureRandom, GF2Polynomial gF2Polynomial) throws RuntimeException {
        super(secureRandom);
        this.isTrinomial = false;
        this.isPentanomial = false;
        this.pc = new int[3];
        if (i < 3) {
            throw new IllegalArgumentException("degree must be at least 3");
        }
        if (gF2Polynomial.getLength() != i + 1) {
            throw new RuntimeException();
        }
        if (!gF2Polynomial.isIrreducible()) {
            throw new RuntimeException();
        }
        this.mDegree = i;
        this.fieldPolynomial = gF2Polynomial;
        computeSquaringMatrix();
        int i2 = 2;
        for (int i3 = 1; i3 < this.fieldPolynomial.getLength() - 1; i3++) {
            if (this.fieldPolynomial.testBit(i3)) {
                i2++;
                if (i2 == 3) {
                    this.tc = i3;
                }
                if (i2 <= 5) {
                    this.pc[i2 - 3] = i3;
                }
            }
        }
        if (i2 == 3) {
            this.isTrinomial = true;
        }
        if (i2 == 5) {
            this.isPentanomial = true;
        }
        this.fields = new java.util.Vector();
        this.matrices = new java.util.Vector();
    }

    public boolean isTrinomial() {
        return this.isTrinomial;
    }

    public boolean isPentanomial() {
        return this.isPentanomial;
    }

    public int getTc() throws RuntimeException {
        if (this.isTrinomial) {
            return this.tc;
        }
        throw new RuntimeException();
    }

    public int[] getPc() throws RuntimeException {
        if (!this.isPentanomial) {
            throw new RuntimeException();
        }
        int[] iArr = new int[3];
        System.arraycopy(this.pc, 0, iArr, 0, 3);
        return iArr;
    }

    public GF2Polynomial getSquaringVector(int i) {
        return new GF2Polynomial(this.squaringMatrix[i]);
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
                GF2nPolynomialElement gF2nPolynomialElement = new GF2nPolynomialElement(this, this.random);
                GF2nPolynomial gF2nPolynomial2 = new GF2nPolynomial(2, GF2nPolynomialElement.ZERO(this));
                gF2nPolynomial2.set(1, gF2nPolynomialElement);
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
        GF2nElement[] gF2nElementArr;
        if (this.mDegree != gF2nField.mDegree) {
            throw new IllegalArgumentException("GF2nPolynomialField.computeCOBMatrix: B1 has a different degree and thus cannot be coverted to!");
        }
        if (gF2nField instanceof GF2nONBField) {
            gF2nField.computeCOBMatrix(this);
            return;
        }
        GF2Polynomial[] gF2PolynomialArr = new GF2Polynomial[this.mDegree];
        for (int i = 0; i < this.mDegree; i++) {
            gF2PolynomialArr[i] = new GF2Polynomial(this.mDegree);
        }
        do {
            randomRoot = gF2nField.getRandomRoot(this.fieldPolynomial);
        } while (randomRoot.isZero());
        if (randomRoot instanceof GF2nONBElement) {
            gF2nElementArr = new GF2nONBElement[this.mDegree];
            gF2nElementArr[this.mDegree - 1] = GF2nONBElement.ONE((GF2nONBField) gF2nField);
        } else {
            gF2nElementArr = new GF2nPolynomialElement[this.mDegree];
            gF2nElementArr[this.mDegree - 1] = GF2nPolynomialElement.ONE((GF2nPolynomialField) gF2nField);
        }
        gF2nElementArr[this.mDegree - 2] = randomRoot;
        for (int i2 = this.mDegree - 3; i2 >= 0; i2--) {
            gF2nElementArr[i2] = (GF2nElement) gF2nElementArr[i2 + 1].multiply(randomRoot);
        }
        if (gF2nField instanceof GF2nONBField) {
            for (int i3 = 0; i3 < this.mDegree; i3++) {
                for (int i4 = 0; i4 < this.mDegree; i4++) {
                    if (gF2nElementArr[i3].testBit((this.mDegree - i4) - 1)) {
                        gF2PolynomialArr[(this.mDegree - i4) - 1].setBit((this.mDegree - i3) - 1);
                    }
                }
            }
        } else {
            for (int i5 = 0; i5 < this.mDegree; i5++) {
                for (int i6 = 0; i6 < this.mDegree; i6++) {
                    if (gF2nElementArr[i5].testBit(i6)) {
                        gF2PolynomialArr[(this.mDegree - i6) - 1].setBit((this.mDegree - i5) - 1);
                    }
                }
            }
        }
        this.fields.addElement(gF2nField);
        this.matrices.addElement(gF2PolynomialArr);
        gF2nField.fields.addElement(this);
        gF2nField.matrices.addElement(invertMatrix(gF2PolynomialArr));
    }

    private void computeSquaringMatrix() {
        GF2Polynomial[] gF2PolynomialArr = new GF2Polynomial[this.mDegree - 1];
        this.squaringMatrix = new GF2Polynomial[this.mDegree];
        for (int i = 0; i < this.squaringMatrix.length; i++) {
            this.squaringMatrix[i] = new GF2Polynomial(this.mDegree, "ZERO");
        }
        for (int i2 = 0; i2 < this.mDegree - 1; i2++) {
            gF2PolynomialArr[i2] = new GF2Polynomial(1, "ONE").shiftLeft(this.mDegree + i2).remainder(this.fieldPolynomial);
        }
        for (int i3 = 1; i3 <= Math.abs(this.mDegree >> 1); i3++) {
            for (int i4 = 1; i4 <= this.mDegree; i4++) {
                if (gF2PolynomialArr[this.mDegree - (i3 << 1)].testBit(this.mDegree - i4)) {
                    this.squaringMatrix[i4 - 1].setBit(this.mDegree - i3);
                }
            }
        }
        for (int iAbs = Math.abs(this.mDegree >> 1) + 1; iAbs <= this.mDegree; iAbs++) {
            this.squaringMatrix[((iAbs << 1) - this.mDegree) - 1].setBit(this.mDegree - iAbs);
        }
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.GF2nField
    protected void computeFieldPolynomial() {
        if (testTrinomials() || testPentanomials()) {
            return;
        }
        testRandom();
    }

    protected void computeFieldPolynomial2() {
        if (testTrinomials() || testPentanomials()) {
            return;
        }
        testRandom();
    }

    private boolean testTrinomials() {
        boolean zIsIrreducible = false;
        int i = 0;
        this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
        this.fieldPolynomial.setBit(0);
        this.fieldPolynomial.setBit(this.mDegree);
        for (int i2 = 1; i2 < this.mDegree && !zIsIrreducible; i2++) {
            this.fieldPolynomial.setBit(i2);
            boolean zIsIrreducible2 = this.fieldPolynomial.isIrreducible();
            i++;
            if (zIsIrreducible2) {
                this.isTrinomial = true;
                this.tc = i2;
                return zIsIrreducible2;
            }
            this.fieldPolynomial.resetBit(i2);
            zIsIrreducible = this.fieldPolynomial.isIrreducible();
        }
        return zIsIrreducible;
    }

    private boolean testPentanomials() {
        boolean zIsIrreducible = false;
        int i = 0;
        this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
        this.fieldPolynomial.setBit(0);
        this.fieldPolynomial.setBit(this.mDegree);
        for (int i2 = 1; i2 <= this.mDegree - 3 && !zIsIrreducible; i2++) {
            this.fieldPolynomial.setBit(i2);
            for (int i3 = i2 + 1; i3 <= this.mDegree - 2 && !zIsIrreducible; i3++) {
                this.fieldPolynomial.setBit(i3);
                for (int i4 = i3 + 1; i4 <= this.mDegree - 1 && !zIsIrreducible; i4++) {
                    this.fieldPolynomial.setBit(i4);
                    if (((this.mDegree & 1) != 0) | ((i2 & 1) != 0) | ((i3 & 1) != 0) | ((i4 & 1) != 0)) {
                        zIsIrreducible = this.fieldPolynomial.isIrreducible();
                        i++;
                        if (zIsIrreducible) {
                            this.isPentanomial = true;
                            this.pc[0] = i2;
                            this.pc[1] = i3;
                            this.pc[2] = i4;
                            return zIsIrreducible;
                        }
                    }
                    this.fieldPolynomial.resetBit(i4);
                }
                this.fieldPolynomial.resetBit(i3);
            }
            this.fieldPolynomial.resetBit(i2);
        }
        return zIsIrreducible;
    }

    private boolean testRandom() {
        this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
        int i = 0;
        while (0 == 0) {
            i++;
            this.fieldPolynomial.randomize();
            this.fieldPolynomial.setBit(this.mDegree);
            this.fieldPolynomial.setBit(0);
            if (this.fieldPolynomial.isIrreducible()) {
                return true;
            }
        }
        return false;
    }
}
