package org.spongycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/GF2mField.class */
public class GF2mField {
    private int degree;
    private int polynomial;

    public GF2mField(int degree) {
        this.degree = 0;
        if (degree >= 32) {
            throw new IllegalArgumentException(" Error: the degree of field is too large ");
        }
        if (degree < 1) {
            throw new IllegalArgumentException(" Error: the degree of field is non-positive ");
        }
        this.degree = degree;
        this.polynomial = PolynomialRingGF2.getIrreduciblePolynomial(degree);
    }

    public GF2mField(int degree, int poly) {
        this.degree = 0;
        if (degree != PolynomialRingGF2.degree(poly)) {
            throw new IllegalArgumentException(" Error: the degree is not correct");
        }
        if (!PolynomialRingGF2.isIrreducible(poly)) {
            throw new IllegalArgumentException(" Error: given polynomial is reducible");
        }
        this.degree = degree;
        this.polynomial = poly;
    }

    public GF2mField(byte[] enc) {
        this.degree = 0;
        if (enc.length != 4) {
            throw new IllegalArgumentException("byte array is not an encoded finite field");
        }
        this.polynomial = LittleEndianConversions.OS2IP(enc);
        if (!PolynomialRingGF2.isIrreducible(this.polynomial)) {
            throw new IllegalArgumentException("byte array is not an encoded finite field");
        }
        this.degree = PolynomialRingGF2.degree(this.polynomial);
    }

    public GF2mField(GF2mField field) {
        this.degree = 0;
        this.degree = field.degree;
        this.polynomial = field.polynomial;
    }

    public int getDegree() {
        return this.degree;
    }

    public int getPolynomial() {
        return this.polynomial;
    }

    public byte[] getEncoded() {
        return LittleEndianConversions.I2OSP(this.polynomial);
    }

    public int add(int a, int b) {
        return a ^ b;
    }

    public int mult(int a, int b) {
        return PolynomialRingGF2.modMultiply(a, b, this.polynomial);
    }

    public int exp(int a, int k) {
        if (k == 0) {
            return 1;
        }
        if (a == 0) {
            return 0;
        }
        if (a == 1) {
            return 1;
        }
        int result = 1;
        if (k < 0) {
            a = inverse(a);
            k = -k;
        }
        while (k != 0) {
            if ((k & 1) == 1) {
                result = mult(result, a);
            }
            a = mult(a, a);
            k >>>= 1;
        }
        return result;
    }

    public int inverse(int a) {
        int d = (1 << this.degree) - 2;
        return exp(a, d);
    }

    public int sqRoot(int a) {
        for (int i = 1; i < this.degree; i++) {
            a = mult(a, a);
        }
        return a;
    }

    public int getRandomElement(SecureRandom sr) {
        int result = RandUtils.nextInt(sr, 1 << this.degree);
        return result;
    }

    public int getRandomNonZeroElement() {
        return getRandomNonZeroElement(new SecureRandom());
    }

    public int getRandomNonZeroElement(SecureRandom sr) {
        int count = 0;
        int result = RandUtils.nextInt(sr, 1 << this.degree);
        while (result == 0 && count < 1048576) {
            result = RandUtils.nextInt(sr, 1 << this.degree);
            count++;
        }
        if (count == 1048576) {
            result = 1;
        }
        return result;
    }

    public boolean isElementOfThisField(int e) {
        if (this.degree == 31) {
            return e >= 0;
        }
        return e >= 0 && e < (1 << this.degree);
    }

    public String elementToStr(int a) {
        String str;
        String s = "";
        for (int i = 0; i < this.degree; i++) {
            if ((((byte) a) & 1) == 0) {
                str = "0" + s;
            } else {
                str = "1" + s;
            }
            s = str;
            a >>>= 1;
        }
        return s;
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof GF2mField)) {
            return false;
        }
        GF2mField otherField = (GF2mField) other;
        if (this.degree == otherField.degree && this.polynomial == otherField.polynomial) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.polynomial;
    }

    public String toString() {
        String str = "Finite Field GF(2^" + this.degree + ") = GF(2)[X]/<" + polyToString(this.polynomial) + "> ";
        return str;
    }

    private static String polyToString(int p) {
        String str = "";
        if (p == 0) {
            str = "0";
        } else {
            byte b = (byte) (p & 1);
            if (b == 1) {
                str = "1";
            }
            int p2 = p >>> 1;
            int i = 1;
            while (p2 != 0) {
                byte b2 = (byte) (p2 & 1);
                if (b2 == 1) {
                    str = str + "+x^" + i;
                }
                p2 >>>= 1;
                i++;
            }
        }
        return str;
    }
}
