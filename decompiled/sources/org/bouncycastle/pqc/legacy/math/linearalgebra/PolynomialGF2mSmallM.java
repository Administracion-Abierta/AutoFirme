package org.bouncycastle.pqc.legacy.math.linearalgebra;

import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.security.SecureRandom;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/PolynomialGF2mSmallM.class */
public class PolynomialGF2mSmallM {
    private GF2mField field;
    private int degree;
    private int[] coefficients;
    public static final char RANDOM_IRREDUCIBLE_POLYNOMIAL = 'I';

    public PolynomialGF2mSmallM(GF2mField gF2mField) {
        this.field = gF2mField;
        this.degree = -1;
        this.coefficients = new int[1];
    }

    public PolynomialGF2mSmallM(GF2mField gF2mField, int i, char c, SecureRandom secureRandom) {
        this.field = gF2mField;
        switch (c) {
            case 'I':
                this.coefficients = createRandomIrreduciblePolynomial(i, secureRandom);
                computeDegree();
                return;
            default:
                throw new IllegalArgumentException(" Error: type " + c + " is not defined for GF2smallmPolynomial");
        }
    }

    private int[] createRandomIrreduciblePolynomial(int i, SecureRandom secureRandom) {
        int[] iArr = new int[i + 1];
        iArr[i] = 1;
        iArr[0] = this.field.getRandomNonZeroElement(secureRandom);
        for (int i2 = 1; i2 < i; i2++) {
            iArr[i2] = this.field.getRandomElement(secureRandom);
        }
        while (!isIrreducible(iArr)) {
            int iNextInt = RandUtils.nextInt(secureRandom, i);
            if (iNextInt == 0) {
                iArr[0] = this.field.getRandomNonZeroElement(secureRandom);
            } else {
                iArr[iNextInt] = this.field.getRandomElement(secureRandom);
            }
        }
        return iArr;
    }

    public PolynomialGF2mSmallM(GF2mField gF2mField, int i) {
        this.field = gF2mField;
        this.degree = i;
        this.coefficients = new int[i + 1];
        this.coefficients[i] = 1;
    }

    public PolynomialGF2mSmallM(GF2mField gF2mField, int[] iArr) {
        this.field = gF2mField;
        this.coefficients = normalForm(iArr);
        computeDegree();
    }

    public PolynomialGF2mSmallM(GF2mField gF2mField, byte[] bArr) {
        this.field = gF2mField;
        int i = 8;
        int i2 = 1;
        while (gF2mField.getDegree() > i) {
            i2++;
            i += 8;
        }
        if (bArr.length % i2 != 0) {
            throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m");
        }
        this.coefficients = new int[bArr.length / i2];
        int i3 = 0;
        for (int i4 = 0; i4 < this.coefficients.length; i4++) {
            for (int i5 = 0; i5 < i; i5 += 8) {
                int[] iArr = this.coefficients;
                int i6 = i4;
                int i7 = i3;
                i3++;
                iArr[i6] = iArr[i6] ^ ((bArr[i7] & 255) << i5);
            }
            if (!this.field.isElementOfThisField(this.coefficients[i4])) {
                throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m");
            }
        }
        if (this.coefficients.length != 1 && this.coefficients[this.coefficients.length - 1] == 0) {
            throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m");
        }
        computeDegree();
    }

    public PolynomialGF2mSmallM(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        this.field = polynomialGF2mSmallM.field;
        this.degree = polynomialGF2mSmallM.degree;
        this.coefficients = IntUtils.clone(polynomialGF2mSmallM.coefficients);
    }

    public PolynomialGF2mSmallM(GF2mVector gF2mVector) {
        this(gF2mVector.getField(), gF2mVector.getIntArrayForm());
    }

    public int getDegree() {
        int length = this.coefficients.length - 1;
        if (this.coefficients[length] == 0) {
            return -1;
        }
        return length;
    }

    public int getHeadCoefficient() {
        if (this.degree == -1) {
            return 0;
        }
        return this.coefficients[this.degree];
    }

    private static int headCoefficient(int[] iArr) {
        int iComputeDegree = computeDegree(iArr);
        if (iComputeDegree == -1) {
            return 0;
        }
        return iArr[iComputeDegree];
    }

    public int getCoefficient(int i) {
        if (i < 0 || i > this.degree) {
            return 0;
        }
        return this.coefficients[i];
    }

    public byte[] getEncoded() {
        int i = 8;
        int i2 = 1;
        while (this.field.getDegree() > i) {
            i2++;
            i += 8;
        }
        byte[] bArr = new byte[this.coefficients.length * i2];
        int i3 = 0;
        for (int i4 = 0; i4 < this.coefficients.length; i4++) {
            for (int i5 = 0; i5 < i; i5 += 8) {
                int i6 = i3;
                i3++;
                bArr[i6] = (byte) (this.coefficients[i4] >>> i5);
            }
        }
        return bArr;
    }

    public int evaluateAt(int i) {
        int iMult = this.coefficients[this.degree];
        for (int i2 = this.degree - 1; i2 >= 0; i2--) {
            iMult = this.field.mult(iMult, i) ^ this.coefficients[i2];
        }
        return iMult;
    }

    public PolynomialGF2mSmallM add(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, add(this.coefficients, polynomialGF2mSmallM.coefficients));
    }

    public void addToThis(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        this.coefficients = add(this.coefficients, polynomialGF2mSmallM.coefficients);
        computeDegree();
    }

    private int[] add(int[] iArr, int[] iArr2) {
        int[] iArr3;
        int[] iArr4;
        if (iArr.length < iArr2.length) {
            iArr3 = new int[iArr2.length];
            System.arraycopy(iArr2, 0, iArr3, 0, iArr2.length);
            iArr4 = iArr;
        } else {
            iArr3 = new int[iArr.length];
            System.arraycopy(iArr, 0, iArr3, 0, iArr.length);
            iArr4 = iArr2;
        }
        for (int length = iArr4.length - 1; length >= 0; length--) {
            iArr3[length] = this.field.add(iArr3[length], iArr4[length]);
        }
        return iArr3;
    }

    public PolynomialGF2mSmallM addMonomial(int i) {
        int[] iArr = new int[i + 1];
        iArr[i] = 1;
        return new PolynomialGF2mSmallM(this.field, add(this.coefficients, iArr));
    }

    public PolynomialGF2mSmallM multWithElement(int i) {
        if (!this.field.isElementOfThisField(i)) {
            throw new ArithmeticException("Not an element of the finite field this polynomial is defined over.");
        }
        return new PolynomialGF2mSmallM(this.field, multWithElement(this.coefficients, i));
    }

    public void multThisWithElement(int i) {
        if (!this.field.isElementOfThisField(i)) {
            throw new ArithmeticException("Not an element of the finite field this polynomial is defined over.");
        }
        this.coefficients = multWithElement(this.coefficients, i);
        computeDegree();
    }

    private int[] multWithElement(int[] iArr, int i) {
        int iComputeDegree = computeDegree(iArr);
        if (iComputeDegree == -1 || i == 0) {
            return new int[1];
        }
        if (i == 1) {
            return IntUtils.clone(iArr);
        }
        int[] iArr2 = new int[iComputeDegree + 1];
        for (int i2 = iComputeDegree; i2 >= 0; i2--) {
            iArr2[i2] = this.field.mult(iArr[i2], i);
        }
        return iArr2;
    }

    public PolynomialGF2mSmallM multWithMonomial(int i) {
        return new PolynomialGF2mSmallM(this.field, multWithMonomial(this.coefficients, i));
    }

    private static int[] multWithMonomial(int[] iArr, int i) {
        int iComputeDegree = computeDegree(iArr);
        if (iComputeDegree == -1) {
            return new int[1];
        }
        int[] iArr2 = new int[iComputeDegree + i + 1];
        System.arraycopy(iArr, 0, iArr2, i, iComputeDegree + 1);
        return iArr2;
    }

    public PolynomialGF2mSmallM[] div(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        int[][] iArrDiv = div(this.coefficients, polynomialGF2mSmallM.coefficients);
        return new PolynomialGF2mSmallM[]{new PolynomialGF2mSmallM(this.field, iArrDiv[0]), new PolynomialGF2mSmallM(this.field, iArrDiv[1])};
    }

    private int[][] div(int[] iArr, int[] iArr2) {
        int iComputeDegree = computeDegree(iArr2);
        int iComputeDegree2 = computeDegree(iArr) + 1;
        if (iComputeDegree == -1) {
            throw new ArithmeticException("Division by zero.");
        }
        int[][] iArr3 = {new int[1], new int[iComputeDegree2]};
        int iInverse = this.field.inverse(headCoefficient(iArr2));
        iArr3[0][0] = 0;
        System.arraycopy(iArr, 0, iArr3[1], 0, iArr3[1].length);
        while (iComputeDegree <= computeDegree(iArr3[1])) {
            int[] iArr4 = {this.field.mult(headCoefficient(iArr3[1]), iInverse)};
            int[] iArrMultWithElement = multWithElement(iArr2, iArr4[0]);
            int iComputeDegree3 = computeDegree(iArr3[1]) - iComputeDegree;
            int[] iArrMultWithMonomial = multWithMonomial(iArrMultWithElement, iComputeDegree3);
            iArr3[0] = add(multWithMonomial(iArr4, iComputeDegree3), iArr3[0]);
            iArr3[1] = add(iArrMultWithMonomial, iArr3[1]);
        }
        return iArr3;
    }

    public PolynomialGF2mSmallM gcd(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, gcd(this.coefficients, polynomialGF2mSmallM.coefficients));
    }

    private int[] gcd(int[] iArr, int[] iArr2) {
        int[] iArr3 = iArr;
        int[] iArr4 = iArr2;
        if (computeDegree(iArr3) == -1) {
            return iArr4;
        }
        while (computeDegree(iArr4) != -1) {
            int[] iArrMod = mod(iArr3, iArr4);
            iArr3 = new int[iArr4.length];
            System.arraycopy(iArr4, 0, iArr3, 0, iArr3.length);
            iArr4 = new int[iArrMod.length];
            System.arraycopy(iArrMod, 0, iArr4, 0, iArr4.length);
        }
        return multWithElement(iArr3, this.field.inverse(headCoefficient(iArr3)));
    }

    public PolynomialGF2mSmallM multiply(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, multiply(this.coefficients, polynomialGF2mSmallM.coefficients));
    }

    private int[] multiply(int[] iArr, int[] iArr2) {
        int[] iArr3;
        int[] iArr4;
        int[] iArrAdd;
        if (computeDegree(iArr) < computeDegree(iArr2)) {
            iArr3 = iArr2;
            iArr4 = iArr;
        } else {
            iArr3 = iArr;
            iArr4 = iArr2;
        }
        int[] iArrNormalForm = normalForm(iArr3);
        int[] iArrNormalForm2 = normalForm(iArr4);
        if (iArrNormalForm2.length == 1) {
            return multWithElement(iArrNormalForm, iArrNormalForm2[0]);
        }
        int length = iArrNormalForm.length;
        int length2 = iArrNormalForm2.length;
        int[] iArr5 = new int[(length + length2) - 1];
        if (length2 != length) {
            int[] iArr6 = new int[length2];
            int[] iArr7 = new int[length - length2];
            System.arraycopy(iArrNormalForm, 0, iArr6, 0, iArr6.length);
            System.arraycopy(iArrNormalForm, length2, iArr7, 0, iArr7.length);
            iArrAdd = add(multiply(iArr6, iArrNormalForm2), multWithMonomial(multiply(iArr7, iArrNormalForm2), length2));
        } else {
            int i = (length + 1) >>> 1;
            int i2 = length - i;
            int[] iArr8 = new int[i];
            int[] iArr9 = new int[i];
            int[] iArr10 = new int[i2];
            int[] iArr11 = new int[i2];
            System.arraycopy(iArrNormalForm, 0, iArr8, 0, iArr8.length);
            System.arraycopy(iArrNormalForm, i, iArr10, 0, iArr10.length);
            System.arraycopy(iArrNormalForm2, 0, iArr9, 0, iArr9.length);
            System.arraycopy(iArrNormalForm2, i, iArr11, 0, iArr11.length);
            int[] iArrAdd2 = add(iArr8, iArr10);
            int[] iArrAdd3 = add(iArr9, iArr11);
            int[] iArrMultiply = multiply(iArr8, iArr9);
            int[] iArrMultiply2 = multiply(iArrAdd2, iArrAdd3);
            int[] iArrMultiply3 = multiply(iArr10, iArr11);
            iArrAdd = add(multWithMonomial(add(add(add(iArrMultiply2, iArrMultiply), iArrMultiply3), multWithMonomial(iArrMultiply3, i)), i), iArrMultiply);
        }
        return iArrAdd;
    }

    private boolean isIrreducible(int[] iArr) {
        if (iArr[0] == 0) {
            return false;
        }
        int iComputeDegree = computeDegree(iArr) >> 1;
        int[] iArrNormalForm = {0, 1};
        int[] iArr2 = {0, 1};
        int degree = this.field.getDegree();
        for (int i = 0; i < iComputeDegree; i++) {
            for (int i2 = degree - 1; i2 >= 0; i2--) {
                iArrNormalForm = modMultiply(iArrNormalForm, iArrNormalForm, iArr);
            }
            iArrNormalForm = normalForm(iArrNormalForm);
            if (computeDegree(gcd(add(iArrNormalForm, iArr2), iArr)) != 0) {
                return false;
            }
        }
        return true;
    }

    public PolynomialGF2mSmallM mod(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, mod(this.coefficients, polynomialGF2mSmallM.coefficients));
    }

    private int[] mod(int[] iArr, int[] iArr2) {
        int iComputeDegree = computeDegree(iArr2);
        if (iComputeDegree == -1) {
            throw new ArithmeticException("Division by zero");
        }
        int[] iArrAdd = new int[iArr.length];
        int iInverse = this.field.inverse(headCoefficient(iArr2));
        System.arraycopy(iArr, 0, iArrAdd, 0, iArrAdd.length);
        while (iComputeDegree <= computeDegree(iArrAdd)) {
            iArrAdd = add(multWithElement(multWithMonomial(iArr2, computeDegree(iArrAdd) - iComputeDegree), this.field.mult(headCoefficient(iArrAdd), iInverse)), iArrAdd);
        }
        return iArrAdd;
    }

    public PolynomialGF2mSmallM modMultiply(PolynomialGF2mSmallM polynomialGF2mSmallM, PolynomialGF2mSmallM polynomialGF2mSmallM2) {
        return new PolynomialGF2mSmallM(this.field, modMultiply(this.coefficients, polynomialGF2mSmallM.coefficients, polynomialGF2mSmallM2.coefficients));
    }

    public PolynomialGF2mSmallM modSquareMatrix(PolynomialGF2mSmallM[] polynomialGF2mSmallMArr) {
        int length = polynomialGF2mSmallMArr.length;
        int[] iArr = new int[length];
        int[] iArr2 = new int[length];
        for (int i = 0; i < this.coefficients.length; i++) {
            iArr2[i] = this.field.mult(this.coefficients[i], this.coefficients[i]);
        }
        for (int i2 = 0; i2 < length; i2++) {
            for (int i3 = 0; i3 < length; i3++) {
                if (i2 < polynomialGF2mSmallMArr[i3].coefficients.length) {
                    iArr[i2] = this.field.add(iArr[i2], this.field.mult(polynomialGF2mSmallMArr[i3].coefficients[i2], iArr2[i3]));
                }
            }
        }
        return new PolynomialGF2mSmallM(this.field, iArr);
    }

    private int[] modMultiply(int[] iArr, int[] iArr2, int[] iArr3) {
        return mod(multiply(iArr, iArr2), iArr3);
    }

    public PolynomialGF2mSmallM modSquareRoot(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        int[] iArrClone = IntUtils.clone(this.coefficients);
        int[] iArrModMultiply = modMultiply(iArrClone, iArrClone, polynomialGF2mSmallM.coefficients);
        while (true) {
            int[] iArr = iArrModMultiply;
            if (isEqual(iArr, this.coefficients)) {
                return new PolynomialGF2mSmallM(this.field, iArrClone);
            }
            iArrClone = normalForm(iArr);
            iArrModMultiply = modMultiply(iArrClone, iArrClone, polynomialGF2mSmallM.coefficients);
        }
    }

    public PolynomialGF2mSmallM modSquareRootMatrix(PolynomialGF2mSmallM[] polynomialGF2mSmallMArr) {
        int length = polynomialGF2mSmallMArr.length;
        int[] iArr = new int[length];
        for (int i = 0; i < length; i++) {
            for (int i2 = 0; i2 < length; i2++) {
                if (i < polynomialGF2mSmallMArr[i2].coefficients.length && i2 < this.coefficients.length) {
                    iArr[i] = this.field.add(iArr[i], this.field.mult(polynomialGF2mSmallMArr[i2].coefficients[i], this.coefficients[i2]));
                }
            }
        }
        for (int i3 = 0; i3 < length; i3++) {
            iArr[i3] = this.field.sqRoot(iArr[i3]);
        }
        return new PolynomialGF2mSmallM(this.field, iArr);
    }

    public PolynomialGF2mSmallM modDiv(PolynomialGF2mSmallM polynomialGF2mSmallM, PolynomialGF2mSmallM polynomialGF2mSmallM2) {
        return new PolynomialGF2mSmallM(this.field, modDiv(this.coefficients, polynomialGF2mSmallM.coefficients, polynomialGF2mSmallM2.coefficients));
    }

    private int[] modDiv(int[] iArr, int[] iArr2, int[] iArr3) {
        int[] iArrNormalForm = normalForm(iArr3);
        int[] iArrMod = mod(iArr2, iArr3);
        int[] iArrNormalForm2 = {0};
        int[] iArrMod2 = mod(iArr, iArr3);
        while (true) {
            int[] iArr4 = iArrMod2;
            if (computeDegree(iArrMod) == -1) {
                return multWithElement(iArrNormalForm2, this.field.inverse(headCoefficient(iArrNormalForm)));
            }
            int[][] iArrDiv = div(iArrNormalForm, iArrMod);
            iArrNormalForm = normalForm(iArrMod);
            iArrMod = normalForm(iArrDiv[1]);
            int[] iArrAdd = add(iArrNormalForm2, modMultiply(iArrDiv[0], iArr4, iArr3));
            iArrNormalForm2 = normalForm(iArr4);
            iArrMod2 = normalForm(iArrAdd);
        }
    }

    public PolynomialGF2mSmallM modInverse(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, modDiv(new int[]{1}, this.coefficients, polynomialGF2mSmallM.coefficients));
    }

    public PolynomialGF2mSmallM[] modPolynomialToFracton(PolynomialGF2mSmallM polynomialGF2mSmallM) {
        int i = polynomialGF2mSmallM.degree >> 1;
        int[] iArrNormalForm = normalForm(polynomialGF2mSmallM.coefficients);
        int[] iArrMod = mod(this.coefficients, polynomialGF2mSmallM.coefficients);
        int[] iArr = {0};
        int[] iArr2 = {1};
        while (true) {
            int[] iArr3 = iArr2;
            if (computeDegree(iArrMod) <= i) {
                return new PolynomialGF2mSmallM[]{new PolynomialGF2mSmallM(this.field, iArrMod), new PolynomialGF2mSmallM(this.field, iArr3)};
            }
            int[][] iArrDiv = div(iArrNormalForm, iArrMod);
            iArrNormalForm = iArrMod;
            iArrMod = iArrDiv[1];
            int[] iArrAdd = add(iArr, modMultiply(iArrDiv[0], iArr3, polynomialGF2mSmallM.coefficients));
            iArr = iArr3;
            iArr2 = iArrAdd;
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PolynomialGF2mSmallM)) {
            return false;
        }
        PolynomialGF2mSmallM polynomialGF2mSmallM = (PolynomialGF2mSmallM) obj;
        return this.field.equals(polynomialGF2mSmallM.field) && this.degree == polynomialGF2mSmallM.degree && isEqual(this.coefficients, polynomialGF2mSmallM.coefficients);
    }

    private static boolean isEqual(int[] iArr, int[] iArr2) {
        int iComputeDegree = computeDegree(iArr);
        if (iComputeDegree != computeDegree(iArr2)) {
            return false;
        }
        for (int i = 0; i <= iComputeDegree; i++) {
            if (iArr[i] != iArr2[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int iHashCode = this.field.hashCode();
        for (int i = 0; i < this.coefficients.length; i++) {
            iHashCode = (iHashCode * 31) + this.coefficients[i];
        }
        return iHashCode;
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

    private static int computeDegree(int[] iArr) {
        int length = iArr.length - 1;
        while (length >= 0 && iArr[length] == 0) {
            length--;
        }
        return length;
    }

    private static int[] normalForm(int[] iArr) {
        int iComputeDegree = computeDegree(iArr);
        if (iComputeDegree == -1) {
            return new int[1];
        }
        if (iArr.length == iComputeDegree + 1) {
            return IntUtils.clone(iArr);
        }
        int[] iArr2 = new int[iComputeDegree + 1];
        System.arraycopy(iArr, 0, iArr2, 0, iComputeDegree + 1);
        return iArr2;
    }
}
