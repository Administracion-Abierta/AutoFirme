package org.spongycastle.pqc.math.linearalgebra;

import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.security.SecureRandom;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/PolynomialGF2mSmallM.class */
public class PolynomialGF2mSmallM {
    private GF2mField field;
    private int degree;
    private int[] coefficients;
    public static final char RANDOM_IRREDUCIBLE_POLYNOMIAL = 'I';

    public PolynomialGF2mSmallM(GF2mField field) {
        this.field = field;
        this.degree = -1;
        this.coefficients = new int[1];
    }

    public PolynomialGF2mSmallM(GF2mField field, int deg, char typeOfPolynomial, SecureRandom sr) {
        this.field = field;
        switch (typeOfPolynomial) {
            case 'I':
                this.coefficients = createRandomIrreduciblePolynomial(deg, sr);
                computeDegree();
                return;
            default:
                throw new IllegalArgumentException(" Error: type " + typeOfPolynomial + " is not defined for GF2smallmPolynomial");
        }
    }

    private int[] createRandomIrreduciblePolynomial(int deg, SecureRandom sr) {
        int[] resCoeff = new int[deg + 1];
        resCoeff[deg] = 1;
        resCoeff[0] = this.field.getRandomNonZeroElement(sr);
        for (int i = 1; i < deg; i++) {
            resCoeff[i] = this.field.getRandomElement(sr);
        }
        while (!isIrreducible(resCoeff)) {
            int n = RandUtils.nextInt(sr, deg);
            if (n == 0) {
                resCoeff[0] = this.field.getRandomNonZeroElement(sr);
            } else {
                resCoeff[n] = this.field.getRandomElement(sr);
            }
        }
        return resCoeff;
    }

    public PolynomialGF2mSmallM(GF2mField field, int degree) {
        this.field = field;
        this.degree = degree;
        this.coefficients = new int[degree + 1];
        this.coefficients[degree] = 1;
    }

    public PolynomialGF2mSmallM(GF2mField field, int[] coeffs) {
        this.field = field;
        this.coefficients = normalForm(coeffs);
        computeDegree();
    }

    public PolynomialGF2mSmallM(GF2mField field, byte[] enc) {
        this.field = field;
        int d = 8;
        int count = 1;
        while (field.getDegree() > d) {
            count++;
            d += 8;
        }
        if (enc.length % count != 0) {
            throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m");
        }
        this.coefficients = new int[enc.length / count];
        int count2 = 0;
        for (int i = 0; i < this.coefficients.length; i++) {
            for (int j = 0; j < d; j += 8) {
                int[] iArr = this.coefficients;
                int i2 = i;
                int i3 = count2;
                count2++;
                iArr[i2] = iArr[i2] ^ ((enc[i3] & 255) << j);
            }
            if (!this.field.isElementOfThisField(this.coefficients[i])) {
                throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m");
            }
        }
        if (this.coefficients.length != 1 && this.coefficients[this.coefficients.length - 1] == 0) {
            throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m");
        }
        computeDegree();
    }

    public PolynomialGF2mSmallM(PolynomialGF2mSmallM other) {
        this.field = other.field;
        this.degree = other.degree;
        this.coefficients = IntUtils.clone(other.coefficients);
    }

    public PolynomialGF2mSmallM(GF2mVector vect) {
        this(vect.getField(), vect.getIntArrayForm());
    }

    public int getDegree() {
        int d = this.coefficients.length - 1;
        if (this.coefficients[d] == 0) {
            return -1;
        }
        return d;
    }

    public int getHeadCoefficient() {
        if (this.degree == -1) {
            return 0;
        }
        return this.coefficients[this.degree];
    }

    private static int headCoefficient(int[] a) {
        int degree = computeDegree(a);
        if (degree == -1) {
            return 0;
        }
        return a[degree];
    }

    public int getCoefficient(int index) {
        if (index < 0 || index > this.degree) {
            return 0;
        }
        return this.coefficients[index];
    }

    public byte[] getEncoded() {
        int d = 8;
        int count = 1;
        while (this.field.getDegree() > d) {
            count++;
            d += 8;
        }
        byte[] res = new byte[this.coefficients.length * count];
        int count2 = 0;
        for (int i = 0; i < this.coefficients.length; i++) {
            for (int j = 0; j < d; j += 8) {
                int i2 = count2;
                count2++;
                res[i2] = (byte) (this.coefficients[i] >>> j);
            }
        }
        return res;
    }

    public int evaluateAt(int e) {
        int result = this.coefficients[this.degree];
        for (int i = this.degree - 1; i >= 0; i--) {
            result = this.field.mult(result, e) ^ this.coefficients[i];
        }
        return result;
    }

    public PolynomialGF2mSmallM add(PolynomialGF2mSmallM addend) {
        int[] resultCoeff = add(this.coefficients, addend.coefficients);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    public void addToThis(PolynomialGF2mSmallM addend) {
        this.coefficients = add(this.coefficients, addend.coefficients);
        computeDegree();
    }

    private int[] add(int[] a, int[] b) {
        int[] result;
        int[] addend;
        if (a.length < b.length) {
            result = new int[b.length];
            System.arraycopy(b, 0, result, 0, b.length);
            addend = a;
        } else {
            result = new int[a.length];
            System.arraycopy(a, 0, result, 0, a.length);
            addend = b;
        }
        for (int i = addend.length - 1; i >= 0; i--) {
            result[i] = this.field.add(result[i], addend[i]);
        }
        return result;
    }

    public PolynomialGF2mSmallM addMonomial(int degree) {
        int[] monomial = new int[degree + 1];
        monomial[degree] = 1;
        int[] resultCoeff = add(this.coefficients, monomial);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    public PolynomialGF2mSmallM multWithElement(int element) {
        if (!this.field.isElementOfThisField(element)) {
            throw new ArithmeticException("Not an element of the finite field this polynomial is defined over.");
        }
        int[] resultCoeff = multWithElement(this.coefficients, element);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    public void multThisWithElement(int element) {
        if (!this.field.isElementOfThisField(element)) {
            throw new ArithmeticException("Not an element of the finite field this polynomial is defined over.");
        }
        this.coefficients = multWithElement(this.coefficients, element);
        computeDegree();
    }

    private int[] multWithElement(int[] a, int element) {
        int degree = computeDegree(a);
        if (degree == -1 || element == 0) {
            return new int[1];
        }
        if (element == 1) {
            return IntUtils.clone(a);
        }
        int[] result = new int[degree + 1];
        for (int i = degree; i >= 0; i--) {
            result[i] = this.field.mult(a[i], element);
        }
        return result;
    }

    public PolynomialGF2mSmallM multWithMonomial(int k) {
        int[] resultCoeff = multWithMonomial(this.coefficients, k);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    private static int[] multWithMonomial(int[] a, int k) {
        int d = computeDegree(a);
        if (d == -1) {
            return new int[1];
        }
        int[] result = new int[d + k + 1];
        System.arraycopy(a, 0, result, k, d + 1);
        return result;
    }

    public PolynomialGF2mSmallM[] div(PolynomialGF2mSmallM f) {
        int[][] resultCoeffs = div(this.coefficients, f.coefficients);
        return new PolynomialGF2mSmallM[]{new PolynomialGF2mSmallM(this.field, resultCoeffs[0]), new PolynomialGF2mSmallM(this.field, resultCoeffs[1])};
    }

    private int[][] div(int[] a, int[] f) {
        int df = computeDegree(f);
        int da = computeDegree(a) + 1;
        if (df == -1) {
            throw new ArithmeticException("Division by zero.");
        }
        int[][] result = {new int[1], new int[da]};
        int hc = headCoefficient(f);
        int hc2 = this.field.inverse(hc);
        result[0][0] = 0;
        System.arraycopy(a, 0, result[1], 0, result[1].length);
        while (df <= computeDegree(result[1])) {
            int[] coeff = {this.field.mult(headCoefficient(result[1]), hc2)};
            int[] q = multWithElement(f, coeff[0]);
            int n = computeDegree(result[1]) - df;
            int[] q2 = multWithMonomial(q, n);
            result[0] = add(multWithMonomial(coeff, n), result[0]);
            result[1] = add(q2, result[1]);
        }
        return result;
    }

    public PolynomialGF2mSmallM gcd(PolynomialGF2mSmallM f) {
        int[] resultCoeff = gcd(this.coefficients, f.coefficients);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    private int[] gcd(int[] f, int[] g) {
        int[] a = f;
        int[] b = g;
        if (computeDegree(a) == -1) {
            return b;
        }
        while (computeDegree(b) != -1) {
            int[] c = mod(a, b);
            a = new int[b.length];
            System.arraycopy(b, 0, a, 0, a.length);
            b = new int[c.length];
            System.arraycopy(c, 0, b, 0, b.length);
        }
        int coeff = this.field.inverse(headCoefficient(a));
        return multWithElement(a, coeff);
    }

    public PolynomialGF2mSmallM multiply(PolynomialGF2mSmallM factor) {
        int[] resultCoeff = multiply(this.coefficients, factor.coefficients);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    private int[] multiply(int[] a, int[] b) {
        int[] mult1;
        int[] mult2;
        int[] result;
        if (computeDegree(a) < computeDegree(b)) {
            mult1 = b;
            mult2 = a;
        } else {
            mult1 = a;
            mult2 = b;
        }
        int[] mult3 = normalForm(mult1);
        int[] mult4 = normalForm(mult2);
        if (mult4.length == 1) {
            return multWithElement(mult3, mult4[0]);
        }
        int d1 = mult3.length;
        int d2 = mult4.length;
        int[] iArr = new int[(d1 + d2) - 1];
        if (d2 != d1) {
            int[] res1 = new int[d2];
            int[] res2 = new int[d1 - d2];
            System.arraycopy(mult3, 0, res1, 0, res1.length);
            System.arraycopy(mult3, d2, res2, 0, res2.length);
            result = add(multiply(res1, mult4), multWithMonomial(multiply(res2, mult4), d2));
        } else {
            int d3 = (d1 + 1) >>> 1;
            int d = d1 - d3;
            int[] firstPartMult1 = new int[d3];
            int[] firstPartMult2 = new int[d3];
            int[] secondPartMult1 = new int[d];
            int[] secondPartMult2 = new int[d];
            System.arraycopy(mult3, 0, firstPartMult1, 0, firstPartMult1.length);
            System.arraycopy(mult3, d3, secondPartMult1, 0, secondPartMult1.length);
            System.arraycopy(mult4, 0, firstPartMult2, 0, firstPartMult2.length);
            System.arraycopy(mult4, d3, secondPartMult2, 0, secondPartMult2.length);
            int[] helpPoly1 = add(firstPartMult1, secondPartMult1);
            int[] helpPoly2 = add(firstPartMult2, secondPartMult2);
            int[] res3 = multiply(firstPartMult1, firstPartMult2);
            int[] res4 = multiply(helpPoly1, helpPoly2);
            int[] res5 = multiply(secondPartMult1, secondPartMult2);
            int[] result2 = add(add(add(res4, res3), res5), multWithMonomial(res5, d3));
            result = add(multWithMonomial(result2, d3), res3);
        }
        return result;
    }

    private boolean isIrreducible(int[] a) {
        if (a[0] == 0) {
            return false;
        }
        int d = computeDegree(a) >> 1;
        int[] u = {0, 1};
        int[] Y = {0, 1};
        int fieldDegree = this.field.getDegree();
        for (int i = 0; i < d; i++) {
            for (int j = fieldDegree - 1; j >= 0; j--) {
                u = modMultiply(u, u, a);
            }
            u = normalForm(u);
            int[] g = gcd(add(u, Y), a);
            if (computeDegree(g) != 0) {
                return false;
            }
        }
        return true;
    }

    public PolynomialGF2mSmallM mod(PolynomialGF2mSmallM f) {
        int[] resultCoeff = mod(this.coefficients, f.coefficients);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    private int[] mod(int[] a, int[] f) {
        int df = computeDegree(f);
        if (df == -1) {
            throw new ArithmeticException("Division by zero");
        }
        int[] result = new int[a.length];
        int hc = headCoefficient(f);
        int hc2 = this.field.inverse(hc);
        System.arraycopy(a, 0, result, 0, result.length);
        while (df <= computeDegree(result)) {
            int coeff = this.field.mult(headCoefficient(result), hc2);
            int[] q = multWithMonomial(f, computeDegree(result) - df);
            result = add(multWithElement(q, coeff), result);
        }
        return result;
    }

    public PolynomialGF2mSmallM modMultiply(PolynomialGF2mSmallM a, PolynomialGF2mSmallM b) {
        int[] resultCoeff = modMultiply(this.coefficients, a.coefficients, b.coefficients);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    public PolynomialGF2mSmallM modSquareMatrix(PolynomialGF2mSmallM[] matrix) {
        int length = matrix.length;
        int[] resultCoeff = new int[length];
        int[] thisSquare = new int[length];
        for (int i = 0; i < this.coefficients.length; i++) {
            thisSquare[i] = this.field.mult(this.coefficients[i], this.coefficients[i]);
        }
        for (int i2 = 0; i2 < length; i2++) {
            for (int j = 0; j < length; j++) {
                if (i2 < matrix[j].coefficients.length) {
                    int scalarTerm = this.field.mult(matrix[j].coefficients[i2], thisSquare[j]);
                    resultCoeff[i2] = this.field.add(resultCoeff[i2], scalarTerm);
                }
            }
        }
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    private int[] modMultiply(int[] a, int[] b, int[] g) {
        return mod(multiply(a, b), g);
    }

    public PolynomialGF2mSmallM modSquareRoot(PolynomialGF2mSmallM a) {
        int[] resultCoeff = IntUtils.clone(this.coefficients);
        int[] iArrModMultiply = modMultiply(resultCoeff, resultCoeff, a.coefficients);
        while (true) {
            int[] help = iArrModMultiply;
            if (!isEqual(help, this.coefficients)) {
                resultCoeff = normalForm(help);
                iArrModMultiply = modMultiply(resultCoeff, resultCoeff, a.coefficients);
            } else {
                return new PolynomialGF2mSmallM(this.field, resultCoeff);
            }
        }
    }

    public PolynomialGF2mSmallM modSquareRootMatrix(PolynomialGF2mSmallM[] matrix) {
        int length = matrix.length;
        int[] resultCoeff = new int[length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (i < matrix[j].coefficients.length && j < this.coefficients.length) {
                    int scalarTerm = this.field.mult(matrix[j].coefficients[i], this.coefficients[j]);
                    resultCoeff[i] = this.field.add(resultCoeff[i], scalarTerm);
                }
            }
        }
        for (int i2 = 0; i2 < length; i2++) {
            resultCoeff[i2] = this.field.sqRoot(resultCoeff[i2]);
        }
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    public PolynomialGF2mSmallM modDiv(PolynomialGF2mSmallM divisor, PolynomialGF2mSmallM modulus) {
        int[] resultCoeff = modDiv(this.coefficients, divisor.coefficients, modulus.coefficients);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    private int[] modDiv(int[] a, int[] b, int[] g) {
        int[] r0 = normalForm(g);
        int[] r1 = mod(b, g);
        int[] s0 = {0};
        int[] iArrMod = mod(a, g);
        while (true) {
            int[] s1 = iArrMod;
            if (computeDegree(r1) != -1) {
                int[][] q = div(r0, r1);
                r0 = normalForm(r1);
                r1 = normalForm(q[1]);
                int[] s2 = add(s0, modMultiply(q[0], s1, g));
                s0 = normalForm(s1);
                iArrMod = normalForm(s2);
            } else {
                int hc = headCoefficient(r0);
                return multWithElement(s0, this.field.inverse(hc));
            }
        }
    }

    public PolynomialGF2mSmallM modInverse(PolynomialGF2mSmallM a) {
        int[] unit = {1};
        int[] resultCoeff = modDiv(unit, this.coefficients, a.coefficients);
        return new PolynomialGF2mSmallM(this.field, resultCoeff);
    }

    public PolynomialGF2mSmallM[] modPolynomialToFracton(PolynomialGF2mSmallM g) {
        int dg = g.degree >> 1;
        int[] a0 = normalForm(g.coefficients);
        int[] a1 = mod(this.coefficients, g.coefficients);
        int[] b0 = {0};
        int[] iArr = {1};
        while (true) {
            int[] b1 = iArr;
            if (computeDegree(a1) > dg) {
                int[][] q = div(a0, a1);
                a0 = a1;
                a1 = q[1];
                int[] b2 = add(b0, modMultiply(q[0], b1, g.coefficients));
                b0 = b1;
                iArr = b2;
            } else {
                return new PolynomialGF2mSmallM[]{new PolynomialGF2mSmallM(this.field, a1), new PolynomialGF2mSmallM(this.field, b1)};
            }
        }
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof PolynomialGF2mSmallM)) {
            return false;
        }
        PolynomialGF2mSmallM p = (PolynomialGF2mSmallM) other;
        if (this.field.equals(p.field) && this.degree == p.degree && isEqual(this.coefficients, p.coefficients)) {
            return true;
        }
        return false;
    }

    private static boolean isEqual(int[] a, int[] b) {
        int da = computeDegree(a);
        int db = computeDegree(b);
        if (da != db) {
            return false;
        }
        for (int i = 0; i <= da; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hash = this.field.hashCode();
        for (int j = 0; j < this.coefficients.length; j++) {
            hash = (hash * 31) + this.coefficients[j];
        }
        return hash;
    }

    public String toString() {
        String str = " Polynomial over " + this.field.toString() + ": \n";
        for (int i = 0; i < this.coefficients.length; i++) {
            str = str + this.field.elementToStr(this.coefficients[i]) + "Y^" + i + Marker.ANY_NON_NULL_MARKER;
        }
        return str + PolicyIdFilter.OID_SEPARATOR;
    }

    private void computeDegree() {
        this.degree = this.coefficients.length - 1;
        while (this.degree >= 0 && this.coefficients[this.degree] == 0) {
            this.degree--;
        }
    }

    private static int computeDegree(int[] a) {
        int degree = a.length - 1;
        while (degree >= 0 && a[degree] == 0) {
            degree--;
        }
        return degree;
    }

    private static int[] normalForm(int[] a) {
        int d = computeDegree(a);
        if (d == -1) {
            return new int[1];
        }
        if (a.length == d + 1) {
            return IntUtils.clone(a);
        }
        int[] result = new int[d + 1];
        System.arraycopy(a, 0, result, 0, d + 1);
        return result;
    }
}
