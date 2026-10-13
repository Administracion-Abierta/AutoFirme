package org.spongycastle.pqc.math.linearalgebra;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/math/linearalgebra/PolynomialRingGF2m.class */
public class PolynomialRingGF2m {
    private GF2mField field;
    private PolynomialGF2mSmallM p;
    protected PolynomialGF2mSmallM[] sqMatrix;
    protected PolynomialGF2mSmallM[] sqRootMatrix;

    public PolynomialRingGF2m(GF2mField field, PolynomialGF2mSmallM p) {
        this.field = field;
        this.p = p;
        computeSquaringMatrix();
        computeSquareRootMatrix();
    }

    public PolynomialGF2mSmallM[] getSquaringMatrix() {
        return this.sqMatrix;
    }

    public PolynomialGF2mSmallM[] getSquareRootMatrix() {
        return this.sqRootMatrix;
    }

    private void computeSquaringMatrix() {
        int numColumns = this.p.getDegree();
        this.sqMatrix = new PolynomialGF2mSmallM[numColumns];
        for (int i = 0; i < (numColumns >> 1); i++) {
            int[] monomCoeffs = new int[(i << 1) + 1];
            monomCoeffs[i << 1] = 1;
            this.sqMatrix[i] = new PolynomialGF2mSmallM(this.field, monomCoeffs);
        }
        for (int i2 = numColumns >> 1; i2 < numColumns; i2++) {
            int[] monomCoeffs2 = new int[(i2 << 1) + 1];
            monomCoeffs2[i2 << 1] = 1;
            PolynomialGF2mSmallM monomial = new PolynomialGF2mSmallM(this.field, monomCoeffs2);
            this.sqMatrix[i2] = monomial.mod(this.p);
        }
    }

    private void computeSquareRootMatrix() {
        int coef;
        int numColumns = this.p.getDegree();
        PolynomialGF2mSmallM[] tmpMatrix = new PolynomialGF2mSmallM[numColumns];
        for (int i = numColumns - 1; i >= 0; i--) {
            tmpMatrix[i] = new PolynomialGF2mSmallM(this.sqMatrix[i]);
        }
        this.sqRootMatrix = new PolynomialGF2mSmallM[numColumns];
        for (int i2 = numColumns - 1; i2 >= 0; i2--) {
            this.sqRootMatrix[i2] = new PolynomialGF2mSmallM(this.field, i2);
        }
        for (int i3 = 0; i3 < numColumns; i3++) {
            if (tmpMatrix[i3].getCoefficient(i3) == 0) {
                boolean foundNonZero = false;
                int j = i3 + 1;
                while (j < numColumns) {
                    if (tmpMatrix[j].getCoefficient(i3) != 0) {
                        foundNonZero = true;
                        swapColumns(tmpMatrix, i3, j);
                        swapColumns(this.sqRootMatrix, i3, j);
                        j = numColumns;
                    }
                    j++;
                }
                if (!foundNonZero) {
                    throw new ArithmeticException("Squaring matrix is not invertible.");
                }
            }
            int invCoef = this.field.inverse(tmpMatrix[i3].getCoefficient(i3));
            tmpMatrix[i3].multThisWithElement(invCoef);
            this.sqRootMatrix[i3].multThisWithElement(invCoef);
            for (int j2 = 0; j2 < numColumns; j2++) {
                if (j2 != i3 && (coef = tmpMatrix[j2].getCoefficient(i3)) != 0) {
                    PolynomialGF2mSmallM tmpSqColumn = tmpMatrix[i3].multWithElement(coef);
                    PolynomialGF2mSmallM tmpInvColumn = this.sqRootMatrix[i3].multWithElement(coef);
                    tmpMatrix[j2].addToThis(tmpSqColumn);
                    this.sqRootMatrix[j2].addToThis(tmpInvColumn);
                }
            }
        }
    }

    private static void swapColumns(PolynomialGF2mSmallM[] matrix, int first, int second) {
        PolynomialGF2mSmallM tmp = matrix[first];
        matrix[first] = matrix[second];
        matrix[second] = tmp;
    }
}
