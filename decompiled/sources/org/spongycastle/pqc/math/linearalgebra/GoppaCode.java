package org.spongycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/GoppaCode.class */
public final class GoppaCode {
    private GoppaCode() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/GoppaCode$MaMaPe.class */
    public static class MaMaPe {
        private GF2Matrix s;
        private GF2Matrix h;
        private Permutation p;

        public MaMaPe(GF2Matrix s, GF2Matrix h, Permutation p) {
            this.s = s;
            this.h = h;
            this.p = p;
        }

        public GF2Matrix getFirstMatrix() {
            return this.s;
        }

        public GF2Matrix getSecondMatrix() {
            return this.h;
        }

        public Permutation getPermutation() {
            return this.p;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/GoppaCode$MatrixSet.class */
    public static class MatrixSet {
        private GF2Matrix g;
        private int[] setJ;

        public MatrixSet(GF2Matrix g, int[] setJ) {
            this.g = g;
            this.setJ = setJ;
        }

        public GF2Matrix getG() {
            return this.g;
        }

        public int[] getSetJ() {
            return this.setJ;
        }
    }

    public static GF2Matrix createCanonicalCheckMatrix(GF2mField field, PolynomialGF2mSmallM gp) {
        int m = field.getDegree();
        int n = 1 << m;
        int t = gp.getDegree();
        int[][] hArray = new int[t][n];
        int[][] yz = new int[t][n];
        for (int j = 0; j < n; j++) {
            yz[0][j] = field.inverse(gp.evaluateAt(j));
        }
        for (int i = 1; i < t; i++) {
            for (int j2 = 0; j2 < n; j2++) {
                yz[i][j2] = field.mult(yz[i - 1][j2], j2);
            }
        }
        for (int i2 = 0; i2 < t; i2++) {
            for (int j3 = 0; j3 < n; j3++) {
                for (int k = 0; k <= i2; k++) {
                    hArray[i2][j3] = field.add(hArray[i2][j3], field.mult(yz[k][j3], gp.getCoefficient((t + k) - i2)));
                }
            }
        }
        int[][] result = new int[t * m][(n + 31) >>> 5];
        for (int j4 = 0; j4 < n; j4++) {
            int q = j4 >>> 5;
            int r = 1 << (j4 & 31);
            for (int i3 = 0; i3 < t; i3++) {
                int e = hArray[i3][j4];
                for (int u = 0; u < m; u++) {
                    int b = (e >>> u) & 1;
                    if (b != 0) {
                        int ind = (((i3 + 1) * m) - u) - 1;
                        int[] iArr = result[ind];
                        iArr[q] = iArr[q] ^ r;
                    }
                }
            }
        }
        return new GF2Matrix(n, result);
    }

    public static MaMaPe computeSystematicForm(GF2Matrix h, SecureRandom sr) {
        Permutation p;
        GF2Matrix hp;
        GF2Matrix sInv;
        boolean found;
        int n = h.getNumColumns();
        GF2Matrix s = null;
        do {
            p = new Permutation(n, sr);
            hp = (GF2Matrix) h.rightMultiply(p);
            sInv = hp.getLeftSubMatrix();
            try {
                found = true;
                s = (GF2Matrix) sInv.computeInverse();
            } catch (ArithmeticException e) {
                found = false;
            }
        } while (!found);
        GF2Matrix shp = (GF2Matrix) s.rightMultiply(hp);
        GF2Matrix m = shp.getRightSubMatrix();
        return new MaMaPe(sInv, m, p);
    }

    public static GF2Vector syndromeDecode(GF2Vector syndVec, GF2mField field, PolynomialGF2mSmallM gp, PolynomialGF2mSmallM[] sqRootMatrix) {
        int n = 1 << field.getDegree();
        GF2Vector errors = new GF2Vector(n);
        if (!syndVec.isZero()) {
            PolynomialGF2mSmallM syndrome = new PolynomialGF2mSmallM(syndVec.toExtensionFieldVector(field));
            PolynomialGF2mSmallM t = syndrome.modInverse(gp);
            PolynomialGF2mSmallM tau = t.addMonomial(1);
            PolynomialGF2mSmallM[] ab = tau.modSquareRootMatrix(sqRootMatrix).modPolynomialToFracton(gp);
            PolynomialGF2mSmallM a2 = ab[0].multiply(ab[0]);
            PolynomialGF2mSmallM b2 = ab[1].multiply(ab[1]);
            PolynomialGF2mSmallM xb2 = b2.multWithMonomial(1);
            PolynomialGF2mSmallM a2plusXb2 = a2.add(xb2);
            int headCoeff = a2plusXb2.getHeadCoefficient();
            int invHeadCoeff = field.inverse(headCoeff);
            PolynomialGF2mSmallM elp = a2plusXb2.multWithElement(invHeadCoeff);
            for (int i = 0; i < n; i++) {
                int z = elp.evaluateAt(i);
                if (z == 0) {
                    errors.setBit(i);
                }
            }
        }
        return errors;
    }
}
