package org.spongycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/GF2nPolynomialField.class */
public class GF2nPolynomialField extends GF2nField {
    GF2Polynomial[] squaringMatrix;
    private boolean isTrinomial;
    private boolean isPentanomial;
    private int tc;
    private int[] pc;

    public GF2nPolynomialField(int deg, SecureRandom random) {
        super(random);
        this.isTrinomial = false;
        this.isPentanomial = false;
        this.pc = new int[3];
        if (deg < 3) {
            throw new IllegalArgumentException("k must be at least 3");
        }
        this.mDegree = deg;
        computeFieldPolynomial();
        computeSquaringMatrix();
        this.fields = new java.util.Vector();
        this.matrices = new java.util.Vector();
    }

    public GF2nPolynomialField(int deg, SecureRandom random, boolean file) {
        super(random);
        this.isTrinomial = false;
        this.isPentanomial = false;
        this.pc = new int[3];
        if (deg < 3) {
            throw new IllegalArgumentException("k must be at least 3");
        }
        this.mDegree = deg;
        if (file) {
            computeFieldPolynomial();
        } else {
            computeFieldPolynomial2();
        }
        computeSquaringMatrix();
        this.fields = new java.util.Vector();
        this.matrices = new java.util.Vector();
    }

    public GF2nPolynomialField(int deg, SecureRandom random, GF2Polynomial polynomial) throws RuntimeException {
        super(random);
        this.isTrinomial = false;
        this.isPentanomial = false;
        this.pc = new int[3];
        if (deg < 3) {
            throw new IllegalArgumentException("degree must be at least 3");
        }
        if (polynomial.getLength() != deg + 1) {
            throw new RuntimeException();
        }
        if (!polynomial.isIrreducible()) {
            throw new RuntimeException();
        }
        this.mDegree = deg;
        this.fieldPolynomial = polynomial;
        computeSquaringMatrix();
        int k = 2;
        for (int j = 1; j < this.fieldPolynomial.getLength() - 1; j++) {
            if (this.fieldPolynomial.testBit(j)) {
                k++;
                if (k == 3) {
                    this.tc = j;
                }
                if (k <= 5) {
                    this.pc[k - 3] = j;
                }
            }
        }
        if (k == 3) {
            this.isTrinomial = true;
        }
        if (k == 5) {
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
        if (!this.isTrinomial) {
            throw new RuntimeException();
        }
        return this.tc;
    }

    public int[] getPc() throws RuntimeException {
        if (!this.isPentanomial) {
            throw new RuntimeException();
        }
        int[] result = new int[3];
        System.arraycopy(this.pc, 0, result, 0, 3);
        return result;
    }

    public GF2Polynomial getSquaringVector(int i) {
        return new GF2Polynomial(this.squaringMatrix[i]);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.GF2nField
    protected GF2nElement getRandomRoot(GF2Polynomial polynomial) {
        GF2nPolynomial h;
        int hDegree;
        int gDegree;
        GF2nPolynomial gF2nPolynomial;
        GF2nPolynomial g = new GF2nPolynomial(polynomial, this);
        int gDegree2 = g.getDegree();
        while (gDegree2 > 1) {
            while (true) {
                GF2nElement u = new GF2nPolynomialElement(this, this.random);
                GF2nPolynomial ut = new GF2nPolynomial(2, GF2nPolynomialElement.ZERO(this));
                ut.set(1, u);
                GF2nPolynomial c = new GF2nPolynomial(ut);
                for (int i = 1; i <= this.mDegree - 1; i++) {
                    c = c.multiplyAndReduce(c, g).add(ut);
                }
                h = c.gcd(g);
                hDegree = h.getDegree();
                gDegree = g.getDegree();
                if (hDegree != 0 && hDegree != gDegree) {
                    break;
                }
            }
            if ((hDegree << 1) > gDegree) {
                gF2nPolynomial = g.quotient(h);
            } else {
                gF2nPolynomial = new GF2nPolynomial(h);
            }
            g = gF2nPolynomial;
            gDegree2 = g.getDegree();
        }
        return g.at(0);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.GF2nField
    protected void computeCOBMatrix(GF2nField B1) {
        GF2nElement u;
        GF2nElement[] gamma;
        if (this.mDegree != B1.mDegree) {
            throw new IllegalArgumentException("GF2nPolynomialField.computeCOBMatrix: B1 has a different degree and thus cannot be coverted to!");
        }
        if (B1 instanceof GF2nONBField) {
            B1.computeCOBMatrix(this);
            return;
        }
        GF2Polynomial[] COBMatrix = new GF2Polynomial[this.mDegree];
        for (int i = 0; i < this.mDegree; i++) {
            COBMatrix[i] = new GF2Polynomial(this.mDegree);
        }
        do {
            u = B1.getRandomRoot(this.fieldPolynomial);
        } while (u.isZero());
        if (u instanceof GF2nONBElement) {
            gamma = new GF2nONBElement[this.mDegree];
            gamma[this.mDegree - 1] = GF2nONBElement.ONE((GF2nONBField) B1);
        } else {
            gamma = new GF2nPolynomialElement[this.mDegree];
            gamma[this.mDegree - 1] = GF2nPolynomialElement.ONE((GF2nPolynomialField) B1);
        }
        gamma[this.mDegree - 2] = u;
        for (int i2 = this.mDegree - 3; i2 >= 0; i2--) {
            gamma[i2] = (GF2nElement) gamma[i2 + 1].multiply(u);
        }
        if (B1 instanceof GF2nONBField) {
            for (int i3 = 0; i3 < this.mDegree; i3++) {
                for (int j = 0; j < this.mDegree; j++) {
                    if (gamma[i3].testBit((this.mDegree - j) - 1)) {
                        COBMatrix[(this.mDegree - j) - 1].setBit((this.mDegree - i3) - 1);
                    }
                }
            }
        } else {
            for (int i4 = 0; i4 < this.mDegree; i4++) {
                for (int j2 = 0; j2 < this.mDegree; j2++) {
                    if (gamma[i4].testBit(j2)) {
                        COBMatrix[(this.mDegree - j2) - 1].setBit((this.mDegree - i4) - 1);
                    }
                }
            }
        }
        this.fields.addElement(B1);
        this.matrices.addElement(COBMatrix);
        B1.fields.addElement(this);
        B1.matrices.addElement(invertMatrix(COBMatrix));
    }

    private void computeSquaringMatrix() {
        GF2Polynomial[] d = new GF2Polynomial[this.mDegree - 1];
        this.squaringMatrix = new GF2Polynomial[this.mDegree];
        for (int i = 0; i < this.squaringMatrix.length; i++) {
            this.squaringMatrix[i] = new GF2Polynomial(this.mDegree, "ZERO");
        }
        for (int i2 = 0; i2 < this.mDegree - 1; i2++) {
            d[i2] = new GF2Polynomial(1, "ONE").shiftLeft(this.mDegree + i2).remainder(this.fieldPolynomial);
        }
        for (int i3 = 1; i3 <= Math.abs(this.mDegree >> 1); i3++) {
            for (int j = 1; j <= this.mDegree; j++) {
                if (d[this.mDegree - (i3 << 1)].testBit(this.mDegree - j)) {
                    this.squaringMatrix[j - 1].setBit(this.mDegree - i3);
                }
            }
        }
        for (int i4 = Math.abs(this.mDegree >> 1) + 1; i4 <= this.mDegree; i4++) {
            this.squaringMatrix[((i4 << 1) - this.mDegree) - 1].setBit(this.mDegree - i4);
        }
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.GF2nField
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
        boolean done = false;
        int l = 0;
        this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
        this.fieldPolynomial.setBit(0);
        this.fieldPolynomial.setBit(this.mDegree);
        for (int i = 1; i < this.mDegree && !done; i++) {
            this.fieldPolynomial.setBit(i);
            boolean done2 = this.fieldPolynomial.isIrreducible();
            l++;
            if (done2) {
                this.isTrinomial = true;
                this.tc = i;
                return done2;
            }
            this.fieldPolynomial.resetBit(i);
            done = this.fieldPolynomial.isIrreducible();
        }
        return done;
    }

    private boolean testPentanomials() {
        boolean done = false;
        int l = 0;
        this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
        this.fieldPolynomial.setBit(0);
        this.fieldPolynomial.setBit(this.mDegree);
        for (int i = 1; i <= this.mDegree - 3 && !done; i++) {
            this.fieldPolynomial.setBit(i);
            for (int j = i + 1; j <= this.mDegree - 2 && !done; j++) {
                this.fieldPolynomial.setBit(j);
                for (int k = j + 1; k <= this.mDegree - 1 && !done; k++) {
                    this.fieldPolynomial.setBit(k);
                    if (((this.mDegree & 1) != 0) | ((i & 1) != 0) | ((j & 1) != 0) | ((k & 1) != 0)) {
                        done = this.fieldPolynomial.isIrreducible();
                        l++;
                        if (done) {
                            this.isPentanomial = true;
                            this.pc[0] = i;
                            this.pc[1] = j;
                            this.pc[2] = k;
                            return done;
                        }
                    }
                    this.fieldPolynomial.resetBit(k);
                }
                this.fieldPolynomial.resetBit(j);
            }
            this.fieldPolynomial.resetBit(i);
        }
        return done;
    }

    private boolean testRandom() {
        this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
        int l = 0;
        while (0 == 0) {
            l++;
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
