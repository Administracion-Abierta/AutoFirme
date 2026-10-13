package org.bouncycastle.pqc.legacy.math.ntru.polynomial;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/TernaryPolynomial.class */
public interface TernaryPolynomial extends Polynomial {
    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
    IntegerPolynomial mult(IntegerPolynomial integerPolynomial);

    int[] getOnes();

    int[] getNegOnes();

    int size();

    void clear();
}
